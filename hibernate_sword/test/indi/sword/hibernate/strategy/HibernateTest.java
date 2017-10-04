package indi.sword.hibernate.strategy;

import java.util.List;
import java.util.Set;

import indi.sword.hibernate.BaseTest;
import org.hibernate.Hibernate;
import org.junit.Test;

public class HibernateTest extends BaseTest{

	@Test
	public void testMany2OneStrategy(){
//		Order order = (Order) session.get(Order.class, 1);
//		System.out.println(order.getCustomer().getCustomerName()); 
		
		List<Order> orders = session.createQuery("FROM Order o").list();
		for(Order order: orders){
			if(order.getCustomer() != null){
				System.out.println(order.getCustomer().getCustomerName()); 
			}
		}
		
		//1. lazy 取值为 proxy 和 false 分别代表对应对应的属性采用延迟检索和立即检索
		//2. fetch 取值为 join, 表示使用迫切左外连接的方式初始化 n 关联的 1 的一端的属性
		//忽略 lazy 属性. 
		//3. batch-size, 该属性需要设置在 1 那一端的 class 元素中: 
		//<class name="Customer" table="CUSTOMERS" lazy="true" batch-size="5">
		//作用: 一次初始化 1 的这一段代理对象的个数. 
	}
	
	@Test
	public void testSetFetch2(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println("-------------------------");
		System.out.println(customer.getOrders().size()); 
	}
	
	//set 集合的 fetch 属性: 确定初始化 orders 集合的方式.
	//1. 默认值为 select. 通过正常的方式来初始化 set 元素
	//2. 可以取值为 subselect. 通过子查询的方式来初始化所有的 set 集合. 子查询
	//作为 where 子句的 in 的条件出现, 子查询查询所有 1 的一端的 ID. 此时 lazy 有效.
	//但 batch-size 失效.
	//3. 若取值为 join. 则
	//3.1 在加载 1 的一端的对象时, 使用迫切左外连接(使用左外链接进行查询, 且把集合属性进行初始化)的方式检索 n 的一端的集合属性
	//3.2 忽略 lazy 属性.
	//3.3 HQL 查询忽略 fetch=join 的取值
	@Test
	public void testSetBatchSize(){
		List<Customer> customers = session.createQuery("FROM Customer").list();
		System.out.println("-------------------------------");
		System.out.println(customers.size());
		System.out.println("-------------------------------");
		for(Customer customer: customers){
			if(customer.getOrders() != null)
				System.out.println(customer.getOrders().size());
		}
		
	}

	//set 元素的 batch-size 属性: 设定一次初始化 set 集合的数量.
	@Test
	public void testOne2ManyLevelStrategy(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());

		System.out.println("--------------------------");
		System.out.println(customer.getOrders().size()); // 这里就已经去发select语句了
		System.out.println("--------------------------");
		Order order = new Order();
		order.setOrderId(1);
		System.out.println(customer.getOrders().contains(order));

		Set<Order> set = customer.getOrders();
		set.stream().map(o -> o.getOrderName()).forEach(System.out::println);

	}

	// 测试initialize方法，程序手动把 lazy=true 变为 false
	// 把未初始化的代理对象，从session缓存中加载，假如session关了的话 然后变成一个完整的游离对象
	@Test
	public void testHibernateInitialize(){
		Customer customer = (Customer) session.load(Customer.class, 1); // 注意，这里是load
		System.out.println("------------------------");
		// initialize 这个方法是为了避免懒加载，然后对象又没有初始化，然后session.clear(),session.close() ，待会custom.getXXX()的时候抛异常
		// could not initialize proxy - no Session    ↓↓↓↓↓↓↓↓↓
//		Hibernate.initialize(customer);
		session.clear();
		session.close();
		System.out.println("------------------------");
		System.out.println(customer.getCustomerName());
	}

	/*
	    增强延迟检索(lazy 属性为 extra): 与 lazy=“true” 类似.
	    主要区别是增强延迟检索策略能进一步延迟 Customer 对象的 orders 集合代理实例的初始化时机.

	 	如果Customer.hbm.xml的set中
	 	Lazy=“true”，那么当程序第一次访问 order 属性的 size(), contains() 和 isEmpty() 方法时，
	 	会去加载Order的实例，但是当
	 	Lazy=“false”，那么 Hibernate 不会初始化 orders 集合类的实例, 仅通过特定的 select 语句查询必要的信息, 不会检索所有的 Order 对象，
	 	比如select count(*)
	  */
	@Test
	public void testLazyExtra(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println("------------------------");
		System.out.println(customer.getOrders().isEmpty()); // lazy = "true" 与 lazy = "extra"

	}


	//---------------set 的 lazy 属性------------------
	//1. 1-n 或 n-n 的集合属性默认使用懒加载检索策略.
	//2. 可以通过设置 set 的 lazy 属性来修改默认的检索策略. 默认为 true
	//并不建议设置为  false.
	//3. lazy 还可以设置为 extra. 增强的延迟检索. 该取值会尽可能的延迟集合初始化的时机!
	@Test
	public void testClassLevelStrategy(){
		Customer customer = (Customer) session.load(Customer.class, 1);
		System.out.println(customer.getClass()); 
		System.out.println(customer.getCustomerId());
		System.out.println("----------------------");
		System.out.println(customer.getCustomerName()); 
	}

	@Test
	public void testSave(){
		Order order1 = new Order();
		Order order2 = new Order();

		order1.setOrderName("order-5");
		order2.setOrderName("order-6");

		Customer customer = new Customer();
		customer.setCustomerName("customer-3");

		customer.getOrders().add(order1);
		customer.getOrders().add(order2);

		order1.setCustomer(customer);
		order2.setCustomer(customer);

		System.out.println("-------------");
		session.save(order1);
		System.out.println("-------------");
		session.save(order2);
		System.out.println("-------------");
		session.save(customer);
		System.out.println("-------------");
	}

}
