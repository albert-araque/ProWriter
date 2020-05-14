package dao;

import model.Capitulo;

public class CapituloHibernate implements CapituloDAO {

	@Override
	public Integer addCapitulo(Capitulo capitulo) {
		/*Session session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Integer idCapitulo = null;
		
		try {
			transaction = session.beginTransaction();
			idCapitulo = (int) session.save(capitulo);
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}
		
		return idCapitulo;*/
		return CrudManager.add(capitulo);
	}

	@Override
	public Capitulo getCapitulo(Integer idCapitulo) {
		/*Session session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Capitulo capitulo = null;
		
		try {
			transaction = session.beginTransaction();
			capitulo = session.get(Capitulo.class, idCapitulo);
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}*/
		
		return (Capitulo) CrudManager.get(idCapitulo, Capitulo.class);
	}

	@Override
	public void updateCapitulo(Capitulo updatedCapitulo) {
		/*Session session = SessionFactoryUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();
			session.update(updatedCapitulo);
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null) transaction.rollback();
		} finally {
			session.close();
		}*/
		CrudManager.update(updatedCapitulo);
	}

	@Override
	public Integer removeCapitulo(Integer idCapitulo) {
		return CrudManager.remove(idCapitulo, Capitulo.class);
	}

}
