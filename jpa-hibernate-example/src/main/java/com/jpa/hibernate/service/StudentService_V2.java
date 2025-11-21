package com.jpa.hibernate.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpa.hibernate.dao.StudentDAO_V3;
import com.jpa.hibernate.entities.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class StudentService_V2 {

    private static final Logger log = LoggerFactory.getLogger(StudentService_V2.class);
    private final EntityManagerFactory emf;
    private final StudentDAO_V3 dao; // injected dependency

    public StudentService_V2 ( EntityManagerFactory emf, StudentDAO_V3 dao) {
        this.emf = emf;
        this.dao = dao;
    }


    public Student createStudent(String name, int age){
        EntityManager em  = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Student created = null;
        try {
            tx.begin();
            dao.setEntityManager(em);
            Student s = new Student(name, age);
            dao.create(s);
            tx.commit();
            created = s;
            System.out.println("✔ Student created : " +s);

        } catch (Exception e) {
            if(tx.isActive()){
                tx.rollback();
            }
            log.error("Error - ", e);
            //e.printStackTrace(); 
        } finally {
            em.close();
        }

        return created;
    }

    public  Student updateStudentAge(Long id, int newAge) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Student updated = null;
        try {
            tx.begin();
            dao.setEntityManager(em);
            Student s = dao.find(id);
            if(null != s){
                s.setAge(newAge);
                updated = dao.update(s);
            }
            tx.commit();
        } catch (Exception e) {
            if(tx.isActive()){
                tx.rollback();
            }
            log.error("Error - ", e);
        } finally {
            em.close();
        }
        return updated;
    }

    public void deleteStudent(Long id){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            dao.setEntityManager(em);
            dao.delete(id);
            tx.commit();

        } catch (Exception e) {
            if(tx.isActive()){
                tx.rollback();
            }
            log.error("Error - ", e);
        } finally {
            em.close();
        }
    }

    public Student findStudent(Long id) {
        EntityManager em = emf.createEntityManager();
               
        dao.setEntityManager(em);
        Student  found = dao.find(id);
        em.close();
        
        return found;        
    }

    public List<Student> getAllStudents(){
        EntityManager em = emf.createEntityManager();
        dao.setEntityManager(em);
        List<Student> list = dao.findAll();
        em.close();
        return list;
    }
}
