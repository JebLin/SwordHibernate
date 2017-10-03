package indi.sword.hibernate.helloworld;

import indi.sword.hibernate.BaseTest;
import org.junit.Test;

/**
 * @Description 测试关联
 * @Author rd_jianbin_lin
 * @Date 20:48 2017/10/2
 */
public class TestComponent extends BaseTest {

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
