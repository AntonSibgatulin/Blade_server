package com.AntonSibgatulin.database;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.AntonSibgatulin.Players.Information;
import com.AntonSibgatulin.services.Anticheat;
import com.AntonSibgatulin.user.User;

public class DatabaseModel {
	//0 - not have ban
	//1 ban for cheat
	//2 ban for dos
	//3 ban chat
	public Map<String, User> cache = new HashMap<>();

	public Information getInfo(int id) {
		Information info = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Query q = session.createQuery("FROM Information p WHERE p.userId = :id");
			q.setInteger("id", id);
			q.setMaxResults(1);
			// List resultList = q.list();
			info = (Information) q.uniqueResult();
			// System.out.println(user.login);
			// displayResult(resultList);
			session.getTransaction().commit();
			// displayResult(resultList);

		} catch (HibernateException he) {
			he.printStackTrace();
		}

		return info;
	}

	public User getUser(String login, String password) {
		User user = null;
		/*
		if (cache.get(login) != null) {

			cache.get(login).connection.close();

		}

		 */

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Query q = session.createQuery("FROM User U WHERE U.login = :login AND U.password = :pass");
			q.setString("login", login);
			q.setString("pass", password);
			// List resultList = q.list();
			user = (User) q.uniqueResult();
			// System.out.println(user.login);
			// displayResult(resultList);
			session.getTransaction().commit();

			if(user!=null && user.ban==0) {
				if (cache.get(login) != null) {
					cache.get(login).connection.close();

				}
				cache.put(user.login, user);

			}


			if (user != null) {
				user.info = this.getInfo(user.id);
				user.anticheat = this.getAnticheat(user.id);

			}
			// displayResult(resultList);

		} catch (HibernateException he) {
			he.printStackTrace();
		}

		return user;
	}

	public User getUserForReg(String login, String password) {
		User user = null;
		if (cache.get(login) != null) {
			cache.get(login).connection.close();

		}

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Query q = session.createQuery("FROM User U WHERE U.login = :login AND U.password = :pass");
			q.setString("login", login);
			q.setString("pass", password);
			q.setMaxResults(1);

			// List resultList = q.list();
			Object object = q.uniqueResult();
			if (object != null && object instanceof User) {
				user = (User) object;

			}
			// System.out.println(user.login);
			// displayResult(resultList);
			session.getTransaction().commit();

			// displayResult(resultList);

		} catch (HibernateException he) {
			he.printStackTrace();
		}

		return user;
	}

	public Anticheat getAnticheat(int id) {
		Anticheat anticheat = null;

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Query q = session.createQuery("FROM Anticheat p WHERE p.userId = :id");
			q.setInteger("id", id);
			// List resultList = q.list();
			anticheat = (Anticheat) q.uniqueResult();
			// System.out.println(user.login);
			// displayResult(resultList);
			session.getTransaction().commit();
			// displayResult(resultList);

		} catch (HibernateException he) {
			he.printStackTrace();
		}
		return anticheat;

	}

	public void updateUser(User user) {
		if (user == null)
			return;

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.saveOrUpdate(user);
			session.saveOrUpdate(user.info);
			session.saveOrUpdate(user.anticheat);
			// session.saveOrUpdate(user);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
		}

	}

	public void regUser(User user) {
		if (user == null)
			return;

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.saveOrUpdate(user);

			// session.saveOrUpdate(user);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
		}
		User user1 = getUserForReg(user.login, user.password);
		if (user1 == null)
			return;
		user.info.setUserId(user1.id);
		user.anticheat.setUserId(user1.id);
		if (user == null)
			return;

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			// session.saveOrUpdate(user);
			session.saveOrUpdate(user.info);
			session.saveOrUpdate(user.anticheat);
			// session.saveOrUpdate(user);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
		}
	}

	public User getUserById(String login) {
		User user = null;

		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Query q = session.createQuery("FROM User U WHERE U.login = :login");
			q.setString("login", login);
			q.setMaxResults(1);
			// List resultList = q.list();
			Object object = q.uniqueResult();
			if (object != null && object instanceof User) {
				user = (User) object;
			}
			// System.out.println(user.login);
			// displayResult(resultList);
			session.getTransaction().commit();

			if (user != null) {
				user.info = this.getInfo(user.id);
			}

			// displayResult(resultList);

		} catch (HibernateException he) {
			he.printStackTrace();
		}
		return user;
	}

	public boolean isExist(String login) {
		// TODO Auto-generated method stub
		return (getUserById(login) != null);
	}

}
