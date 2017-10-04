package indi.sword.hibernate.joined_subclass;

import java.util.List;

import indi.sword.hibernate.BaseTest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest extends BaseTest {

	/**
	 * 优点:
	 * 1. 不需要使用了辨别者列.
	 * 2. 子类独有的字段能添加非空约束.
	 * 3. 没有冗余的字段. 
	 */
	
	/**
	 * 查询:
	 * 1. 查询父类记录, 做一个左外连接查询
	 * 2. 对于子类记录, 做一个内连接查询. 
	 */
	@Test
	public void testQuery(){
		List<Person> persons = session.createQuery("FROM Person").list();
		System.out.println(persons.size()); 
		
		List<Student> stus = session.createQuery("FROM Student").list();
		System.out.println(stus.size()); 
	}
	
	/**
	 * 插入操作: 
	 * 1. 对于子类对象至少需要插入到两张数据表中. 
	 */
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

}
