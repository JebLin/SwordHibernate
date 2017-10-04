# SwordHibernate
### 什么是 Hibernate ?
```
一个框架
一个 Java 领域的持久化框架
一个 ORM 框架
```

#### 对象的持久化
```
狭义的理解，“持久化”仅仅指把对象永久保存到数据库中
广义的理解，“持久化”包括和数据库相关的各种操作：
    保存：把对象永久保存到数据库中。
    更新：更新数据库中对象(记录)的状态。
    删除：从数据库中删除一个对象。
    查询：根据特定的查询条件，把符合查询条件的一个或多个对象从数据库加载到内存中。
    加载：根据特定的OID，把一个对象从数据库加载到内存中。
    OID：为了在系统中能够找到所需对象，需要为每一个对象分配一个唯一的标识号。在关系数据库中称之为主键，而在对象术语中，则叫做对象标识(Object identifier-OID). 

```

#### ORM(Object/Relation Mapping): 对象/关系映射
|面向对象概念 | 面向关系概念 |
|-------|-------|
| 类 | 表 |
| 对象 | 表的行（记录）|
| 属性 | 表的列（字段）|

| 阶层 | 描述 |
|--------|--------|
|业务逻辑层| 域模型（对象、属性、关联、继承和多态）  |
|持久化层| ORM 实现（参照 对象-关系映射文件）|
|数据库层| 关系数据模型（表、字段、索引、主键和外键）|

#### 流行的ORM框架
```
ibernate: 非常优秀、成熟的 ORM 框架。 完成对象的持久化操作 Hibernate 允许开发者采用面向对象的方式来操作关系数据库。 消除那些针对特定数据库厂商的 SQL 代码
myBatis： 相比 Hibernate 灵活高，运行速度快 开发速度慢，不支持纯粹的面向对象操作，需熟悉sql语   句，并且熟练使用sql语句优化功能
TopLink
OJB
```

#### Hibernate 开发步骤
```
1、创建Hibernate配置文件  （hibernate.cfg.xml）（Hibernate 从其配置文件中读取和数据库连接的有关信息, 这个文件应该位于应用的 classpath 下）
2、创建持久化类 （Hibernate 不要求持久化类继承任何父类或实现接口，这可以保证代码不被污染。这就是Hibernate被称为低侵入式设计的原因）
3、创建对象-关系映射文件（*.hbm.xml）  （Hibernate 采用 XML 格式的文件来指定对象和关系数据之间的映射. 在运行时 Hibernate 将根据这个映射文件来生成各种 SQL 语句 映射文件的扩展名为 .hbm.xml）
4、通过Hibernate API编写访问数据库的代码
```
#### 创建持久化 Java 类 
```
步骤：    
提供一个无参的构造器:使Hibernate可以使用Constructor.newInstance() 来实例化持久化类 
提供一个标识属性(identifier property): 通常映射为数据库表的主键字段. 如果没有该属性，一些功能将不起作用，如：Session.saveOrUpdate() 
为类的持久化类字段声明访问方法(get/set): Hibernate对JavaBeans 风格的属性实行持久化。 
使用非 final 类: 在运行时生成代理是 Hibernate 的一个重要的功能. 如果持久化类没有实现任何接口, Hibnernate 使用 CGLIB 生成代理. 如果使用的是 final 类, 则无法生成 CGLIB 代理.无法延迟加载。。。
重写 eqauls 和 hashCode 方法: 如果需要把持久化类的实例放到 Set 中(当需要进行关联映射时), 则应该重写这两个方法 
```

#### Configuration 类
```
Configuration 类负责管理 Hibernate 的配置信息。包括如下内容：
Hibernate 运行的底层信息：数据库的URL、用户名、密码、JDBC驱动类，数据库Dialect,数据库连接池等（对应 hibernate.cfg.xml 文件）。
持久化类与数据表的映射关系（*.hbm.xml 文件）
创建 Configuration 的两种方式
属性文件（hibernate.properties）: Configuration cfg = new Configuration(); 
Xml文件（hibernate.cfg.xml）：Configuration cfg = new Configuration().configure();
Configuration 的 configure 方法还支持带参数的访问： File file = new File(“simpleit.xml”); Configuration cfg = new Configuration().configure(file); 
```
#### SessionFactory 接口
```
针对单个数据库映射关系经过编译后的内存镜像，是线程安全的。
SessionFactory 对象一旦构造完毕，即被赋予特定的配置信息 SessionFactory是生成Session的工厂 构造 SessionFactory 很消耗资源，一般情况下一个应用中只初始化一个 SessionFactory 对象。
Hibernate4 新增了一个 ServiceRegistry 接口，所有基于 Hibernate 的配置或者服务都必须统一向这个 ServiceRegistry  注册后才能生效 Hibernate4 中创建 SessionFactory 的步骤 
ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
sessionFactory = configuration.buildSessionFactory(serviceRegistry);
```
#### Session 接口
```
Session 是应用程序与数据库之间交互操作的一个单线程对象，是 Hibernate 运作的中心，所有持久化对象必须在 session 的管理下才可以进行持久化操作。此对象的生命周期很短。
Session 对象有一个一级缓存，显式执行 flush 之前，所有的持久层操作的数据都缓存在 session 对象处。相当于 JDBC 中的 Connection。 
持久化类与 Session 关联起来后就具有了持久化的能力。
Session 类的方法： 取得持久化对象的方法： get() load() 持久化对象都得保存，更新和删除：save(),update(),saveOrUpdate(),delete() 开启事务: beginTransaction().
管理 Session 的方法：isOpen(),flush(), clear(), evict(), close()等.
```

#### Hibernate 配置文件的两个配置项
```
hbm2ddl.auto：该属性可帮助程序员实现正向工程, 即由 java 代码生成数据库脚本, 进而生成具体的表结构。
取值 create | update | create-drop | validate
create : 会根据 .hbm.xml  文件来生成数据表, 但是每次运行都会删除上一次的表 ,重新生成表, 哪怕二次没有任何改变
create-drop : 会根据 .hbm.xml 文件生成表,但是SessionFactory一关闭, 表就自动删除
update : 最常用的属性值，也会根据 .hbm.xml 文件生成表, 但若 .hbm.xml  文件和数据库中对应的数据表的表结构不同, Hiberante  将更新数据表结构，但不会删除已有的行和列
validate : 会和数据库中的表进行比较, 若 .hbm.xml 文件中的列在数据表中不存在，则抛出异常
--------------------------------------------------------
format_sql：是否将 SQL 转化为格式良好的 SQL . 取值 true | false 
```

#### 通过 Session 操纵对象
```
Session 接口是 Hibernate 向应用程序提供的操纵数据库的最主要的接口, 它提供了基本的保存, 更新, 删除和加载 Java 对象的方法.
Session 具有一个缓存, 位于缓存中的对象称为持久化对象, 它和数据库中的相关记录对应.
Session 能够在某些时间点, 按照缓存中对象的变化来执行相关的 SQL 语句, 来同步更新数据库, 这一过程被称为刷新缓存(flush) 站在持久化的角度, 
Hibernate 把对象分为 4 种状态: 持久化状态, 临时状态, 游离状态, 删除状态. 
Session 的特定方法能使对象从一个状态转换到另一个状态. 
```
#### Session 缓存
```
在 Session 接口的实现中包含一系列的 Java 集合, 这些 Java 集合构成了 Session 缓存. 只要 Session 实例没有结束生命周期, 且没有清理缓存，则存放在它缓存中的对象也不会结束生命周期 Session 缓存可减少 Hibernate 应用程序访问数据库的频率。
```
#### 操作 Session 缓存
```
flush()   ： cache --> DB  将缓存中的数据持久化到数据库 
场景：
a.显式调用 Session 的 flush() 方法
b.当应用程序调用 Transaction 的 commit（）方法的时, 该方法先 flush ，然后在向数据库提交事务 
c.flush 缓存的特殊情况: 如果对象使用 native 生成器生成 OID, 那么当调用 Session 的 save() 方法保存对象时, 会立即执行向数据库插入该实体的 insert 语句.因为save方法后，必须保证对象的ID是存在的
refresh() ： DB --> cache 强制发送一条select语句 将数据库中的数据同步到缓存  
clear()  ：  清空cache缓存中数据
```
####  session.flush()和transaction.commit()的区别
```
1、flush()方法进行清理缓存的操作,执行一系列的SQL语句,但不会提交事务;commit()方法会先调用flush()方法,然后提交事务. 提交事务意味着对数据库所做的更新会永久保持下来 所谓清理,是指Hibernate 按照持久化象的状态来同步更新数据库
2、Flush()后只是将Hibernate缓存中的数据提交到数据库,如果这时数据库处在一个事物当中,则数据库将这些SQL语句缓存起来,当Hibernate进行commit时,会告诉数据库,你可以真正提交了,这时数据才会永久保存下来,也就是被持久化了.
3、commit针对事物的，flush针对缓存的， 数据同步到数据库中后只要没有commit还是可以rollback的。
可以这么理解，hibiernate有二级缓存，而平时一般只用一级缓存（默认开启），也就是session级的缓存。处于一个事务当中，当save的时候，只是把相应的insert行为登记在了以及缓存上，而flush是把缓存清空，同时把insert行为登记在数据库的事务上。当commit提交之后，才会执行相应的insert代码，而commit又是隐性的调用flush的，
那在commit之前调用flush的作用的什么？我的理解是防止多条SQL语句冲突，这是因为flush到数据库中执行SQL语句的顺序不是按照你代码的先后顺序，而是按照insert，update....delete的顺序执行的，如果你不按照这个顺序在代码中编写，如果逻辑一旦出错就会抛exception了，解决这个的办法之一就是在可能其冲突的SQL操作后面flush一下，防止后面的语句其冲突
同时flush的作用，也有提交大量数据时候清理缓存的作用
```

#### Hibernate各种主键生成策略与配置详解
```
1、assigned
主键由外部程序负责生成，在 save() 之前必须指定一个。Hibernate不负责维护主键生成。与Hibernate和底层数据库都无关，可以跨数据库。在存储对象前，必须要使用主键的setter方法给主键赋值，至于这个值怎么生成，完全由自己决定，这种方法应该尽量避免。

“ud”是自定义的策略名，人为起的名字，后面均用“ud”表示。

特点：可以跨数据库，人为控制主键生成，应尽量避免。

2、increment
由Hibernate从数据库中取出主键的最大值（每个session只取1次），以该值为基础，每次增量为1，在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库。

Hibernate调用org.hibernate.id.IncrementGenerator类里面的generate()方法，使用select max(idColumnName) from tableName语句获取主键最大值。该方法被声明成了synchronized，所以在一个独立的Java虚拟机内部是没有问题的，然而，在多个JVM同时并发访问数据库select max时就可能取出相同的值，再insert就会发生Dumplicate entry的错误。所以只能有一个Hibernate应用进程访问数据库，否则就可能产生主键冲突，所以不适合多进程并发更新数据库，适合单一进程访问数据库，不能用于群集环境。

官方文档：只有在没有其他进程往同一张表中插入数据时才能使用，在集群下不要使用。

特点：跨数据库，不适合多进程并发更新数据库，适合单一进程访问数据库，不能用于群集环境。

3、hilo
hilo（高低位方式high low）是hibernate中最常用的一种生成方式，需要一张额外的表保存hi的值。保存hi值的表至少有一条记录（只与第一条记录有关），否则会出现错误。可以跨数据库。

hilo生成器生成主键的过程（以hibernate_unique_key表，next_hi列为例）：

1. 获得hi值：读取并记录数据库的hibernate_unique_key表中next_hi字段的值，数据库中此字段值加1保存。

2. 获得lo值：从0到max_lo循环取值，差值为1，当值为max_lo值时，重新获取hi值，然后lo值继续从0到max_lo循环。

3. 根据公式 hi * (max_lo + 1) + lo计算生成主键值。

注意：当hi值是0的时候，那么第一个值不是0*(max_lo+1)+0=0，而是lo跳过0从1开始，直接是1、2、3……

那max_lo配置多大合适呢？

这要根据具体情况而定，如果系统一般不重启，而且需要用此表建立大量的主键，可以吧max_lo配置大一点，这样可以减少读取数据表的次数，提高效率；反之，如果服务器经常重启，可以吧max_lo配置小一点，可以避免每次重启主键之间的间隔太大，造成主键值主键不连贯。

特点：跨数据库，hilo算法生成的标志只能在一个数据库中保证唯一。

4、seqhilo
与hilo类似，通过hi/lo算法实现的主键生成机制，只是将hilo中的数据表换成了序列sequence，需要数据库中先创建sequence，适用于支持sequence的数据库，如Oracle。

特点：与hilo类似，只能在支持序列的数据库中使用。
5、sequence
采用数据库提供的sequence机制生成主键，需要数据库支持sequence。如oralce、DB、SAP DB、PostgerSQL、McKoi中的sequence。MySQL这种不支持sequence的数据库则不行（可以使用identity）。

Hibernate生成主键时，查找sequence并赋给主键值，主键值由数据库生成，Hibernate不负责维护，使用时必须先创建一个sequence，如果不指定sequence名称，则使用Hibernate默认的sequence，名称为hibernate_sequence，前提要在数据库中创建该sequence。

特点：只能在支持序列的数据库中使用，如Oracle。

6、identity
identity由底层数据库生成标识符。identity是由数据库自己生成的，但这个主键必须设置为自增长，使用identity的前提条件是底层数据库支持自动增长字段类型，如DB2、SQL Server、MySQL、Sybase和HypersonicSQL等，Oracle这类没有自增字段的则不支持。

例：如果使用MySQL数据库，则主键字段必须设置成auto_increment。

id int(11) primary key auto_increment

特点：只能用在支持自动增长的字段数据库中使用，如MySQL。

7、native
native由hibernate根据使用的数据库自行判断采用identity、hilo、sequence其中一种作为主键生成方式，灵活性很强。如果能支持identity则使用identity，如果支持sequence则使用sequence。

例如MySQL使用identity，Oracle使用sequence

注意：如果Hibernate自动选择sequence或者hilo，则所有的表的主键都会从Hibernate默认的sequence或hilo表中取。并且，有的数据库对于默认情况主键生成测试的支持，效率并不是很高。

使用sequence或hilo时，可以加入参数，指定sequence名称或hi值表名称等，如

<param name="sequence">hibernate_id</param>

特点：根据数据库自动选择，项目中如果用到多个数据库时，可以使用这种方式，使用时需要设置表的自增字段或建立序列，建立表等。

8、uuid
UUID：Universally Unique Identifier，是指在一台机器上生成的数字，它保证对在同一时空中的所有机器都是唯一的。按照开放软件基金会(OSF)制定的标准计算，用到了以太网卡地址、纳秒级时间、芯片ID码和许多可能的数字，标准的UUID格式为：

xxxxxxxx-xxxx-xxxx-xxxxxx-xxxxxxxxxx (8-4-4-4-12)

其中每个 x 是 0-9 或 a-f 范围内的一个十六进制的数字。

Hibernate在保存对象时，生成一个UUID字符串作为主键，保证了唯一性，但其并无任何业务逻辑意义，只能作为主键，唯一缺点长度较大，32位（Hibernate将UUID中间的“-”删除了）的字符串，占用存储空间大，但是有两个很重要的优点，Hibernate在维护主键时，不用去数据库查询，从而提高效率，而且它是跨数据库的，以后切换数据库极其方便。

特点：uuid长度大，占用空间大，跨数据库，不用访问数据库就生成主键值，所以效率高且能保证唯一性，移植非常方便，推荐使用。

9、guid
GUID：Globally Unique Identifier全球唯一标识符，也称作 UUID，是一个128位长的数字，用16进制表示。算法的核心思想是结合机器的网卡、当地时间、一个随即数来生成GUID。从理论上讲，如果一台机器每秒产生10000000个GUID，则可以保证（概率意义上）3240年不重复。

Hibernate在维护主键时，先查询数据库，获得一个uuid字符串，该字符串就是主键值，该值唯一，缺点长度较大，支持数据库有限，优点同uuid，跨数据库，但是仍然需要访问数据库。

注意：长度因数据库不同而不同

MySQL中使用select uuid()语句获得的为36位（包含标准格式的“-”）

Oracle中，使用select rawtohex(sys_guid()) from dual语句获得的为32位（不包含“-”）

特点：需要数据库支持查询uuid，生成时需要查询数据库，效率没有uuid高，推荐使用uuid。

10、foreign
使用另外一个相关联的对象的主键作为该对象主键。主要用于一对一关系中。

<id name="id" column="id">

<generator class="foreign">

<param name="property">user</param>

</generator>

</id>

<one-to-one name="user" class="domain.User" constrained="true" />

该例使用domain.User的主键作为本类映射的主键。

特点：很少使用，大多用在一对一关系中。

11、select
使用触发器生成主键，主要用于早期的数据库主键生成机制，能用到的地方非常少。

```

#### 刷新缓存的时间点
```
若希望改变 flush 的默认时间点, 可以通过 Session 的 setFlushMode() 方法显式设定 flush 的时间点 
```
|清理缓存的模式|各种查询方法|Transaction的commit方法|Session的flush方法|
|---|---|---|
|FlushMode.AUTO(默认) | 清理| 清理|清理|
|FlushMode.COMMIT | 不清理 | 清理 | 清理|
|FlushMode.NEVER|不清理 | 不清理 | 不清理|


#### 数据库的隔离级别
```
数据库事务的隔离性: 数据库系统必须具有隔离并发运行各个事务的能力, 使它们不会相互影响, 避免各种并发问题.
一个事务与其他事务隔离的程度称为隔离级别. 
数据库规定了多种事务隔离级别, 不同隔离级别对应不同的干扰程度, 隔离级别越高, 数据一致性就越好, 但并发性越弱 
```

#### 在 MySql 中设置隔离级别
```
每启动一个 mysql 程序, 就会获得一个单独的数据库连接. 每个数据库连接都有一个全局变量 @@tx_isolation, 表示当前的事务隔离级别. 
MySQL 默认的隔离级别为 Repeatable Read 查看当前的隔离级别: SELECT @@tx_isolation;
设置当前 mySQL 连接的隔离级别:  set transaction isolation level read committed;
设置数据库系统的全局的隔离级别: set global transaction isolation level read committed; 

JDBC 数据库连接使用数据库系统默认的隔离级别. 在 Hibernate 的配置文件中可以显式的设置隔离级别. 
每一个隔离级别都对应一个整数:
1. READ UNCOMMITED
2. READ COMMITED
4. REPEATABLE READ
8. SERIALIZEABLE
Hibernate通过为 Hibernate 映射文件指定 hibernate.connection.isolation 属性来设置事务的隔离级别 
```

#### 持久化对象的状态
```
站在持久化的角度, Hibernate 把对象分为 4 种状态: 持久化状态, 临时状态, 游离状态, 删除状态.
 Session 的特定方法能使对象从一个状态转换到另一个状态. 
比喻：
OID --> 公司门卡
Session缓存 --> 在公司干活
数据库 --> 公司名单中有你
```
```
a.临时对象（Transient）: （拿到offer了，还没去报告） 在使用代理主键的情况下, OID 通常为 null，不处于 Session 的缓存中，在数据库中没有对应的记录。

b.持久化对象(也叫”托管”)（Persist）：（正式员工） OID 不为 null，位于 Session 缓存中 若在数据库中已经有和其对应的记录, 持久化对象和数据库中的相关记录对应 。Session 在 flush 缓存时, 会根据持久化对象的属性变化, 来同步更新数据库 。
在同一个 Session 实例的缓存中, 数据库表中的每条记录只对应唯一的持久化对象。

c.删除对象(Removed) （离职了） 在数据库中没有和其 OID 对应的记录 不再处于 Session 缓存中 一般情况下, 应用程序不该再使用被删除的对象 。

d.游离对象(也叫”脱管”) （Detached）：（请假了） OID 不为 null 不再处于 Session 缓存中 一般情况需下, 游离对象是由持久化对象转变过来的, 因此在数据库中可能还存在与它对应的记录 。

```
| 状态 | 有OID | 位于缓存内 | 数据库有记录 |
| --- | --- | --- | --- |
| 临时对象(Transient)| × | × | × |
| 持久化对象(Persist)| √ | √ | √ |
| 删除对象 (Removed)|× | × | × |
|游离对象(Detached)| √ | × | √ |

 
#### Session 的 save() 方法 
```
Session 的 save() 方法使一个临时对象转变为持久化对象.
Session 的 save() 方法完成以下操作:
1、把 News 对象加入到 Session 缓存中, 使它进入持久化状态
2、选用映射文件指定的标识符生成器, 为持久化对象分配唯一的 OID. 在 使用代理主键的情况下, setId() 方法为 News 对象设置 OID 使无效的.
3、计划执行一条 insert 语句：在 flush 缓存的时候 Hibernate 通过持久化对象的 OID 来维持它和数据库相关记录的对应关系. 当 News 对象处于持久化状态时, 不允许程序随意修改它的 ID
persist() 和 save() 区别： 当对一个 OID 不为 Null 的对象执行 save() 方法时, 会把该对象以一个新的 oid 保存到数据库中;  但执行 persist() 方法时会抛出一个异常. 
```
#### Session 的 get() 和 load() 方法
```
根据跟定的 OID 从数据库中加载一个持久化对象
区别: 当数据库中不存在与 OID 对应的记录时, load() 方法抛出 ObjectNotFoundException 异常, 而 get() 方法返回 null
两者采用不同的延迟检索策略：load 方法支持延迟加载策略。而 get 不支持。 
```
#### Session 的 update() 方法
```
Session 的 update() 方法使一个游离对象转变为持久化对象, 并且计划执行一条 update 语句.
若希望 Session 仅当修改了 News 对象的属性时, 才执行 update() 语句, 可以把映射文件中 <class> 元素的 select-before-update 设为 true. 该属性的默认值为 false 当 update() 方法关联一个游离对象时, 如果在 Session 的缓存中已经存在相同 OID 的持久化对象, 会抛出异常
当 update() 方法关联一个游离对象时, 如果在数据库中不存在相应的记录, 也会抛出异常. 
```
#### Session 的 delete() 方法
```
Session 的 delete() 方法既可以删除一个游离对象, 也可以删除一个持久化对象 Session 的 delete() 方法处理过程 计划执行一条 delete 语句 把对象从 Session 缓存中删除, 该对象进入删除状态.
Hibernate 的 cfg.xml 配置文件中有一个 hibernate.use_identifier_rollback 属性, 其默认值为 false, 若把它设为 true, 将改变 delete() 方法的运行行为: delete() 方法会把持久化对象或游离对象的 OID 设置为 null, 使它们变为临时对象 
```
#### 通过 Hibernate 调用存储过程
```
Work 接口: 直接通过 JDBC API 来访问数据库的操作 
Session 的 doWork(Work) 方法用于执行 Work 对象指定的操作, 即调用 Work 对象的 execute() 方法. Session 会把当前使用的数据库连接传递给 execute() 方法. 
```

#### Hibernate 与触发器协同工作
```
Hibernate 与数据库中的触发器协同工作时, 
会造成两类问题 :
触发器使 Session 的缓存中的持久化对象与数据库中对应的数据不一致:触发器运行在数据库中, 它执行的操作对 Session 是透明的
Session 的 update() 方法盲目地激发触发器: 无论游离对象的属性是否发生变化, 都会执行 update 语句, 而 update 语句会激发数据库中相应的触发器 
解决方案:
在执行完 Session 的相关操作后, 立即调用 Session 的 flush() 和 refresh() 方法, 迫使 Session 的缓存与数据库同步(refresh() 方法重新从数据库中加载对象)
在映射文件的的 <class> 元素中设置 select-before-update 属性: 当 Session 的 update 或 saveOrUpdate() 方法更新一个游离对象时, 会先执行 Select 语句, 获得当前游离对象在数据库中的最新数据, 只有在不一致的情况下才会执行 update 语句 
```

#### Hibernate配置文件
```
Hibernate 配置文件主要用于配置数据库连接和 Hibernate 运行时所需的各种属性
每个 Hibernate 配置文件对应一个 Configuration 对象
Hibernate配置文件可以有两种格式: hibernate.properties、 hibernate.cfg.xml 
```
#### hibernate.cfg.xml的常用属性
```
JDBC 连接属性 connection.url：数据库URL
connection.username：数据库用户名
connection.password：数据库用户密码 
connection.driver_class：数据库JDBC驱动
dialect：配置数据库的方言，根据底层的数据库不同产生不同的 sql 语句，Hibernate 会针对数据库的特性在访问时进行优化 
show_sql：是否将运行期生成的SQL输出到日志以供调试。取值 true | false 
format_sql：是否将 SQL 转化为格式良好的 SQL . 取值 true | false
hbm2ddl.auto：在启动和停止时自动地创建，更新或删除数据库模式。取值 create | update | create-drop | validate hibernate.jdbc.fetch_size hibernate.jdbc.batch_size

hibernate.jdbc.fetch_size 与 hibernate.jdbc.batch_size ：
hibernate.jdbc.fetch_size：实质是调用 Statement.setFetchSize() 方法设定 JDBC 的 Statement 读取数据的时候每次从数据库中取出的记录条数。
例如一次查询1万条记录，对于Oracle的JDBC驱动来说，是不会 1 次性把1万条取出来的，而只会取出 fetchSize 条数，当结果集遍历完了这些记录以后，再去数据库取 fetchSize 条数据。因此大大节省了无谓的内存消耗。
Fetch Size设的越大，读数据库的次数越少，速度越快；Fetch Size越小，读数据库的次数越多，速度越慢。Oracle数据库的JDBC驱动默认的Fetch Size = 10，是一个保守的设定。
根据测试，当Fetch Size=50时，性能会提升1倍之多，当 fetchSize=100，性能还能继续提升20%，Fetch Size继续增大，性能提升的就不显著了。并不是所有的数据库都支持Fetch Size特性，例如MySQL就不支持.

hibernate.jdbc.batch_size：设定对数据库进行批量删除，批量更新和批量插入的时候的批次大小，类似于设置缓冲区大小的意思。
batchSize 越大，批量操作时向数据库发送sql的次数越少，速度就越快。 测试结果是当Batch Size=0的时候，使用Hibernate对Oracle数据库删除1万条记录需要25秒，Batch Size = 50的时候，删除仅仅需要5秒！Oracle数据库 batchSize=30 的时候比较合适。 

C3P0 数据库连接池属性
hibernate.c3p0.max_size: 数据库连接池的最大连接数
hibernate.c3p0.min_size: 数据库连接池的最小连接数
hibernate.c3p0.timeout: 数据库连接池中连接对象在多长时间没有使用过后，就应该被销毁
hibernate.c3p0.max_statements: 缓存 Statement 对象的数量
hibernate.c3p0.idle_test_period: 表示连接池检测线程多长时间检测一次池内的所有链接对象是否超时. 连接池本身不会把自己从连接池中移除，而是专门有一个线程按照一定的时间间隔来做这件事，这个线程通过比较连接对象最后一次被使用时间和当前时间的时间差来和 timeout 做对比，进而决定是否销毁这个连接对象。 hibernate.c3p0.acquire_increment: 当数据库连接池中的连接耗尽时, 同一时刻获取多少个数据库连接.

```

#### 对象关系映射文件
#### POJO 类和数据库的映射文件*.hbm.xml
```
POJO 类和关系数据库之间的映射可以用一个XML文档来定义。
通过 POJO 类的数据库映射文件，Hibernate可以理解持久化类和数据表之间的对应关系，也可以理解持久化类属性与数据库表列之间的对应关系 在运行时 Hibernate 将根据这个映射文件来生成各种 SQL 语句 映射文件的扩展名为 .hbm.xml 
```
#### 映射文件说明
```
hibernate-mapping 类层次：class
主键：id 基本类型:property  
实体引用类: many-to-one  |  one-to-one
集合:set | list | map | array one-to-many many-to-many
子类:subclass | joined-subclass
其它:component | any 等
查询语句:query（用来放置查询语句，便于对数据库查询的统一管理和优化）
每个Hibernate-mapping中可以同时定义多个类. 但更推荐为每个类都创建一个单独的映射文件 
```
#### hibernate-mapping
```
hibernate-mapping 是 hibernate 映射文件的
根元素 schema: 指定所映射的数据库schema的名称。若指定该属性, 则表明会自动添加该 schema 前缀
catalog:指定所映射的数据库catalog的名称。
default-cascade(默认为 none): 设置hibernate默认的级联风格. 若配置 Java 属性, 集合映射时没有指定 cascade 属性, 则 Hibernate 将采用此处指定的级联风格.  
default-access (默认为 property): 指定 Hibernate 的默认的属性访问策略。默认值为 property, 即使用 getter, setter 方法来访问属性. 若指定 access, 则 Hibernate 会忽略 getter/setter 方法, 而通过反射访问成员变量.
default-lazy(默认为 true): 设置 Hibernat morning的延迟加载策略. 该属性的默认值为 true, 即启用延迟加载策略. 若配置 Java 属性映射, 集合映射时没有指定 lazy 属性, 则 Hibernate 将采用此处指定的延迟加载策略
  auto-import (默认为 true): 指定是否可以在查询语言中使用非全限定的类名（仅限于本映射文件中的类）。
package (可选): 指定一个包前缀，如果在映射文档中没有指定全限定的类名， 就使用这个作为包名。
```
#### class
```
class 元素用于指定类和表的映射
name:指定该持久化类映射的持久化类的类名
table:指定该持久化类映射的表名, Hibernate 默认以持久化类的类名作为表名
dynamic-insert: 若设置为 true, 表示当保存一个对象时, 会动态生成 insert 语句, insert 语句中仅包含所有取值不为 null 的字段. 默认值为 false dynamic-update: 若设置为 true, 表示当更新一个对象时, 会动态生成 update 语句, update 语句中仅包含所有取值需要更新的字段. 默认值为 false 
select-before-update:设置 Hibernate 在更新某个持久化对象之前是否需要先执行一次查询. 默认值为 false
 batch-size:指定根据 OID 来抓取实例时每批抓取的实例数. lazy: 指定是否使用延迟加载.  mutable: 若设置为 true, 等价于所有的 <property> 元素的 update 属性为 false, 表示整个实例不能被更新. 默认为 true.
discriminator-value: 指定区分不同子类的值. 当使用 <subclass/> 元素来定义持久化类的继承关系时需要使用该属性 
```
#### 映射组成关系
```
Hibernate 把持久化类的属性分为两种:
值(value)类型: 没有 OID, 不能被单独持久化, 生命周期依赖于所属的持久化类的对象的生命周期
实体(entity)类型: 有 OID, 可以被单独持久化, 有独立的生命周期 显然无法直接用 property 映射 pay 属性 
Hibernate 使用 <component> 元素来映射组成关系, 该元素表名 pay 属性是 Worker 类一个组成部分, 在 Hibernate 中称之为组件
<component> 元素来映射组成关系
class:设定组成关系属性的类型, 此处表明 pay 属性为 Pay 类型 
<parent> 元素指定组件属性所属的整体类
name: 整体类在组件类中的属性名 

```

 
#### 映射一对多关联关系
```
在领域模型中, 类与类之间最普遍的关系就是关联关系. 在 UML 中, 关联是有方向的.
以 Customer 和 Order 为例： 一个用户能发出多个订单, 而一个订单只能属于一个客户. 从 Order 到 Customer 的关联是多对一关联; 而从 Customer 到 Order 是一对多关联 单向关联 

<set> 元素的 inverse 属性 
在hibernate中通过对 inverse 属性的来决定是由双向关联的哪一方来维护表和表之间的关系. 
inverse = false 的为主动方，inverse = true 的为被动方, 由主动方负责维护关联关系 在没有设置 inverse=true 的情况下，父子两边都维护父子
关系 
在 1-n 关系中，将 n 方设为主控方将有助于性能改善(如果要国家元首记住全国人民的名字，不是太可能，但要让全国人民知道国家元首，就容易的多)
在 1-N 关系中，若将 1 方设为主控方 会额外多出 update 语句。
插入数据时无法同时插入外键列，因而无法为外键列添加非空约束
```
#### cascade 属性
```
在对象 – 关系映射文件中, 用于映射持久化类之间关联关系的元素, <set>, <many-to-one> 和 <one-to-one> 都有一个 cascade 属性, 它用于指定如何操纵与当前对象关联的其他对象. (开发时不建议使用级联） 
```

| Cascade 取值 | 描述 |
| --- | --- |
|none | 默认值，Session操作当前对象时，忽略其他关联的对象|

|delete |当通过Session的delete()方法删除当前的对象时，会级联删除所有关联的对象|

|delete-orphan | 接触所有和当前对象解除关联关系的对象。例如：customer.getOrders().clear();执行后，数据库中的先前与该customer相关联的order都被删除。|

| save-update |当通过Session的save()、update()及saveOrUpdate()方法更新或保存当前对象时，级联保存所有关联的新建的临时对象，并且级联更新所有关联的游离对象 |

| persist | 当通过Session的persist()方法来保存当前对象时，会级联保存所关联的新建的临时对象|

| merge | 当通过Session的merge()方法来保存当前对象时，会级联融合所有关联的游离对象|

| lock | 当通过Session的lock()方法把当前游离对象加入到Session()缓存中时，会把所有关联的游离对象也加入到Session缓存中。|

| replicate | 当通过Session的replicate()方法赋值当前对象时，会级联赋值所有关联的对象|

| evict |当通过Session的evict()方法从Session缓存中清除当前对象时，会级联清除所有关联的对象|

| refresh |当通过Session的refresh()方法刷新当前对象时，会级联刷新所有关联的对象，所为刷新是指读取数据库中相应的数据。然后根据数据库中的最新的数据去同步更新Session缓存中的数据|

 | all | 包含save-update、persist、merge、delete、lock、replicate、evict及refresh的行为 |

 | all-delete-orphan | 包含all和delete-orphan的行为| 



#### 映射一对一关联关系

```

基于外键映射的 1-1 

对于基于外键的1-1关联，其外键可以存放在任意一边，在需要存放外键一端，增加many-to-one元素。为many-to-one元素增加unique=“true” 属性来表示为1-1关联 

另一端需要使用one-to-one元素，该元素使用 property-ref 属性指定使用被关联实体主键以外的字段作为关联字段



基于主键映射的 1-1 

基于主键的映射策略:指一端的主键生成器使用 foreign 策略,表明根据”对方”的主键来生成自己的主键，自己并不能独立生成主键.

 <param> 子元素指定使用当前持久化类的哪个属性作为 “对方” 

采用foreign主键生成器策略的一端增加 one-to-one 元素映射关联属性，其one-to-one属性还应增加 constrained=“true” 属性；

另一端增加one-to-one元素映射关联属性。

constrained(约束):指定为当前持久化类对应的数据库表的主键添加一个外键约束，引用被关联的对象(“对方”)所对应的数据库表主键 



```

#### 映射多对多关联关系

```

单向 n-n

n-n 的关联必须使用连接表 与 1-n 映射类似，必须为 set 集合元素添加 key 子元素

双向n-n关联 

双向 n-n 关联需要两端都使用集合属性

双向n-n关联必须使用连接表

集合属性应增加 key 子元素用以映射外键列, 集合元素里还应增加many-to-many子元素关联实体类

在双向 n-n 关联的两边都需指定连接表的表名及外键列的列名. 两个集合元素 set 的 table 元素的值必须指定，而且必须相同。set元素的两个子元素：key 和 many-to-many 都必须指定 column 属性，其中，key 和 many-to-many 分别指定本持久化类和关联类在连接表中的外键列名，因此两边的 key 与 many-to-many 的column属性交叉相同。也就是说，一边的set元素的key的 cloumn值为a,many-to-many 的 column 为b；则另一边的 set 元素的 key 的 column 值 b,many-to-many的 column 值为 a. 

  对于双向 n-n 关联, 必须把其中一端的 inverse 设置为 true, 否则两端都维护关联关系可能会造成主键冲突. 

```



#### 映射继承关系

```

对于面向对象的程序设计语言而言，继承和多态是两个最基本的概念。

Hibernate 的继承映射可以理解持久化类之间的继承关系。例如：人和学生之间的关系。学生继承了人，可以认为学生是一个特殊的人，如果对人进行查询，学生的实例也将被得

```

#### 继承映射

```

Hibernate支持三种继承映射策略：

使用 subclass 进行映射：将域模型中的每一个实体对象映射到一个独立的表中，也就是说不用在关系数据模型中考虑域模型中的继承关系和多态。

使用 joined-subclass 进行映射： 对于继承关系中的子类使用同一个表，这就需要在数据库表中增加额外的区分子类类型的字段。

使用  union-subclass 进行映射：域模型中的每个类映射到一个表，通过关系数据模型中的外键来描述表之间的继承关系。这也就相当于按照域模型的结构来建立数据库中的表，并通过外键来建立表之间的继承关系。 

```

#### 采用 subclass 元素的继承映射
```
采用 subclass 的继承映射可以实现对于继承关系中父类和子类使用同一张表
因为父类和子类的实例全部保存在同一个表中，因此需要在该表内增加一列，使用该列来区分每行记录到底是哪个类的实例----这个列被称为辨别者列(discriminator). 
在这种映射策略下，使用 subclass 来映射子类，使用 class 或 subclass 的 discriminator-value 属性指定辨别者列的值 
所有子类定义的字段都不能有非空约束。如果为那些字段添加非空约束，那么父类的实例在那些列其实并没有值，这将引起数据库完整性冲突，导致父类的实例无法保存到数据库中.
```
| ID | TYPE | NAME | AGE | SCHOOL |
| --- | --- | --- | --- | --- |
| 1 | person | AA | 11 | (NULL) |
| 2 | Student | BB | 22 | school1|

#### 采用 joined-subclass 元素的继承映射
```
采用 joined-subclass 元素的继承映射可以实现每个子类一张表
采用这种映射策略时，父类实例保存在父类表中，子类实例由父类表和子类表共同存储。因为子类实例也是一个特殊的父类实例，因此必然也包含了父类实例的属性。于是将子类和父类共有的属性保存在父类表中，子类增加的属性，则保存在子类表中。
在这种映射策略下，无须使用鉴别者列，但需要为每个子类使用 key 元素映射共有主键。
子类增加的属性可以添加非空约束。因为子类的属性和父类的属性没有保存在同一个表中 。
```
```
@Test
public void testSave(){
   // 下面1条insert
   Person person = new Person();
   person.setAge(33);
   person.setName("CC");
   
   session.save(person);
   System.out.println("-------------------");

   // 下面2条insert 先插Person再插Student
   Student stu = new Student();
   stu.setAge(44);
   stu.setName("DD");
   stu.setSchool("school-1");
   
   session.save(stu);
}

```
person表：
|ID|NAME|AGE|
|--|--|--|
|1|CC|33|
|2|DD|44|

students表：
|Student_id|school|
|---|---|
| 2 | school-1 |

#### 采用 union-subclass 元素的继承映射
```
采用 union-subclass 元素可以实现将每一个实体对象映射到一个独立的表中。 子类增加的属性可以有非空约束 --- 即父类实例的数据保存在父表中，而子类实例的数据保存在子类表中。
子类实例的数据仅保存在子类表中, 而在父类表中没有任何记录 在这种映射策略下，子类表的字段会比父类表的映射字段要多,因为子类表的字段等于父类表的字段、加子类增加属性的总和 在这种映射策略下，既不需要使用鉴别者列，也无须使用 key 元素来映射共有主键. 
使用 union-subclass 映射策略是不可使用 identity 的主键生成策略, 因为同一类继承层次中所有实体类都需要使用同一个主键种子, 即多个持久化实体对应的记录的主键应该是连续的. 受此影响, 也不该使用 native 主键生成策略, 因为 native 会根据数据库来选择使用 identity 或 sequence. 
```
person表： （其实query Person size是有2条记录的）
| ID | NAME | AGE|
| -- | -- | -- |
|98304| AA | 20 |
Students 表：
| ID | NAME | AGE | School |
| -- | -- | -- |
|98305 | BB | 20 | school-1|

#### Hibernate 检索策略
```
检索数据时的 2 个问题： 不浪费内存：当 Hibernate 从数据库中加载 Customer 对象时, 如果同时加载所有关联的 Order 对象, 而程序实际上仅仅需要访问 Customer 对象, 那么这些关联的 Order 对象就白白浪费了许多内存. 更高的查询效率：发送尽可能少的 SQL 语句 
```
#### 类级别的检索策略
```
类级别可选的检索策略包括立即检索和延迟检索, 默认为延迟检索.
立即检索: 立即加载检索方法指定的对象 lazy="false" .
延迟检索: 延迟加载检索方法指定的对象。
在使用具体的属性时，再进行加载 lazy="true" .
类级别的检索策略可以通过 <class> 元素的 lazy 属性进行设置.
如果程序加载一个对象的目的是为了访问它的属性, 可以采取立即检索. 如果程序加载一个持久化对象的目的是仅仅为了获得它的引用, 可以采用延迟检索。
注意出现懒加载异常！ 

无论 <class> 元素的 lazy 属性是 true 还是 false, Session 的 get() 方法及 Query 的 list() 方法在类级别总是使用立即检索策略 
若 <class> 元素的 lazy 属性为 true 或取默认值, Session 的 load() 方法不会执行查询数据表的 SELECT 语句, 仅返回代理类对象的实例, 
该代理类实例有如下特征: 由 Hibernate 在运行时采用 CGLIB 工具动态生成
Hibernate 创建代理类实例时, 仅初始化其 OID 属性. 
System.out.println(dept.getDeptId());
这一句是不会去select的。
在应用程序第一次访问代理类实例的非 OID 属性时, Hibernate 会初始化代理类实例. 
```

#### 一对多和多对多的检索策略
```
在映射文件中, 用 <set> 元素来配置一对多关联及多对多关联关系.
 <set> 元素有 lazy 和 fetch 属性
lazy: 主要决定 orders 集合被初始化的时机. 即到底是在加载 Customer 对象时就被初始化, 还是在程序访问 orders 集合时被初始化
fetch: 取值为 “select” 或 “subselect” 时, 决定初始化 orders 的查询语句的形式;  若取值为”join”, 则决定 orders 集合被初始化的时机 若把 fetch 设置为 “join”, lazy 属性将被忽略 

用带子查询的 select 语句整批量初始化 orders 集合(fetch 属性为 “subselect”) ：
<set> 元素的 fetch 属性: 取值为 “select” 或 “subselect” 时, 决定初始化 orders 的查询语句的形式; 
若取值为”join”, 则决定 orders 集合被初始化的时机.默认值为 select 。
当 fetch 属性为 “subselect” 时 假定 Session 缓存中有 n 个 orders 集合代理类实例没有被初始化, Hibernate 能够通过带子查询的 select 语句, 来批量初始化 n 个 orders 集合代理类实例 。
batch-size 属性将被忽略.
子查询中的 select 语句为查询 CUSTOMERS 表 OID 的 SELECT 语句.

迫切左外连接检索(fetch 属性值设为 “join”)
当 fetch 属性为 “join” 时: 检索 Customer 对象时, 会采用迫切左外连接(通过左外连接加载与检索指定的对象关联的对象)策略来检索所有关联的 Order 对象 。
lazy 属性将被忽略 。
Query 的list() 方法会忽略映射文件中配置的迫切左外连接检索策略, 而依旧采用延迟加载策略 。
```

#### 多对一和一对一关联的检索策略
```
和 <set> 一样, <many-to-one> 元素也有一个 lazy 属性和 fetch 属性. 
若 fetch 属性设为 join, 那么 lazy 属性被忽略
迫切左外连接检索策略的优点在于比立即检索策略使用的 SELECT 语句更少.
无代理延迟检索需要增强持久化类的字节码才能实现 
Query 的 list 方法会忽略映射文件配置的迫切左外连接检索策略, 而采用延迟检索策略 。
如果在关联级别使用了延迟加载或立即加载检索策略, 可以设定批量检索的大小, 以帮助提高延迟检索或立即检索的运行性能.
Hibernate 允许在应用程序中覆盖映射文件中设定的检索策略. 

在映射文件中, 用 <set> 元素来配置一对多关联及多对多关联关系. <set> 元素有 lazy 和 fetch 属性 
lazy: 主要决定 orders 集合被初始化的时机. 即到底是在加载 Customer 对象时就被初始化, 还是在程序访问 orders 集合时被初始化
fetch: 取值为 “select” 或 “subselect” 时, 决定初始化 orders 的查询语句的形式; 
若取值为”join”, 则决定 orders 集合被初始化的时机 若把 fetch 设置为 “join”, lazy 属性将被忽略 

当 fetch 属性为 “subselect” 时 假定 Session 缓存中有 n 个 orders 集合代理类实例没有被初始化, Hibernate 能够通过带子查询的 select 语句, 来批量初始化 n 个 orders 集合代理类实例 
batch-size 属性将被忽略 
子查询中的 select 语句为查询 CUSTOMERS 表 OID 的 SELECT 语句
和 <set> 一样, <many-to-one> 元素也有一个 lazy 属性和 fetch 属性
(fetch 属性设为 join)迫切左外连接检索策略的优点在于比立即检索策略使用的 SELECT 语句更少. 
Query 的list() 方法会忽略映射文件中配置的迫切左外连接检索策略, 而依旧采用延迟加载策略 
如果在关联级别使用了延迟加载或立即加载检索策略, 可以设定批量检索的大小, 以帮助提高延迟检索或立即检索的运行性能. Hibernate 允许在应用程序中覆盖映射文件中设定的检索策略. 

延迟检索和增强延迟检索
在延迟检索(lazy 属性值为 true) 集合属性时, Hibernate 在以下情况下初始化集合代理类实例 .
应用程序第一次访问集合属性: iterator(), size(), isEmpty(), contains() 等方法 
通过 Hibernate.initialize() 静态方法显式初始化
增强延迟检索(lazy 属性为 extra): 与 lazy=“true” 类似.
主要区别是增强延迟检索策略能进一步延迟 Customer 对象的 orders 集合代理实例的初始化时机：
当程序第一次访问 orders 属性的 iterator() 方法时, 会导致 orders 集合代理类实例的初始化
当程序第一次访问 order 属性的 size(), contains() 和 isEmpty() 方法时, Hibernate 不会初始化 orders 集合类的实例, 仅通过特定的 select 语句查询必要的信息, 不会检索所有的 Order 对象 
```
#### 检索策略小结

 

 
### Hibernate 检索方式
#### 概述
```
Hibernate 提供了以下几种检索对象的方式
导航对象图检索方式:  根据已经加载的对象导航到其他对象
OID 检索方式:  按照对象的 OID 来检索对象 session.get|load
HQL 检索方式: 使用面向对象的HQL 查询语言 
QBC 检索方式: 使用 QBC(Query By Criteria) API 来检索对象. 这种 API 封装了基于字符串形式的查询语句, 提供了更加面向对象的查询接口. 
本地 SQL 检索方式: 使用本地数据库的 SQL 查询语句 
```
#### HQL 检索方式
```
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

```
#### HQL vs SQL: 
```
HQL 查询语句是面向对象的, Hibernate 负责解析 HQL 查询语句, 然后根据对象-关系映射文件中的映射信息, 把 HQL 查询语句翻译成相应的 SQL 语句. 
HQL 查询语句中的主体是域模型中的类及类的属性 。
SQL 查询语句是与关系数据库绑定在一起的. SQL 查询语句中的主体是数据库表及表的字段. 
```

#### 绑定参数: 
```
Hibernate 的参数绑定机制依赖于 JDBC API 中的 PreparedStatement 的预定义 SQL 语句功能. 
HQL 的参数绑定由两种形式: 
按参数名字绑定: 在 HQL 查询语句中定义命名参数, 命名参数以 “:” 开头. 
按参数位置绑定: 在 HQL 查询语句中用 “?” 来定义参数位置 .
相关方法:
setEntity(): 把参数与一个持久化类绑定 。
setParameter(): 绑定任意类型的参数. 该方法的第三个参数显式指定 Hibernate 映射类型 

HQL 采用 ORDER BY 关键字对查询结果排序
```
#### 分页查询: 
```
setFirstResult(int firstResult): 设定从哪一个对象开始检索, 参数 firstResult 表示这个对象在查询结果中的索引位置, 索引位置的起始值为 0. 默认情况下, Query 从查询结果中的第一个对象开始检索 。
setMaxResults(int maxResults): 设定一次最多检索出的对象的数目. 在默认情况下, Query 和 Criteria 接口检索出查询结果中所有的对象。
```
#### 在映射文件中定义命名查询语句 
```
Hibernate 允许在映射文件中定义字符串形式的查询语句. 
<query> 元素用于定义一个 HQL 查询语句, 它和 <class> 元素并列. 
在程序中通过 Session 的 getNamedQuery() 方法获取查询语句对应的 Query 对象.
```
#### 投影查询
```
投影查询: 查询结果仅包含实体的部分属性. 通过 SELECT 关键字实现. 
Query 的 list() 方法返回的集合中包含的是数组类型的元素, 每个对象数组代表查询结果的一条记录 。
可以在持久化类中定义一个对象的构造器来包装投影查询返回的记录, 使程序代码能完全运用面向对象的语义来访问查询结果集。

可以通过 DISTINCT 关键字来保证查询结果不会返回重复元素 
```
#### 报表查询
```
报表查询用于对数据分组和统计, 与 SQL 一样, HQL 利用 GROUP BY 关键字对数据分组, 用 HAVING 关键字对分组数据设定约束条件.
在 HQL 查询语句中可以调用以下聚集函数 count() min() max() sum() avg() 
```

#### HQL (迫切)左外连接
```
迫切左外连接: 
LEFT JOIN FETCH 关键字表示迫切左外连接检索策略. 
list() 方法返回的集合中存放实体对象的引用, 每个 Department 对象关联的 Employee  集合都被初始化, 存放所有关联的 Employee 的实体对象. 
查询结果中可能会包含重复元素, 可以通过一个 HashSet 来过滤重复元素 
左外连接: 
LEFT JOIN 关键字表示左外连接查询. 
list() 方法返回的集合中存放的是对象数组类型 .
根据配置文件来决定 Employee 集合的检索策略. 
如果希望 list() 方法返回的集合中仅包含 Department 对象, 可以在HQL 查询语句中使用 SELECT 关键字 
```
#### HQL (迫切)内连接
```
迫切内连接:
INNER JOIN FETCH 关键字表示迫切内连接, 也可以省略 INNER 关键字 .
list() 方法返回的集合中存放 Department 对象的引用, 每个 Department 对象的 Employee 集合都被初始化, 存放所有关联的 Employee 对象 .

内连接: 
INNER JOIN 关键字表示内连接, 也可以省略 INNER 关键字 。
list() 方法的集合中存放的每个元素对应查询结果的一条记录, 每个元素都是对象数组类型 。
如果希望 list() 方法的返回的集合仅包含 Department  对象, 可以在 HQL 查询语句中使用 SELECT 关键字 。
与左外连接的区别：不返回与左表条件不符合的记录 。
```
#### 关联级别运行时的检索策略
```
如果在 HQL 中没有显式指定检索策略, 将使用映射文件配置的检索策略. 
HQL 会忽略映射文件中设置的迫切左外连接检索策略, 如果希望 HQL 采用迫切左外连接策略, 就必须在 HQL 查询语句中显式的指定它 .
若在 HQL 代码中显式指定了检索策略, 就会覆盖映射文件中配置的检索策略 .
```

#### QBC 检索和本地 SQL 检索
```
QBC 查询就是通过使用 Hibernate 提供的 Query By Criteria API 来查询对象，这种 API 封装了 SQL 语句的动态拼装，对查询提供了更加面向对象的功能接口 .

本地SQL查询来完善HQL不能涵盖所有的查询特性 
```

#### Hibernate 二级缓存
```
缓存(Cache): 计算机领域非常通用的概念。它介于应用程序和永久性数据存储源(如硬盘上的文件或者数据库)之间，其作用是降低应用程序直接读写永久性数据存储源的频率，从而提高应用的运行性能。缓存中的数据是数据存储源中数据的拷贝。缓存的物理介质通常是内存.
Hibernate中提供了两个级别的缓存 。
第一级别的缓存是 Session 级别的缓存，它是属于事务范围的缓存。这一级别的缓存由 hibernate 管理的 。
第二级别的缓存是 SessionFactory 级别的缓存，它是属于进程范围的缓存 。
```
#### SessionFactory 级别的缓存
```
SessionFactory 的缓存可以分为两类: 内置缓存: Hibernate 自带的, 不可卸载. 通常在 Hibernate 的初始化阶段, Hibernate 会把映射元数据和预定义的 SQL 语句放到 SessionFactory 的缓存中, 映射元数据是映射文件中数据（.hbm.xml 文件中的数据）的复制。该内置缓存是只读的 。
外置缓存(二级缓存): 一个可配置的缓存插件. 在默认情况下, SessionFactory 不会启用这个缓存插件. 外置缓存中的数据是数据库数据的复制, 外置缓存的物理介质可以是内存或硬盘 
```
#### 使用 Hibernate 的二级缓存
```
适合放入二级缓存中的数据: 
很少被修改
不是很重要的数据, 允许出现偶尔的并发问题 
不适合放入二级缓存中的数据: 
经常被修改 
财务数据, 绝对不允许出现并发问题 
与其他应用程序共享的数据 
```
#### Hibernate 二级缓存的架构

 
#### 二级缓存的并发访问策略
```
两个并发的事务同时访问持久层的缓存的相同数据时, 也有可能出现各类并发问题. 
二级缓存可以设定以下 4 种类型的并发访问策略, 每一种访问策略对应一种事务隔离级别 。
读写型(Read-write): 提供 Read Commited 数据隔离级别.对于经常读但是很少被修改的数据, 可以采用这种隔离类型, 因为它可以防止脏读 。
事务型(Transactional): 仅在受管理环境下适用. 它提供了 Repeatable Read 事务隔离级别. 对于经常读但是很少被修改的数据, 可以采用这种隔离类型, 因为它可以防止脏读和不可重复读 。
只读型(Read-Only):提供 Serializable 数据隔离级别, 对于从来不会被修改的数据, 可以采用这种访问策略 。
```
#### 管理 Hibernate 的二级缓存 
```
Hibernate 的二级缓存是进程或集群范围内的缓存 :
二级缓存是可配置的的插件, Hibernate 允许选用以下类型的缓存插件: 
EHCache: 可作为进程范围内的缓存, 存放数据的物理介质可以使内存或硬盘, 对 Hibernate 的查询缓存提供了支持 .
OpenSymphony OSCache:可作为进程范围内的缓存, 存放数据的物理介质可以使内存或硬盘, 提供了丰富的缓存数据过期策略, 对 Hibernate 的查询缓存提供了支持 。
SwarmCache: 可作为集群范围内的缓存, 但不支持 Hibernate 的查询缓存 
JBossCache:可作为集群范围内的缓存, 支持 Hibernate 的查询缓存 .
```
#### 4 种缓存插件支持的并发访问策略
|Concurrency strategy cache provider| Read-only | nonstrict-read-write | Read-write | transactinal |
| -- | -- | -- | -- | -- |
|EHCache|√|√|√| |
|OSCache|√|√|√| |
|SwamCache|√ | √ | | |
|JBossCache|√ | | | √ |

#### 配置进程范围内的二级缓存
```
配置进程范围内的二级缓存的步骤: 
选择合适的缓存插件: EHCache(jar 包和 配置文件), 并编译器配置文件
.
在 Hibernate 的配置文件中启用二级缓存并指定和 EHCache 对应的缓存适配器
.
选择需要使用二级缓存的持久化类, 设置它的二级缓存的并发访问策略
.
<class> 元素的 cache 子元素表明 Hibernate 会缓存对象的简单属性, 但不会缓存集合属性, 若希望缓存集合属性中的元素, 必须在 <set> 元素中加入 <cache> 子元素
.
在 hibernate 配置文件中通过 <class-cache/> 节点配置使用缓存
.

```

 
 

#### ehcache.xml
```
<diskStore>: 指定一个目录：当 EHCache 把数据写到硬盘上时, 将把数据写到这个目录下.
<defaultCache>: 设置缓存的默认数据过期策略 .
<cache> 设定具体的命名缓存的数据过期策略。每个命名缓存代表一个缓存区域
.
缓存区域(region)：一个具有名称的缓存块，可以给每一个缓存块设置不同的缓存策略。如果没有设置任何的缓存区域，则所有被缓存的对象，都将使用默认的缓存策略。即：<defaultCache.../>
。
Hibernate在不同的缓存区域保存不同的类/集合。
对于类而言，区域的名称是类名。如:com.atguigu.domain.Customer
对于集合而言，区域的名称是类名加属性名。如com.atguigu.domain.Customer.orders
。

cache 元素的属性  
name:设置缓存的名字,它的取值为类的全限定名或类的集合的名字.
maxInMemory:设置基于内存的缓存中可存放的对象最大数目 
.
eternal:设置对象是否为永久的,true表示永不过期,此时将忽略.
timeToIdleSeconds 和 timeToLiveSeconds属性; 默认值是false 
.
timeToIdleSeconds:设置对象空闲最长时间,以秒为单位, 超过这个时间,对象过期。当对象过期时,EHCache会把它从缓存中清除。如果此值为0,表示对象可以无限期地处于空闲状态。 
timeToLiveSeconds:设置对象生存最长时间,超过这个时间,对象过期。如果此值为0,表示对象可以无限期地存在于缓存中. 该属性值必须大于或等于 timeToIdleSeconds 属性值 。
overflowToDisk:设置基于内存的缓存中的对象数目达到上限后,是否把溢出的对象写到基于硬盘的缓存中 
。
```

#### 查询缓存
```
对于经常使用的查询语句, 如果启用了查询缓存, 当第一次执行查询语句时, Hibernate 会把查询结果存放在查询缓存中. 以后再次执行该查询语句时, 只需从缓存中获得查询结果, 从而提高查询性能
.
查询缓存使用于如下场合：
应用程序运行时经常使用查询语句
.
很少对与查询语句检索到的数据进行插入, 删除和更新操作
.
启用查询缓存的步骤：
配置二级缓存, 因为查询缓存依赖于二级缓存
。
在 hibernate 配置文件中启用查询缓存。
对于希望启用查询缓存的查询语句, 调用 Query 的 setCacheable() 方法
。
```

#### 时间戳缓存区域
```
时间戳缓存区域存放了对于查询结果相关的表进行插入, 更新或删除操作的时间戳.  
Hibernate 通过时间戳缓存区域来判断被缓存的查询结果是否过期, 其运行过程如下:
T1 时刻执行查询操作, 把查询结果存放在 QueryCache 区域, 记录该区域的时间戳为 T1
.
T2 时刻对查询结果相关的表进行更新操作, Hibernate 把 T2 时刻存放在UpdateTimestampCache 区域.
T3 时刻执行查询结果前, 先比较 QueryCache 区域的时间戳和 UpdateTimestampCache 区域的时间戳, 若 T2 >T1, 那么就丢弃原先存放在 QueryCache 区域的查询结果, 重新到数据库中查询数据, 再把结果存放到 QueryCache 区域; 若 T2 < T1, 直接从 QueryCache 中获得查询结果
.

```

#### Query 接口的 iterate() 方法
```
Query 接口的 iterator() 方法
同 list() 一样也能执行查询操作
.
list() 方法执行的 SQL 语句包含实体类对应的数据表的所有字段
.
Iterator() 方法执行的SQL 语句中仅包含实体类对应的数据表的 ID 字段
.
当遍历访问结果集时, 该方法先到 Session 缓存及二级缓存中查看是否存在特定 OID 的对象, 如果存在, 就直接返回该对象, 如果不存在该对象就通过相应的 SQL Select 语句到数据库中加载特定的实体对象
大多数情况下, 应考虑使用 list() 方法执行查询操作. iterator() 方法仅在满足以下条件的场合, 可以稍微提高查询性能：
要查询的数据表中包含大量字段
.
启用了二级缓存, 且二级缓存中可能已经包含了待查询的对象
.
```

#### 管理 Session
```
Hibernate  自身提供了三种管理 Session 对象的方法
:
Session 对象的生命周期与本地线程绑定
.
Session 对象的生命周期与 JTA 事务绑定
.
Hibernate 委托程序管理 Session 对象的生命周期
.
在 Hibernate 的配置文件中, hibernate.current_session_context_class 属性用于指定 Session 管理方式, 可选值包括：
thread: Session 对象的生命周期与本地线程绑定
。
jta*: Session 对象的生命周期与 JTA 事务绑定。
managed: Hibernate 委托程序来管理 Session 对象的生命周期
。

```

#### Session 对象的生命周期与本地线程绑定
```
如果把 Hibernate 配置文件的 hibernate.current_session_context_class 属性值设为 thread, Hibernate 就会按照与本地线程绑定的方式来管理 Session.
Hibernate 按一下规则把 Session 与本地线程绑定
.
当一个线程(threadA)第一次调用 SessionFactory 对象的 getCurrentSession() 方法时, 该方法会创建一个新的 Session(sessionA) 对象, 把该对象与 threadA 绑定, 并将 sessionA 返回 。
当 threadA 再次调用 SessionFactory 对象的 getCurrentSession() 方法时, 该方法将返回 sessionA 对象
。
当 threadA 提交 sessionA 对象关联的事务时, Hibernate 会自动flush sessionA 对象的缓存, 然后提交事务, 关闭 sessionA 对象. 当 threadA 撤销 sessionA 对象关联的事务时, 也会自动关闭 sessionA 对象
。
若 threadA 再次调用 SessionFactory 对象的 getCurrentSession() 方法时, 该方法会又创建一个新的 Session(sessionB) 对象, 把该对象与 threadA 绑定, 并将 sessionB 返回 。
```

#### 批量处理数据
```
批量处理数据是指在一个事务中处理大量数据.
在应用层进行批量操作, 主要有以下方式:
通过 Session

通过 HQL 

通过 StatelessSession

通过 JDBC API
```
#### 通过 Session 来进行批量操作
```
Session 的 save() 及 update() 方法都会把处理的对象存放在自己的缓存中. 如果通过一个 Session 对象来处理大量持久化对象, 应该及时从缓存中清空已经处理完毕并且不会再访问的对象. 具体的做法是在处理完一个对象或小批量对象后, 立即调用 flush() 方法刷新缓存, 然后在调用 clear() 方法清空缓存.
通过 Session 来进行处理操作会受到以下约束
.
需要在  Hibernate 配置文件中设置 JDBC 单次批量处理的数目, 应保证每次向数据库发送的批量的 SQL 语句数目与 batch_size 属性一致.
若对象采用 “identity” 标识符生成器, 则 Hibernate 无法在 JDBC 层进行批量插入操作
.
进行批量操作时, 建议关闭 Hibernate 的二级缓存
.

批量更新: 在进行批量更新时, 如果一下子把所有对象都加载到 Session 缓存, 然后再缓存中一一更新, 显然是不可取的
.
使用可滚动的结果集 org.hibernate.ScrollableResults, 该对象中实际上并不包含任何对象, 只包含用于在线定位记录的游标.
只有当程序遍历访问 ScrollableResults 对象的特定元素时, 它才会到数据库中加载相应的对象. 
org.hibernate.ScrollableResults 对象由 Query 的 scroll 方法返回
.
注意: HQL 只支持 INSERT INTO … SELECT 形式的插入语句, 但不支持 INSERT INTO … VALUES 形式的插入语句. 所以使用 HQL 不能进行批量插入操作. 

```

 #### 通过StatelessSession来进行批量操作
```
从形式上看，StatelessSession与session的用法类似。StatelessSession与session相比，有以下区别:
StatelessSession没有缓存，通过StatelessSession来加载、保存或更新后的对象处于游离状态。
StatelessSession不会与Hibernate的第二级缓存交互。
当调用StatelessSession的save()、update()或delete()方法时，这些方法会立即执行相应的SQL语句，而不会仅计划执行一条SQL语句.
StatelessSession不会进行脏检查，因此修改了Customer对象属性后，还需要调用StatelessSession的update()方法来更新数据库中数据。
StatelessSession不会对关联的对象进行任何级联操作。
通过同一个StatelessSession对象两次加载OID为1的Customer对象，得到的两个对象内存地址不同。
StatelessSession所做的操作可以被Interceptor拦截器捕获到，但是会被Hibernate的事件处理系统忽略掉。
```
 