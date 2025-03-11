package org.schoolproject.dao;

// LieuDAO.java

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.schoolproject.model.Lieu;
import org.schoolproject.util.HibernateUtil;

import java.util.List;

public class LieuDAO {

    // Create a new location
    public void saveLieu(Lieu lieu) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(lieu);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Update an existing location
    public void updateLieu(Lieu lieu) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(lieu);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Delete a location
    public void deleteLieu(Long codeLieu) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Lieu lieu = session.get(Lieu.class, codeLieu);
            if (lieu != null) {
                session.remove(lieu);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Get location by ID
    public Lieu getLieuById(Long codeLieu) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Lieu.class, codeLieu);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all locations
    public List<Lieu> getAllLieux() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Lieu", Lieu.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}