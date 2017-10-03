package indi.sword.hibernate.n2n;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import indi.sword.hibernate.BaseTest;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest extends BaseTest{

	@Test
	public void testGet2(){
		Item item = (Item) session.get(Item.class,1);
		System.out.println(item.getName());
	}

	@Test
	public void testGet(){
		Category category = (Category) session.get(Category.class, 1);
		System.out.println(category.getName());
		System.out.println("---------------------");
		//需要连接中间表
		Set<Item> items = category.getItems();
		System.out.println(items.size()); 
	}
	
	@Test
	public void testSave(){
		Category category1 = new Category();
		category1.setName("C-AA");

		Category category2 = new Category();
		category2.setName("C-BB");
		
		Item item1 = new Item();
		item1.setName("I-AA");
		
		Item item2 = new Item();
		item2.setName("I-BB");
		
		//设定关联关系
		category1.getItems().add(item1);
		category1.getItems().add(item2);
		
		category2.getItems().add(item1);
		category2.getItems().add(item2);
		
		item1.getCategories().add(category1);
		item1.getCategories().add(category2);
		
		item2.getCategories().add(category1);
		item2.getCategories().add(category2);
		
		//执行保存操作
		session.save(category1);
		session.save(category2);
		
		session.save(item1);
		session.save(item2);
	}

}
