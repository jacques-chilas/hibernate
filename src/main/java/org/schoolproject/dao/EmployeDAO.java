package org.schoolproject.dao;

// EmployeDAO.java
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.schoolproject.model.Employe;
import org.schoolproject.util.HibernateUtil;

import java.util.Date;
import java.util.List;

public class EmployeDAO {

    // Create a new employee
    public void saveEmploye(Employe employe) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(employe);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Update an existing employee
    public void updateEmploye(Employe employe) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(employe);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Delete an employee
    public void deleteEmploye(Long codeEmp) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Employe employe = session.get(Employe.class, codeEmp);
            if (employe != null) {
                session.remove(employe);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Get employee by ID
    public Employe getEmployeById(Long codeEmp) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Employe.class, codeEmp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all employees
    public List<Employe> getAllEmployes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Employe", Employe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Search employees by name
    public List<Employe> searchEmployesByName(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Employe> query = session.createQuery(
                    "from Employe where lower(nom) like :nom or lower(prenom) like :prenom", Employe.class);
            query.setParameter("nom", "%" + keyword.toLowerCase() + "%");
            query.setParameter("prenom", "%" + keyword.toLowerCase() + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
