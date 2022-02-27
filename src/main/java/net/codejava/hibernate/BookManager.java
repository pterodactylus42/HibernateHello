package net.codejava.hibernate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class BookManager {

	protected SessionFactory sessionFactory;

	protected void setup() {
		// code to load Hibernate Session factory

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	protected void exit() {
		// code to close Hibernate Session factory
		System.out.println("Closing session factory...");
		sessionFactory.close();
		System.exit(0);
	}

	protected void create() {
		// code to save a book
		Book book = new Book();
		book.setTitle("Effective Java");
		book.setAuthor("Joshua Bloch");
		book.setPrice(32.59f);

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		// select CAN be omitted in hql queries::::
		Query<Book> query = session.createQuery("from Book where title = :title");
		query.setString("title", book.getTitle());
		List<Book> results = query.list();
		if(results.size()>0) {
			System.out.printf("Got %d results for title %s ...\n", results.size(), book.getTitle());
			System.out.println("Title already exists, exiting without save.");
			return;
		}

		System.out.println("Saving entity.");
		session.save(book);

		session.getTransaction().commit();
		session.close();
	}

	protected void read() {
		// code to get a book
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		Query<Book> query = session.createQuery("from Book");
		List<Book> results = query.list();
		
		if(results.size() > 0) {
			Book book = results.get(0);

			System.out.println("Title: " + book.getTitle());
			System.out.println("Author: " + book.getAuthor());
			System.out.println("Price: " + book.getPrice());
		}

		session.close();
	}

	protected void update() {
		// code to modify a book
		Book book = new Book();
		book.setId(20);
		book.setTitle("Ultimate Java Programming");
		book.setAuthor("Nam Ha Minh");
		book.setPrice(19.99f);

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		Query<Book> query = session.createQuery("from Book where book_id = :id");
		query.setInteger("id", (int)book.getId());
		List<Book> results = query.list();
		
		if(results.size() > 0) {
			System.out.printf("Updating book with id %d ...\n", book.getId());
			session.update(book);
		} else {
			System.out.printf("update: No book with id %d found.\n", book.getId());			
		}

		session.getTransaction().commit();
		session.close();
	}

	protected void delete() {
		// code to remove a book
		Book book = new Book();
		book.setId(20);

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		Query<Book> query = session.createQuery("from Book where book_id = :id");
		query.setInteger("id", (int)book.getId());
		List<Book> results = query.list();
		
		if(results.size() > 0) {
			System.out.printf("Deleting book with id %d ...\n", book.getId());
			session.delete(book);
		} else {
			System.out.printf("delete: No book with id %d found.\n", book.getId());			
		}

		session.getTransaction().commit();
		session.close();
	}

	public static Session getCurrentSession() {
		// Hibernate 5.4 SessionFactory example,
		// getting your Session without XML
		Map<String, String> settings = new HashMap<>();
		settings.put("connection.driver_class", "com.mysql.jdbc.Driver");
		settings.put("dialect", "org.hibernate.dialect.MySQL8Dialect");
		settings.put("hibernate.connection.url", 
				"jdbc:mysql://localhost/bookstore");
		settings.put("hibernate.connection.username", "username");
		settings.put("hibernate.connection.password", "password");
		settings.put("hibernate.current_session_context_class", "thread");
		settings.put("hibernate.show_sql", "true");
		settings.put("hibernate.format_sql", "true");

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(settings).build();

		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		Metadata metadata = metadataSources.buildMetadata();

		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
		Session session = sessionFactory.getCurrentSession();
		return session;
	}    

	public static void main(String[] args) {
		// code to run the program
		List<Task<Long>> tedious;
		// Session mySession = getCurrentSession();
		// System.out.println("BookManager.getCurrentSession() successful.");

		BookManager manager = new BookManager();
		manager.setup();
		System.out.println("BookManager.setup() successful.");


		System.out.println("BookManager.create().");
		manager.create();
		manager.read();
		
		System.out.println("BookManager.update().");
		manager.update();
		manager.read();

		System.out.println("BookManager.delete().");
		manager.delete();
		manager.read();
		
		System.out.println("BookManager.create().");
		manager.create();
		manager.read();

		manager.exit();

	}
}
