package indi.sword.hibernate.query;

import indi.sword.hibernate.BaseTest;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.junit.Test;

import java.util.*;


/**
 * @Description
 * @Author rd_jianbin_lin
 * @Date 13:31 2017/10/4
 */
/*
    HQL(Hibernate Query Language) 是面向对象的查询语言, 它和 SQL 查询语言有些相似. 在 Hibernate 提供的各种检索方式中, HQL 是使用最广的一种检索方式. 它有如下功能:
    在查询语句中设定各种查询条件 。
    支持投影查询, 即仅检索出对象的部分属性 。
    支持分页查询  支持连接查询 。
    支持分组查询, 允许使用 HAVING 和 GROUP BY 关键字 。
    提供内置聚集函数, 如 sum(), min() 和 max() 。
    支持子查询  支持动态绑定参数 。
    能够调用 用户定义的 SQL 函数或标准的 SQL 函数 。

    HQL 检索方式包括以下步骤:
    通过 Session 的 createQuery() 方法创建一个 Query 对象, 它包括一个 HQL 查询语句. HQL 查询语句中可以包含命名参数
    动态绑定参数
    Qurey 接口支持方法链编程风格, 它的 setXxx() 方法返回自身实例, 而不是 void 类型
 */
/*
    HQL 查询语句是面向对象的, Hibernate 负责解析 HQL 查询语句, 然后根据对象-关系映射文件中的映射信息, 把 HQL 查询语句翻译成相应的 SQL 语句.
    HQL 查询语句中的主体是域模型中的类及类的属性 。
    SQL 查询语句是与关系数据库绑定在一起的. SQL 查询语句中的主体是数据库表及表的字段.
 */
public class HibernateTest extends BaseTest{

    @Test
    public void save(){
        Department department = new Department();
        department.setName("dept-1");

        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee1.setName("employee-1");
        employee1.setEmail("1mail.com");
        employee1.setSalary(1111.11f);

        employee2.setName("employee-2");
        employee2.setEmail("2mail.com");
        employee2.setSalary(2222.22f);

        department.getEmps().add(employee1);
        department.getEmps().add(employee2);

        employee1.setDept(department);
        employee2.setDept(department);

        session.save(department);
        session.save(employee1);
        session.save(employee2);

    }

    /*
        Hibernate 的参数绑定机制依赖于 JDBC API 中的 PreparedStatement 的预定义 SQL 语句功能.
        HQL 的参数绑定由两种形式:
        按参数名字绑定: 在 HQL 查询语句中定义命名参数, 命名参数以 “:” 开头.
        按参数位置绑定: 在 HQL 查询语句中用 “?” 来定义参数位置 .
        相关方法:
        setEntity(): 把参数与一个持久化类绑定 。
        setParameter(): 绑定任意类型的参数. 该方法的第三个参数显式指定 Hibernate 映射类型

        HQL 采用 ORDER BY 关键字对查询结果排序
     */
    @Test
    public void testHQL(){
        //1. 创建 Query 对象
        //基于位置的参数.
        String hql = "FROM Employee e WHERE e.salary > ? AND e.email LIKE ? AND e.dept = ? "
                + "ORDER BY e.salary";
        Query query = session.createQuery(hql);

        //2. 绑定参数
        //Query 对象调用 setXxx 方法支持方法链的编程风格.
        Department dept = new Department();
        dept.setId(1);
        query.setFloat(0, 0f)
                .setString(1, "%mail%")
                .setEntity(2, dept);

        //3. 执行查询
        List<Employee> emps = query.list();
        System.out.println(emps.size());

//        long count = ((List<Employee>)query.list()).stream().count();
//        System.out.println(count);

//        Optional<Integer> optional = ((List<Employee>)query.list()).stream().map(e -> 1 ).reduce(Integer::sum);
//        System.out.println(optional.get());

    }

    @Test
    public void testHQLNameParameter(){

        //1. 创建 Query 对象
        //基于命名参数.
        String hql = "FROM Employee e WHERE e.salary > :sal AND e.email LIKE :email";
        Query query = session.createQuery(hql);

        //2. 绑定参数
        query.setFloat("sal",0f)
                .setString("email","%mail%");
        //3. 执行查询
        List<Employee> emps = query.list();
        System.out.println(emps.size());
    }

    /*
        setFirstResult(int firstResult): 设定从哪一个对象开始检索, 参数 firstResult 表示这个对象在查询结果中的索引位置, 索引位置的起始值为 0. 默认情况下, Query 从查询结果中的第一个对象开始检索 。
        setMaxResults(int maxResults): 设定一次最多检索出的对象的数目. 在默认情况下, Query 和 Criteria 接口检索出查询结果中所有的对象。
     */
    @Test
    public void testPageQuery(){
        String hql = "From Employee";
        Query query = session.createQuery(hql);

        int pageNo = 2;
        int pageSize = 2;

        // 就找一页就够了，找第二页的内容，每页两条内容
        ((List<Employee>)query.setFirstResult((pageNo -1 ) * pageSize)
                .setMaxResults(pageSize).list()).stream().forEach(System.out::println);

    }

    /*
        Hibernate 允许在映射文件中定义字符串形式的查询语句.
        <query> 元素用于定义一个 HQL 查询语句, 它和 <class> 元素并列.
        在程序中通过 Session 的 getNamedQuery() 方法获取查询语句对应的 Query 对象.
     */
    @Test
    public void testNameQuery(){
        // 这个是预先写好在xml里面的query，可以根据名字直接拿来用
        Query query = session.getNamedQuery("salaryEmps");
        List<Employee> emps = query.setFloat("minSal", 0f)
                .setFloat("maxSal", 10000)
                .list();

        System.out.println(emps.size());
    }

    /*
        投影查询: 查询结果仅包含实体的部分属性. 通过 SELECT 关键字实现.
        Query 的 list() 方法返回的集合中包含的是数组类型的元素, 每个对象数组代表查询结果的一条记录 。
        可以在持久化类中定义一个对象的构造器来包装投影查询返回的记录, 使程序代码能完全运用面向对象的语义来访问查询结果集。

        可以通过 DISTINCT 关键字来保证查询结果不会返回重复元素
     */
    @Test
    public void testFieldQuery(){
        String hql = "SELECT e.email, e.salary, e.dept FROM Employee e WHERE e.dept = :dept";
        Query query = session.createQuery(hql);

        Department dept = new Department();
        dept.setId(1);
        List<Object[]> result = query.setEntity("dept", dept)
                .list();

//        for(Object [] objs: result){
//            System.out.println(Arrays.asList(objs));
//        }
        result.stream().map(Arrays::asList).forEach(System.out::println);
//        tempEntities.forEach(System.out::println);
        }

    @Test
    public void testFieldQuery2(){
        // 包装一下下
        String hql = "SELECT new Employee(e.email, e.salary, e.dept) "
                + "FROM Employee e "
                + "WHERE e.dept = :dept";
        Query query = session.createQuery(hql);

        Department dept = new Department();
        dept.setId(80);
        List<Employee> result = query.setEntity("dept", dept)
                .list();

        for(Employee emp: result){
            System.out.println(emp.getId() + ", " + emp.getEmail()
                    + ", " + emp.getSalary() + ", " + emp.getDept());
        }
    }

    @Test
    public void testGroupBy(){
        //查询每个部门的最低工资和最高工资
        String hql = "SELECT min(e.salary), max(e.salary) "
                + "FROM Employee e "
                + "GROUP BY e.dept "
                + "HAVING min(salary) > :minSal";

        Query query = session.createQuery(hql)
                .setFloat("minSal", 1000);

        List<Object []> result = query.list();
//        for(Object [] objs: result){
//            System.out.println(Arrays.asList(objs));
//        }
        result.stream().map(Arrays::asList).forEach(System.out::println);
    }

    /*
        迫切左外连接:
        LEFT JOIN FETCH 关键字表示迫切左外连接检索策略.
        list() 方法返回的集合中存放实体对象的引用, 每个 Department 对象关联的 Employee  集合都被初始化, 存放所有关联的 Employee 的实体对象.
        查询结果中可能会包含重复元素, 可以通过一个 HashSet 来过滤重复元素
        左外连接:
        LEFT JOIN 关键字表示左外连接查询.
        list() 方法返回的集合中存放的是对象数组类型 .
        根据配置文件来决定 Employee 集合的检索策略.
        如果希望 list() 方法返回的集合中仅包含 Department 对象, 可以在HQL 查询语句中使用 SELECT 关键字
     */
    // 不迫切，类似于lazy=true
    @Test
    public void testLeftJoin(){
        String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.emps";
        Query query = session.createQuery(hql);

        List<Department> depts = query.list();
        System.out.println(depts.size());
        System.out.println("------------------------------------------------");
        for(Department dept: depts){
            System.out.println(dept.getName() + ", " + dept.getEmps().size());
        }

//		List<Object []> result = query.list();
//		result = new ArrayList<>(new LinkedHashSet<>(result));
//		System.out.println(result);
//
//		for(Object [] objs: result){
//			System.out.println(Arrays.asList(objs));
//		}
    }

    // 迫切，类似于lazy=false
    @Test
    public void testLeftJoinFetch(){
		String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.emps";
//        String hql = "FROM Department d INNER JOIN FETCH d.emps";
        Query query = session.createQuery(hql);

        List<Department> depts = query.list();
        depts = new ArrayList<>(new LinkedHashSet(depts));
        System.out.println("------------------------------------------------");
        System.out.println(depts.size());

        for(Department dept: depts){
            System.out.println(dept.getName() + "-" + dept.getEmps().size());
        }
    }

    /*
    迫切内连接:
    INNER JOIN FETCH 关键字表示迫切内连接, 也可以省略 INNER 关键字 .
    list() 方法返回的集合中存放 Department 对象的引用, 每个 Department 对象的 Employee 集合都被初始化, 存放所有关联的 Employee 对象 .

    内连接:
    INNER JOIN 关键字表示内连接, 也可以省略 INNER 关键字 。
    list() 方法的集合中存放的每个元素对应查询结果的一条记录, 每个元素都是对象数组类型 。
    如果希望 list() 方法的返回的集合仅包含 Department  对象, 可以在 HQL 查询语句中使用 SELECT 关键字 。
    与左外连接的区别：不返回与左表条件不符合的记录 。
     */
    @Test
    public void testInnerJoinFetch(){
        String hql = "SELECT e FROM Employee e INNER JOIN Fetch e.dept";
        Query query = session.createQuery(hql);

        List<Employee> emps = query.list();
        System.out.println("-----------------------------------------");
        System.out.println(emps.size());

        for(Employee emp: emps){
            System.out.println(emp.getName() + ", " + emp.getDept().getName());
        }
    }

    @Test
    public void testInnerJoin(){
        String hql = "SELECT e FROM Employee e INNER JOIN e.dept";
        Query query = session.createQuery(hql);

        List<Employee> emps = query.list();
        System.out.println("-----------------------------------------");
        System.out.println(emps.size());

        for(Employee emp: emps){
            System.out.println(emp.getName() + ", " + emp.getDept().getName());
        }
    }

    /*
       QBC 查询就是通过使用 Hibernate 提供的 Query By Criteria API 来查询对象，这种 API 封装了 SQL 语句的动态拼装，对查询提供了更加面向对象的功能接口

     */

    @Test
    public void testQBC(){
        //1. 创建一个 Criteria 对象
        Criteria criteria = session.createCriteria(Employee.class);

        //2. 添加查询条件: 在 QBC 中查询条件使用 Criterion 来表示
        //Criterion 可以通过 Restrictions 的静态方法得到
        criteria.add(Restrictions.eq("id", 1));
        criteria.add(Restrictions.gt("salary", 0f));

        //3. 执行查询
        Employee employee = (Employee) criteria.uniqueResult();
        System.out.println(employee);
    }

    @Test
    public void testQBC2(){
        Criteria criteria = session.createCriteria(Employee.class);

        //1. AND: 使用 Conjunction 表示
        //Conjunction 本身就是一个 Criterion 对象
        //且其中还可以添加 Criterion 对象
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.like("name", "a", MatchMode.ANYWHERE));
        Department dept = new Department();
        dept.setId(80);
        conjunction.add(Restrictions.eq("dept", dept));
        System.out.println(conjunction);

        //2. OR
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.ge("salary", 6000F));
        disjunction.add(Restrictions.isNull("email"));

        //另一个AND
        criteria.add(disjunction);
        criteria.add(conjunction);

        criteria.list();
    }

    @Test
    public void testQBC3(){
        Criteria criteria = session.createCriteria(Employee.class);

        //统计查询: 使用 Projection 来表示: 可以由 Projections 的静态方法得到
        criteria.setProjection(Projections.max("salary"));

        System.out.println(criteria.uniqueResult());
    }


    @Test
    public void testQBC4(){
        Criteria criteria = session.createCriteria(Employee.class);

        //1. 添加排序
        criteria.addOrder(Order.asc("salary"));
        criteria.addOrder(Order.desc("email"));

        //2. 添加翻页方法
        int pageSize = 5;
        int pageNo = 3;
        criteria.setFirstResult((pageNo - 1) * pageSize)
                .setMaxResults(pageSize)
                .list();
    }

    @Test
    public void testNativeSQL(){
        String sql = "INSERT INTO t_query_department VALUES(?, ?)";
        Query query = session.createSQLQuery(sql);

        query.setInteger(0, 280)
                .setString(1, "aaaaa")
                .executeUpdate();
    }
    @Test
    public void testHQLUpdate(){
        String hql = "DELETE FROM Department d WHERE d.id = :id";
        session.createQuery(hql).setInteger("id", 1)
                .executeUpdate();
    }

}
