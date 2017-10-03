package indi.sword.hibernate.one2one.foreign;

import indi.sword.hibernate.BaseTest;
import org.junit.Test;

/**
 * @Description
 * @Author rd_jianbin_lin
 * @Date 11:29 2017/10/3
 */
public class Test01 extends BaseTest {

    @Test
    public void testSave(){

        Department department = new Department();
        department.setDeptName("DEPT-CC");

        Manager manager = new Manager();
        manager.setMgrName("MGR-CC");

        // 设定关联关系
        department.setMgr(manager);
        manager.setDept(department);

        // 保存操作
        // 建议先保存没有外键列的那个对象，这样会减少UPDATE语句
        // 两个insert
        session.save(manager);
        session.save(department);

        // 两个 insert 一个update
//        session.save(department);
//        session.save(manager);
    }

    @Test
    public void testGet(){

        // Get相比load，主实体早就加载了
        Department dept = (Department) session.get(Department.class,1);
        System.out.println("----------------");
        System.out.println(dept.getDeptName());


        //1. 默认情况下对关联属性使用懒加载，下面还不加载
        Manager mgr = dept.getMgr();
        System.out.println(mgr.getClass().getName());
        System.out.println("------------------");
        // 下面才发select
        //3. 查询 Manager 对象的连接条件应该是 dept.manager_id = mgr.manager_id
        //而不应该是 dept.dept_id = mgr.manager_id 这部分在xml中的one2one的property-ref="mgr"配置
        System.out.println(mgr.getMgrName());

    }

    @Test
    public void testGet2(){
        //在查询没有外键的实体对象时, 使用的左外连接查询, 一并查询出其关联的对象
        //并已经进行初始化. 关联对象都一起查出来了。
        Manager mgr = (Manager) session.get(Manager.class,1);
        System.out.println(mgr.getMgrName());

        System.out.println("---------------");
        System.out.println(mgr.getDept());
        System.out.println(mgr.getDept().getDeptName());

    }
}
