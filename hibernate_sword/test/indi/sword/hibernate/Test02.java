package indi.sword.hibernate;

import indi.sword.hibernate.bean.News;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static javax.print.attribute.standard.ReferenceUriSchemesSupported.NEWS;

/**
 * @Description
 * @Author rd_jianbin_lin
 * @Date 20:09 2017/10/1
 */
public class Test02 {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void before(){
        Configuration configuration = new Configuration().configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).buildServiceRegistry();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @After
    public void after(){
        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void testSessionCache(){
        System.out.println("testGet() ..............");
        News news = (News) session.get(News.class,1);
        System.out.println(news);
        System.out.println("----------------------");
        News news2 = (News) session.get(News.class,1); // 由于有缓存，所以只发一次SQL
        System.out.println(news2);
        System.out.println("testGet() ..............");
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
        News news = (News) session.get(News.class,1);
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

    @Test
    public void testUpdate(){

    }
}
