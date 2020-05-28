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

/**
 * 
 * Este CrudManager permite realizar las operaciones basicas CRUD (Create, Read, Update, Delete) con cualquier entidad de la base de datos.
 * 
 * 
 * @author Albert Araque
 * @author Francisco José Ruiz
 * 
 */
public class CrudManager {
	
	static Session session;
	
	/**
	 *  Añade una entidad a la base de datos.
	 * 
	 * @param object la entidad a introducir
	 * @return devuelve el id de la entidad introducida
	 */
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
	
	/**
	 * 
	 * Obtiene una entidad como objeto de la base de datos.
	 * 
	 * @param idObject el id de la entidad que queremos obtener
	 * @param objectClass la clase de la entidad que queremos obtener
	 * @return la entidad
	 */
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
	
	/**
	 * 
	 * Actualiza una entidad de la base de datos.
	 * 
	 * @param object el objeto actualizado
	 */
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
	
	/**
	 * 
	 * Borra una entidad de la base de datos.
	 * 
	 * @param idObject el id de la entidad que queremos borrar
	 * @param objectClass la clase de la entidad que queremos borrar
	 * @return id de la entidad eliminada
	 */
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
	
	/**
	 * 
	 * Obtiene una lista de todas las entidades que hay en una tabla determinada.
	 * 
	 * 
	 * @param className el nombre de la clase de las entitades que queremos obtener
	 * @param objectClass la clase de las entidades que queremos obtener
	 * @return vector de entidades de la clase correspondiente
	 */
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
	
	
	/**
	 * 
	 * Este metodo hace un cast a la clase a la que perteneza el objeto introducido y inizializa todos los Set que tenga.
	 * 
	 * @param object la entidad
	 */
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
	
	/**
	 * 
	 * Este metodo hace una cast a la clase a la que pertenezca el objeto introducido, al utilizar Object como clase general no podemos obtener el 
	 * ID, entonces tenemos que averiguar primero que objeto esta instanciado, hacer un cast y obtener el ID.
	 * 
	 * @param object la entidad de la cual queremos obtener el id
	 * @return el id de la entidad introducida
	 */
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
