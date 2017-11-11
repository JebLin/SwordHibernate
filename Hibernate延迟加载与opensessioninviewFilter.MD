# Hibernate延迟加载与opensessioninviewFilter

<hr />


hibernate延迟加载：
一个person对应多个school，使用hibernate处理关联关系：

T_PERSON表：
| id | name | age |
| --- | --- | --- |
| 1 | person1 | 11 |

T_SCHOOL表：
| id | schoolname | personId |
| --- | --- | --- |
| 1 | school1 | 1 |
| 2 | school2 | 1 |
| 3 | school3 | 1 |

person类：
```
public class Person {
   public Person(){}
   private int id;
   private String name;
   private int age;
   private Set<School> schools = new HashSet<School>();
   public int getId() {
      return id;
   }
   public void setId(int id) {
      this.id = id;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public int getAge() {
      return age;
   }
   public void setAge(int age) {
      this.age = age;
   }
   public Set<School> getSchools() {
      return schools;
   }
   public void setSchools(Set<School> schools) {
      this.schools = schools;
   }
}
```
school 类：
```
public class School {
   public School(){}
   private int id;
   private String schoolName;
   private int personId;
   public int getId() {
      return id;
   }
   public void setId(int id) {
      this.id = id;
   }
   public String getSchoolName() {
      return schoolName;
   }
   public void setSchoolName(String schoolName) {
      this.schoolName = schoolName;
   }
   public int getPersonId() {
      return personId;
   }
   public void setPersonId(int personId) {
      this.personId = personId;
   }
}

```
person类的映射文件为：
```
<hibernate-mapping>
    <class name="com.po.Person" table="T_PERSON">
        <id name="id" column="id" type="java.lang.Integer">
            <generator class="assigned"/>
        </id>
        <property name="name" column="name" type="java.lang.String" length="20"/>
        <property name="age" column="age" type="java.lang.Integer"/>
        <set name="schools" table="T_SCHOOL" inverse="true" lazy="false">
            <key column="personId"/>
            <one-to-many class="com.po.School"/>
        </set>
    </class>
</hibernate-mapping>
```

school类的映射文件为：
```
<hibernate-mapping>
    <class name="com.po.School" table="T_SCHOOL">
        <id name="id" column="id" type="java.lang.Integer">
            <generator class="assigned"/>
        </id>
        <property name="schoolName" column="schoolName" type="java.lang.String"
                  length="20"/>
        <property name="personId" column="personId" type="java.lang.Integer"/>
    </class>
</hibernate-mapping>

```
由person类的配置文件可知没有使用延迟加载（person类的映射文件中schools集合属性lazy=“false”）
```
public static void main(String[] args) {
   Configuration conf = new Configuration();
   SessionFactory sessionFactory = conf.configure().buildSessionFactory();
   Session session = sessionFactory.openSession();
   String hql = "from Person";
   Query q = session.createQuery(hql);
   List<Person> result = q.list();
   session.flush();
   session.close();
   System.out.println("end");
}
```
观察了一下打印信息，有2条sql：
```
Hibernate: select person0_.id as id0_,
person0_.name as name0_, person0_.age as age0_ from T_PERSON person0_
Hibernate: select schools0_.personId as personId0_1_, schools0_.id as id1_,
schools0_.id as id1_0_, schools0_.schoolName as schoolName1_0_,
schools0_.personId as personId1_0_ from T_SCHOOL schools0_
where schools0_.personId=?
end

```
在查询出person的同时查询出和person相关联的school，当数据量比较大的时候这是相当消耗性能的
修改一下使用延迟加载（person类的映射文件中schools集合属性lazy=“true”）
还是使用上面的main方法，打印信息中只有1条sql：
```
Hibernate: select person0_.id as id0_, person0_.name as name0_,
person0_.age as age0_ from T_PERSON person0_
end
```

修改一下mian方法：
```
public static void main(String[] args) {
   Configuration conf = new Configuration();
   SessionFactory sessionFactory = conf.configure().buildSessionFactory();
   Session session = sessionFactory.openSession();
   String hql = "from Person";
   Query q = session.createQuery(hql);
   List<Person> result = q.list();
   System.out.println("person");
   Set<School> s = result.get(0).getSchools();
   System.out.println("schools");
   session.flush();
   session.close();
   System.out.println("end");
}

```

注意这里面并没有真正使用person中的Set<school>，创建了一个Set<school>的引用，再次观察打印信息：
```
Hibernate: select person0_.id as id0_, person0_.name as name0_,
person0_.age as age0_ from T_PERSON person0_
person
schools
end

```
还是一条，再次修改一下main方法：
```
public static void main(String[] args) {
   Configuration conf = new Configuration();
   SessionFactory sessionFactory = conf.configure().buildSessionFactory();
   Session session = sessionFactory.openSession();
   String hql = "from Person";
   Query q = session.createQuery(hql);
   List<Person> result = q.list();
   System.out.println("person");
   Set<School> s = result.get(0).getSchools();
   s.iterator();
   System.out.println("schools");
   session.flush();
   session.close();
   System.out.println("end");
}
```
这次的打印信息中有2条sql了：
```
Hibernate: select person0_.id as id0_, person0_.name as name0_,
person0_.age as age0_ from T_PERSON person0_
person
Hibernate: select schools0_.personId as personId0_1_,
schools0_.id as id1_, schools0_.id as id1_0_, schools0_.schoolName
as schoolName1_0_, schools0_.personId as personId1_0_
from T_SCHOOL schools0_ where schools0_.personId=?
schools
end

```
说明只有当程序中真正使用设置为延迟加载的对象时，hibernate才会去加载该对象。
s,size()、s.contains()、s.empty() 不会加载 ，iterator会加载。
但是应该注意到在上面的代码中，即使是调用设置为延迟加载s的对象，也是在同一个session中调用，但是实际项目中，肯定不会在同一个session中使用s对象，而是经常在dao层取出person，而在bo层甚至是view层中延迟加载s进行业务逻辑处理或是进行页面展示，这个时候session已经关闭了。

```
public static void main(String[] args) {
   Configuration conf = new Configuration();
   SessionFactory sessionFactory = conf.configure().buildSessionFactory();
   Session session = sessionFactory.openSession();
   String hql = "from Person";
   Query q = session.createQuery(hql);
   List<Person> result = q.list();
   session.flush();
   session.close();
   System.out.println("end");
   Set<School> s = result.get(0).getSchools();
   s.iterator();
   System.out.println("schools");
}

```

如果不在同一个session中使用延迟加载对象，则会报如下的错误：
```
Hibernate: select person0_.id as id0_, person0_.name as name0_, person0_.age as age0_ from T_PERSON person0_
end
Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.po.Person.schools, no session or session was closed
at org.hibernate.collection.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:383)
at org.hibernate.collection.AbstractPersistentCollection.throwLazyInitializationExceptionIfNotConnected(AbstractPersistentCollection.java:375)
at org.hibernate.collection.AbstractPersistentCollection.initialize(AbstractPersistentCollection.java:368)
at org.hibernate.collection.AbstractPersistentCollection.read(AbstractPersistentCollection.java:111)
at org.hibernate.collection.PersistentSet.iterator(PersistentSet.java:186)
at com.test.test.main(test.java:27)

```
会提示找不到session或session已关闭，这时候如何处理呢，就是下面opensessioninviewFilter

### opensessioninviewFilter：
官方文档对他的描述：
```
Servlet 2.3 Filter that binds a Hibernate Session to the thread for
the entire processing of the request. Intended for the "Open Session in View" pattern,
i.e. to allow for lazy loading in web views despite the original transactions
already being completed.
This filter makes Hibernate Sessions available via the current thread,
which will be autodetected by transaction managers.
It is suitable for service layer transactions via HibernateTransactionManager
or JtaTransactionManager as well as for non-transactional execution
(if configured appropriately).
NOTE: This filter will by default not flush the Hibernate Session,
with the flush mode set to FlushMode.NEVER. It assumes to be used in combination
with service layer transactions that care for the flushing:
The active transaction manager will temporarily change the flush mode
to FlushMode.AUTO during a read-write transaction,
with the flush mode reset to FlushMode.NEVER at the end of each transaction.
If you intend to use this filter without transactions,
consider changing the default flush mode (through the "flushMode" property).
WARNING: Applying this filter to existing logic can cause issues
that have not appeared before, through the use of a single Hibernate Session
for the processing of an entire request. In particular, the reassociation of persistent
objects with a Hibernate Session has to occur at the very beginning of request processing,
to avoid clashes with already loaded instances of the same objects.
Alternatively, turn this filter into deferred close mode,
by specifying "singleSession"="false":
It will not use a single session per request then,
but rather let each data access operation or transaction use its own session
(like without Open Session in View).
Each of those sessions will be registered for deferred close,
though, actually processed at request completion.
A single session per request allows for most efficient first-level caching,
but can cause side effects, for example on saveOrUpdate or when continuing
after a rolled-back transaction.
The deferred close strategy is as safe as no Open Session in View in that respect,
while still allowing for lazy loading in views
(but not providing a first-level cache for the entire request).
Looks up the SessionFactory in Spring's root web application context.
Supports a "sessionFactoryBeanName" filter init-param in web.xml;
the default bean name is "sessionFactory".
Looks up the SessionFactory on each request, to avoid initialization order issues
(when using ContextLoaderServlet,
the root application context will get initialized after this filter).

```

该filter会将session绑定至当前请求的线程上，这样只要是在当前请求的生命周期内，可以随时访问session，只需要在项目的web.xml文件中增加如下配置即可：
```
<filter>
    <filter-name>hibernateOpenSessionInViewFilter</filter-name>
    <filter-class>org.springside.modules.orm.hibernate.OpenSessionInViewFilter</filter-class>
    <init-param>
        <param-name>flushMode</param-name>
        <param-value>AUTO</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>hibernateOpenSessionInViewFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
<!-- <filter>
<filter-name>struts2</filter-name>
<filter-class>org.apache.struts2.dispatcher.FilterDispatcher</filter-class>
</filter> -->

```


> 只要增加该过滤器即可，但是要特别注意的是：该过滤器需要配置在struts2的过滤器之前。
还有一点要注意的是（参见文档说明中的note）：在使用opensessioninviewFilter时，session的flushMode默认是never的，如果想要进行保存，更新等操作，必须要修改session的flushMode，有2种方式进行修改（当然可以重写filter的方法，有点麻烦不讨论了）：
1，像上面一样，当在web.xml中注册filter时，增加初始化参数（init-param，灵活性不高)
2，使用声明式事务代理（灵活性很高，推荐）
需要注意的是（参见文档说明中的warning）：由于session会和当前request绑定，所以在已经存在的逻辑上添加该filter会产生以前没有产生过的问题。每次通过session取得hibernate持久化对象时都要重新关联，避免获得保存在session中以前加载过的相同对象。


举例如下：
数据库中的表的主键都统一通过ColumnSequence这张表来管理，ColumnSequence表通过tablename字段和columnname字段来唯一的确定一张表的主键，并通过sequencenum字段对表的主键进行管理：

| tablename| columnname| sequencenum|
| --- | --- | --- |
| 表名 | 列名 | 主键值 |

当需要向数据库中的某张表插入新的数据时，首先更新在ColumnSequence表中管理的对应表的主键值为当前值加1，再将更新后的值取出作为即将插入数据的主键值：
```
public Integer getSequence(String tableName, String columnName) throws DAOException {
      try {
         ColumnSequencePK id = new ColumnSequencePK(tableName,columnName);
         String hql = "update ColumnSequence ";
         hql += " set sequencenum = sequencenum + 1";
         hql += " where tablename = '"+tableName+"' and columnname = '"+columnName+"'";
         this.update(hql);
/* //在查询前清除掉session中的缓存，以免接下来的get方法从缓存中读取数据而不是从数据库中重新查询数据
this.getMyHibernateTemplate().clear(); */
         ColumnSequence tmp = (ColumnSequence) this.getByID(ColumnSequence.class,id);
         return tmp.getSequencenum();
      }catch (Exception e) {
         throw new DAOException(e, this.getClass());
      }
   }

```

> 当在同一次请求中连续2次保存同类对象，则需连续2次通过上面的方法获取主键值。
第一次通过ColumnSequence tmp = (ColumnSequence) this.getByID(ColumnSequence.class,id)获取ColumnSequence对象时，hibernate会将该对象保存在session中，当第二次通过ColumnSequence tmp = (ColumnSequence) this.getByID(ColumnSequence.class,id)获取ColumnSequence对象时，hibernate发现session中存有相同的对象（getByID的参数ColumnSequence.class以及id都相同），就不会进行sql查询，而是直接返回session中已存在的ColumnSequence对象，并将该对象的sequencenum返回作为主键值，要注意的是第二次获取ColumnSequence对象之前是进行了一次更新操作的，数据库中的sequencenum被更新了2次，但是2个需要保存到数据库中的对象获取到的主键值却是同一个（2次getByID方法返回的都是同一个ColumnSequence对象），这时候当事务提交时会报错，提示主键已存在，只有第一个需要保存的对象成功的存入了数据库中。

#### 解决方式：
上面代码中的注释部分，在第二次获取ColumnSequence之前清理掉session中的缓存，hibernate在session中找不到相同的对象，只能去数据库中查询获得对象，这时候第二次取到的ColumnSequence对象就变成第二次更新后的值了，而不是第一次取到的ColumnSequence对象在session中的缓存，当然主键也就是更新后的了。
这种情况比较少见，但是应当注意
warning中还给出了一种解决方式，即在web.xml中的opensessioninviewFilter初始化参数中添加如下项：
```
<init-param>
    <param-name>singleSession</param-name>
    <param-value>false</param-value>
</init-param>

```
但是这种配置方式有2个问题：
1，相当于没有使用opensessioninviewFilter
2，明显的增多hibernate打开的session数量，对hibernate性能影响严重
