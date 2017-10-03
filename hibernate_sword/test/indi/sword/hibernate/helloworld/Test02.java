package indi.sword.hibernate.helloworld;

import indi.sword.hibernate.BaseTest;
import indi.sword.hibernate.helloworld.News;
import org.hibernate.jdbc.Work;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;


/**
 * @Description
 * @Author rd_jianbin_lin
 * @Date 20:09 2017/10/1
 */
public class Test02 extends BaseTest {

    @Test
    public void testSessionCache(){
        News news = (News) session.get(News.class,2);
        System.out.println(news);
        News news2 = (News) session.get(News.class,2); // 由于有缓存，所以只发一次SQL
        System.out.println(news2);
        System.out.println("----------------------------------");
        news.setAuthor("TTT");  // 虽然是两个setAuthor,但是操作的是同一个缓存中的对象
        news2.setAuthor("ggg"); // 虽然是两个setAuthor,但是操作的是同一个缓存中的对象，
        // 所以最后输出的是ggg
        System.out.println(news);
        System.out.println(news2);
    }

    /**
     * flush: 使数据表中的记录和 Session 缓存中的对象的状态保持一致. 为了保持一致, 则可能会发送对应的 SQL 语句.
     * 1. 在 Transaction 的 commit() 方法中: 先调用 session 的 flush 方法, 再提交事务
     * 2. flush() 方法会可能会发送 SQL 语句, 但不会提交事务.
     * 3. 注意: 在未提交事务或显式的调用 session.flush() 方法之前, 也有可能会进行 flush() 操作.
     * 1). 执行 HQL 或 QBC 查询, 会先进行 flush() 操作, 以得到数据表的最新的记录
     * 2). 若记录的 ID 是由底层数据库使用自增的方式生成的, 则在调用 save() 方法时, 就会立即发送 INSERT 语句.
     * 因为 save 方法后, 必须保证对象的 ID 是存在的!
     */
    @Test
    public void testSessionFlush(){
        News news = (News) session.get(News.class,1);
        news.setAuthor("Oracle");

        session.flush(); // Flush()后只是将Hibernate缓存中的数据提交到数据库，但是没真正刷进去
        System.out.println("flush");

        News news2 = (News) session.createCriteria(News.class).uniqueResult();
        System.out.println(news2);
    }

    @Test
    public void testSessionFlush2(){
        News news = new News("English3","teacher3",new Date());
        session.save(news); // 若记录的 ID 是由底层数据库使用自增的方式生成的, 则在调用 save() 方法时, 就会立即发送 INSERT 语句.
    }

    /**
     * refresh(): 会强制发送 SELECT 语句, 以使 Session 缓存中对象的状态和数据表中对应的记录保持一致!
     */
    @Test
    public void testRefresh(){
        News news = (News) session.get(News.class,3);
        System.out.println(news);

        session.refresh(news); // 这里会导致hibernate再发sql语句，把缓存中的news再刷新一遍
        System.out.println(news);
    }

    /**
     * clear(): 清理缓存
     */
    @Test
    public void testClear(){
        News news1  = (News) session.get(News.class,1);
        session.clear();
        News news2 =  (News) session.get(News.class,1); //清除缓存后，继续发sql语句查询
    }

    /**
     * 1. save() 方法
     * 1). 使一个临时对象变为持久化对象
     * 2). 为对象分配 ID.
     * 3). 在 flush 缓存时会发送一条 INSERT 语句.
     * 4). 在 save 方法之前的 id 是无效的
     * 5). 持久化对象的 ID 是不能被修改的!
     * 这个id是hibernate根据数据库生成唯一的。假如下面被你改了，hibernate觉得不是我自己找的，怕被你设置成已经存在的
     * 那么hibernate肯定不认，commit的时候就会报错
     */
    @Test
    public void testSave(){
        News news = new News();
        news.setTitle("1CC");
        news.setAuthor("author");
        news.setDate(new Date());
        news.setId(100);
        System.out.println(news);

        session.save(news); // native 获取id，这个id是hibernate根据数据库生成唯一的。假如下面被你改了，hibernate觉得不是我自己找的，肯定不认，那么就会报错
        System.out.println(news);
        news.setId(101);
//        System.out.println(news); // 能输出，但是无法commit，会报错
    }

    /**
     * persist(): 也会执行 INSERT 操作
     *
     * 和 save() 的区别 :
     * 在调用 persist 方法之前, 若对象已经有 id 了, 则不会执行 INSERT, 而抛出异常
     */
    @Test
    public void testPersist(){
        News news = new News();
        news.setTitle("EEE");
        news.setAuthor("EE");
        news.setDate(new Date());
//        news.setId(200);  // 抛出异常

        session.persist(news);
        System.out.println(news);
    }

    /**
     * get VS load:
     *
     * 1. 执行 get 方法: 会立即加载对象.
     *    执行 load 方法, 若不适用该对象, 则不会立即执行查询操作, 而返回一个代理对象
     *
     *    get 是 立即检索, load 是延迟检索.
     *
     * 2. load 方法可能会抛出 LazyInitializationException 异常: 在需要初始化
     * 代理对象之前已经关闭了 Session
     *
     * 3. 若数据表中没有对应的记录, Session 也没有被关闭.
     *    get 返回 null(事情办不了就说办不了)
     *    load 若不使用该对象的任何属性, 没问题; 若需要初始化了, 抛出异常.（答应了一件事，办的时候发现办不了了）
     */
    @Test
    public void testGet(){
        News news = (News) session.get(News.class,1);
        System.out.println("------------------------");
        session.clear();
        System.out.println(news);
    }

    @Test
    public void testLoad(){
        News news = (News) session.load(News.class,1);
        System.out.println("------------------------");
        session.clear(); // 延时加载
        System.out.println(news);
    }

    /**
     * update:
     * 1. 若更新一个持久化对象, 不需要显示的调用 update 方法. 因为在调用 Transaction
     * 的 commit() 方法时, 会先执行 session 的 flush 方法.
     * 2. 更新一个游离对象, 需要显式的调用 session 的 update 方法. 可以把一个游离对象
     * 变为持久化对象
     *
     * 需要注意的:
     * 1. 无论要更新的游离对象和数据表的记录是否一致, 都会发送 UPDATE 语句.
     *    如何能让 updat 方法不再盲目的出发 update 语句呢 ? 在 .hbm.xml 文件的 class 节点设置
     *    select-before-update=true (默认为 false). 但通常不需要设置该属性. 除非与触发器并发操作的话就需要设置
     *
     * 2. 若数据表中没有对应的记录, 但还调用了 update 方法, 会抛出异常
     *
     * 3. 当 update() 方法关联一个游离对象时,
     * 如果在 Session 的缓存中已经存在相同 OID 的持久化对象, 会抛出异常. 因为在 Session 缓存中
     * 不能有两个 OID 相同的对象!
     *
     */
    @Test
    public void testDynamicUpdate(){
        News news = (News) session.get(News.class,1);
        news.setAuthor("GGG"); // 新一个持久化对象, 不需要显示的调用 update 方法. 因为在调用 Transaction 的 commit() 方法时, 会先执行 session 的 flush 方法.
    }
    @Test
    public void testUpdate02(){
        News news = (News) session.get(News.class,1);
        news.setAuthor("GGG");
        session.clear(); // 清空了缓冲区，update不到任何东西
    }
    @Test
    public void testUpdate03(){
        News news = (News) session.get(News.class, 1);

        transaction.commit();
        session.close(); //这个session关了，那么session中的news缓存也就消失了

        session = sessionFactory.openSession(); //新开了一个新的session
        transaction = session.beginTransaction(); //新开了一个新的transaction事务
        news.setAuthor("PPPPPP"); //数据表里面有news的对应记录，但是不在任何一个session缓存里面，变成了一个游离对象

        // 这个要注释掉，不然的话，抛异常。session缓存里面会有两个id一模一样的对象，hibernate将对象持久化的时候，不知道持久化哪个
//        News news2 = (News) session.get(News.class,1);

        // session 的 update 方法. 可以把一个游离对象Detached 变为持久化对象
        //            save方法可以把一个临时对象 Transient（比如NEW出来的） 变成持久化对象
        session.update(news);
    }

    /**
     * 正常逻辑下，你saveOrUpdate一个临时对象（new）的时候，是没有id的。这时候hibernate会调用save()
     * 假如是SaveOrUpdate 一个游离对象（get or load)的时候，是有带数据库ID的，这时候hibernate会调用update()
     *
     * 注意:
     * 1. 若 OID 不为 null, 但数据表中还没有和其对应的记录. 会抛出一个异常. news.setId(11);
     * 2. 了解: OID 值等于 id 的 unsaved-value 属性值的对象, 也被认为是一个游离对象
     */
    @Test
    public void testSaveOrUpdate(){
        News news = new News("LJB","LJB",new Date());

//        news.setId(1); //这个会报错的
        session.saveOrUpdate(news);
    }

    /**
     * delete: 执行删除操作. 只要 OID 和数据表中一条记录对应, 就会准备执行 delete 操作
     * 若 OID 在数据表中没有对应的记录, 则抛出异常
     *
     * 可以通过设置 hibernate 配置文件 hibernate.use_identifier_rollback 为 true,
     * 使删除对象后, 把其 OID 置为  null
     */
    @Test
    public void testDelete(){
        News news = new News();
        news.setId(1);
        news.setTitle("GG"); // 利用 transient对象del

//        News news = (News) session.get(News.class, 1); // 利用persist对象del
        session.delete(news);
        System.out.println("----------------");
        System.out.println(news); // 是这句与上面那句输出之后再发送delete语句
    }


    /**
     * evict: 从 session 缓存中把指定的持久化对象移除
     */
    @Test
    public void testEvict(){
        News news1 = (News) session.get(News.class,2);
        News news2 = (News) session.get(News.class,3);
//        System.out.println(news1);
//        System.out.println(news2);
        news1.setAuthor("555");
        news2.setAuthor("666");
        System.out.println(news1);
        System.out.println(news2);

        session.evict(news2); // 从 session 缓存中把 news2 移除
    }

    @Test
    public void testDoWork(){
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                System.out.println(connection);

            }
        });
    }
    @Test
    public void testBlob() throws Exception{
//		News news = new News();
//		news.setAuthor("cc");
//		news.setContent("CONTENT");
//		news.setDate(new Date());
//		news.setDesc("DESC");
//		news.setTitle("CC");
//
//		InputStream stream = new FileInputStream("Hydrangeas.jpg");
//		Blob image = Hibernate.getLobCreator(session)
//				              .createBlob(stream, stream.available());
//		news.setImage(image);
//
//		session.save(news);

        News news = (News) session.get(News.class, 1);
        Blob image = news.getImage();

        InputStream in = image.getBinaryStream();
        System.out.println(in.available());
    }
}
