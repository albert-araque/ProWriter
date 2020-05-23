package dao;


import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import model.Capitulo;
import model.Escena;
import model.Libro;
import model.Localidad;
import model.Personaje;
import model.Proyecto;

public class CrudManager {
	
	static Session session;
	
	public static synchronized Integer add(Object object) {
		session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Integer id = 0;
		
		try {
			transaction = session.beginTransaction();
			id = (int) session.save(object);
			transaction.commit();
		} catch (HibernateException exception)  {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}
		
		return id;
	}
	
	public static synchronized Object get(Integer idObject, Class<?> objectClass) {
		session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Object object = null;
		
		try {
			transaction = session.beginTransaction();
			object = session.get(objectClass, idObject);
			initializeSet(object);
			transaction.commit();
		} catch (HibernateException exception)  {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}		
		
		return object;
	}
	
	public static synchronized void update(Object object) {
		session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();
			session.update(object);
			transaction.commit();
		} catch (HibernateException exception)  {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}
	}
	
	public static synchronized Integer remove(Integer idObject, Class<?> objectClass) {
		session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Object object = null;
		
		try {
			transaction = session.beginTransaction();
			object = session.get(objectClass, idObject);
			idObject = getId(object);
			session.delete(object);
			transaction.commit();
		} catch (HibernateException exception)  {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}		
		
		return idObject;
	}
	
	public static synchronized Object[] getList(String className, Class<?> objectClass) {		
		session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Object[] objectList = null;
		
		try {
			transaction = session.beginTransaction();
			objectList = (Object[]) session.createQuery("FROM " + className, objectClass).list().toArray();
			
			for (Object object : objectList) {
				initializeSet(object);
			}			
		} catch (HibernateException exception)  {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}
		
		return objectList;
	}
	
	public static void closeSession() {
		session.close();
	}
	
	private static synchronized void initializeSet(Object object) {
		
		if (object instanceof Proyecto) {
			Hibernate.initialize(((Proyecto) object).getLibros());
		} else if (object instanceof Capitulo) {
			Hibernate.initialize(((Capitulo) object).getEscenas());
		} else if (object instanceof Escena) {
			Hibernate.initialize(((Escena) object).getPersonajes());
			Hibernate.initialize(((Escena) object).getLocalidad());
		} else if (object instanceof Libro) {
			Hibernate.initialize(((Libro) object).getCapitulos());
			Hibernate.initialize(((Libro) object).getPersonajes());
			Hibernate.initialize(((Libro) object).getProyectos());
		} else if (object instanceof Localidad) {
			Hibernate.initialize(((Localidad) object).getEscenas());
		} else if (object instanceof Personaje) {
			Hibernate.initialize(((Personaje) object).getEscenas());
			Hibernate.initialize(((Personaje) object).getLibros());
		}
	}
	
	private static synchronized Integer getId(Object object) {
		
		if (object instanceof Proyecto) {
			return ((Proyecto) object).getId();
		} else if (object instanceof Capitulo) {
			return ((Capitulo) object).getId();
		} else if (object instanceof Escena) {
			return ((Escena) object).getId();
		} else if (object instanceof Libro) {
			return ((Libro) object).getId();
		} else if (object instanceof Localidad) {
			return ((Localidad) object).getId();
		} else if (object instanceof Personaje) {
			return ((Personaje) object).getId();
		}
		
		return null;		
	}
}
