package indi.sword.hibernate.query;

import indi.sword.hibernate.BaseTest;
import indi.sword.hibernate.helloworld.News;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.jdbc.Work;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
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


    // 二级缓存 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    @Test
    public void testHibernateSecondLevelCache(){
        Employee employee = (Employee) session.get(Employee.class, 1);
        System.out.println(employee.getName());

        transaction.commit();
        session.close();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        /*
            <cache name="indi.sword.hibernate.query.Employee"
            maxElementsInMemory="1"
            eternal="false"
            timeToIdleSeconds="3" //这里设置小一点，待会就会继续去发select了
            timeToLiveSeconds="3"
            overflowToDisk="true"
            />
         */
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------");
        // 不会再发select语句，因为Employee不是存在session里面，而是存在sessionfactory里面的
        Employee employee2 = (Employee) session.get(Employee.class, 1);
        System.out.println(employee2.getName());
    }

    @Test
    public void testCollectionSecondLevelCache(){
        Department dept = (Department) session.get(Department.class, 1);
        System.out.println(dept.getName());
        System.out.println(dept.getEmps().size());

        transaction.commit();
        session.close();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        /*
             <cache name="indi.sword.hibernate.query.Department.emps"
            maxElementsInMemory="1000"
            eternal="true"  // 永久
            timeToIdleSeconds="0"
            timeToLiveSeconds="0"
            overflowToDisk="false"
            />
         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------");
        Department dept2 = (Department) session.get(Department.class, 1);
        System.out.println(dept2.getName());
        System.out.println(dept2.getEmps().size());
    }

    @Test
    public void testQueryCache(){
        Query query = session.createQuery("FROM Employee");
        query.setCacheable(true);

        List<Employee> emps = query.list();
        System.out.println(emps.size());
        System.out.println("--------------------------------------------------");
        emps = query.list();
        System.out.println(emps.size());

        Criteria criteria = session.createCriteria(Employee.class);
        criteria.setCacheable(true);
    }

    @Test
    public void testUpdateTimeStampCache(){
        Query query = session.createQuery("FROM Employee");
        query.setCacheable(true);

        List<Employee> emps = query.list();
        System.out.println(emps.size());

        System.out.println("-------------------------------------");
        Employee employee = (Employee) session.get(Employee.class, 1);
        employee.setSalary(30000);
        System.out.println("-------------------------------------");
        emps = query.list();
        System.out.println(emps.size());
    }

    /*
        Query 接口的 iterator() 方法同 list() 一样也能执行查询操作
        list() 方法执行的 SQL 语句包含实体类对应的数据表的所有字段
        Iterator() 方法执行的SQL 语句中仅包含实体类对应的数据表的 ID 字段

        当遍历访问结果集时, 该方法先到 Session 缓存及二级缓存中查看是否存在特定 OID 的对象,
        如果存在, 就直接返回该对象, 如果不存在该对象就通过相应的 SQL Select 语句到数据库中加载特定的实体对象

        多数情况下, 应考虑使用 list() 方法执行查询操作. iterator() 方法仅在满足以下条件的场合, 可以稍微提高查询性能:
        要查询的数据表中包含大量字段
        启用了二级缓存, 且二级缓存中可能已经包含了待查询的对象

     */
    @Test
    public void testQueryIterate() {

        Query query = session.createQuery("FROM Employee e WHERE e.dept.id = 1");
        System.out.println("-----------------------");

        Iterator<Employee> empIt = query.iterate();
        System.out.println("------------------------");
        while (empIt.hasNext()) {
            System.out.println(empIt.next().getId());
        }
    }

    @Test
    public void testQueryIterate2() {
        // 启用了二级缓存, 且二级缓存中可能已经包含了待查询的对象
        Department dept = (Department) session.get(Department.class, 1);
        System.out.println(dept.getName());
        System.out.println(dept.getEmps().size());

        Query query = session.createQuery("FROM Employee e WHERE e.dept.id = 1");
        System.out.println("-----------------------");
//		List<Employee> emps = query.list();
//		System.out.println(emps.size());

        Iterator<Employee> empIt = query.iterate();
        System.out.println("------------------------");
        /*
            当遍历访问结果集时, 该方法先到 Session 缓存及二级缓存中查看是否存在特定 OID 的对象,
            如果存在, 就直接返回该对象, 如果不存在该对象就通过相应的 SQL Select 语句到数据库中加载特定的实体对象
         */
        while (empIt.hasNext()) {
            System.out.println(empIt.next().getName());
        }
    }

    @Test
    public void testManageSession(){

        //获取 Session
        //开启事务
        Session session = HibernateUtils.getInstance().getSession();
        System.out.println("-->" + session.hashCode());
        Transaction transaction = session.beginTransaction();

        DepartmentDao departmentDao = new DepartmentDao();

        Department dept = new Department();
        dept.setName("Dept");

        departmentDao.save(dept);
        departmentDao.save(dept);
        departmentDao.save(dept);

        //若 Session 是由 thread 来管理的, 则在提交或回滚事务时, 已经关闭 Session 了.
        transaction.commit();
        /*
            若 threadA 再次调用 SessionFactory 对象的 getCurrentSession() 方法时,
             该方法会又创建一个新的 Session(sessionB) 对象, 把该对象与 threadA 绑定, 并将 sessionB 返回

         */
        session = HibernateUtils.getInstance().getSession();
        System.out.println("-->" + session.hashCode());
        System.out.println(session.isOpen());
    }

    /*
        通过 Session 来进行批量操作
        Session 的 save() 及 update() 方法都会把处理的对象存放在自己的缓存中.
        如果通过一个 Session 对象来处理大量持久化对象, 应该及时从缓存中清空已经处理完毕并且不会再访问的对象.
        具体的做法是在处理完一个对象或小批量对象后, 立即调用 flush() 方法刷新缓存, 然后在调用 clear() 方法清空缓存

        通过 Session 来进行处理操作会受到以下约束
        需要在  Hibernate 配置文件中设置 JDBC 单次批量处理的数目, 应保证每次向数据库发送的批量的 SQL 语句数目与 batch_size 属性一致 (配置在 *.hbm.xml 里面)
        若对象采用 “identity” 标识符生成器, 则 Hibernate 无法在 JDBC 层进行批量插入操作.
        进行批量操作时, 建议关闭 Hibernate 的二级缓存

        注意: HQL 只支持 INSERT INTO … SELECT 形式的插入语句, 但不支持 INSERT INTO … VALUES 形式的插入语句. 所以使用 HQL 不能进行批量插入操作

        形式上看，StatelessSession与session的用法类似。StatelessSession与session相比，有以下区别:
        StatelessSession没有缓存，通过StatelessSession来加载、保存或更新后的对象处于游离状态。
        StatelessSession不会与Hibernate的第二级缓存交互。
        当调用StatelessSession的save()、update()或delete()方法时，这些方法会立即执行相应的SQL语句，而不会仅计划执行一条SQL语句
        StatelessSession不会进行脏检查，因此修改了Customer对象属性后，
        还需要调用StatelessSession的update()方法来更新数据库中数据。StatelessSession不会对关联的对象进行任何级联操作
        通过同一个StatelessSession对象两次加载OID为1的Customer对象，得到的两个对象内存地址不同。
        StatelessSession所做的操作可以被Interceptor拦截器捕获到，但是会被Hibernate的事件处理系统忽略掉。

     */
    @Test
    public void testBatchSave(){
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                //通过 JDBC 原生的 API 进行操作, 效率最高, 速度最快!
                News news = null;
                for (int i = 0 ; i < 1000;i++){
                    news = new News();
                    news.setTitle("--" + i);
                    session.save(news);

                    if((i + 1) % 20 == 0){
                        session.flush();
                        session.clear();
                    }
                }
            }
        });
    }

    /*
        批量更新: 在进行批量更新时, 如果一下子把所有对象都加载到 Session 缓存, 然后再缓存中一一更新, 显然是不可取的
        使用可滚动的结果集 org.hibernate.ScrollableResults, 该对象中实际上并不包含任何对象,
        只包含用于在线定位记录的游标. 只有当程序遍历访问 ScrollableResults 对象的特定元素时,
        它才会到数据库中加载相应的对象.

         org.hibernate.ScrollableResults 对象由 Query 的 scroll 方法返回
     */
    @Test
    public void testBatchUpdate(){
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                ScrollableResults sr = session.createQuery("from News").scroll();

                int count = 0;
                while(sr.next()){
                    News n = (News)sr.get(0);
                    n.setTitle(n.getTitle() + "*****");
                }
                if(((count++) + 1) % 100 == 0){
                    session.flush();
                    session.clear();
                }

            }
        });
    }
}
