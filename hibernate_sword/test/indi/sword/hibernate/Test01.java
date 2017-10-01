package indi.sword.hibernate;

import indi.sword.hibernate.bean.News;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;
import java.util.Date;


/**
 * @Description 
 * @Author rd_jianbin_lin
 * @Date 10:41 2017/10/1
 */

public class Test01 {

    @Test
    public void test_base(){

        //1、创建一个SessionFactory对象
        SessionFactory sessionFactory = null;

        //a. 创建 Configuration对象，对应hibernate的基本配置信息和对应关系映射信息。
        Configuration configuration = new Configuration().configure();

        //  b. 创建 一个ServiceRegistry对象：hibernate 4.x新增的对象
        //hibernate的任何配置和服务都需要在该对象中注册才有效
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();

        //  c.1 hibernate4.0之前这样创建
//        sessionFactory = configuration.buildSessionFactory();

        //  c.2 获取sessionFacotry
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        //2、创建一个Session对象
        Session session =sessionFactory.openSession();

        //3、开启事务
        Transaction transaction = session.beginTransaction();

        //4、执行保存操作
        News news = new News("JAVA","Jack",new Date());
        session.save(news);

        /*
            当查询的时候，返回的实体类是一个对象实例，是hibernate动态通过反射生成的。
            反射的Claas.forName("className").newInstance();需要都赢的类提供一个无参构造函数
         */
        News news2 = (News) session.get(News.class,1);
        System.out.println("query the hibernate save result --> " + news2);

        //5、提交事务
        transaction.commit();

        //6、关闭Session
        session.clear();

        //7、关闭SessionFactory
        sessionFactory.close();
    }
}
