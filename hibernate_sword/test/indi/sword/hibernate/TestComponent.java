package indi.sword.hibernate;

import indi.sword.hibernate.bean.Pay;
import indi.sword.hibernate.bean.Worker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Description
 * @Author rd_jianbin_lin
 * @Date 20:48 2017/10/2
 */
public class TestComponent {

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
        System.out.println();
    }

    @After
    public void after(){
        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void testComponent(){
        Worker worker = new Worker();
        Pay pay = new Pay();

        pay.setMonthlyPay(1000);
        pay.setYearPay(80000);
        pay.setVocationWithPay(5);

        worker.setName("ABCD");
        worker.setPay(pay);

        session.save(worker);
    }
}
