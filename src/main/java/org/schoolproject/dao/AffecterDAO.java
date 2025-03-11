package org.schoolproject.dao;


// AffecterDAO.java
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.schoolproject.model.Affecter;
import org.schoolproject.model.AffecterPK;
import org.schoolproject.model.Employe;
import org.schoolproject.model.Lieu;
import org.schoolproject.util.HibernateUtil;

import javax.swing.*;
import java.util.Date;
import java.util.List;

public class AffecterDAO {


    // Create a new assignment with employee, location and date
    public void saveAffectation(Employe employe, Lieu lieu, Date date) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Ensure Employe and Lieu are managed
            Employe managedEmploye = session.get(Employe.class, employe.getCodeEmp());
            Lieu managedLieu = session.get(Lieu.class, lieu.getCodeLieu());

            if (managedEmploye == null || managedLieu == null) {
                throw new IllegalStateException("Employe or Lieu not found in database");
            }

            // Create Affecter entity with managed entities
            Affecter affecter = new Affecter(managedEmploye, managedLieu, date);
            session.persist(affecter);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Update an existing assignment
    public void updateAffectation(Affecter affecter) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(affecter);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Delete an assignment
    public void deleteAffectation(AffecterPK id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Affecter affecter = session.get(Affecter.class, id);
            if (affecter != null) {
                session.remove(affecter);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Get assignment by composite ID
    public Affecter getAffectationById(AffecterPK id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Affecter.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all assignments
    public List<Affecter> getAllAffectations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Affecter", Affecter.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get assignments for a specific employee
    public List<Affecter> getAffectationsByEmploye(Employe employe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Affecter where employe = :employe", Affecter.class)
                    .setParameter("employe", employe)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get assignments for a specific location
    public List<Affecter> getAffectationsByLieu(Lieu lieu) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Affecter where lieu = :lieu", Affecter.class)
                    .setParameter("lieu", lieu)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
