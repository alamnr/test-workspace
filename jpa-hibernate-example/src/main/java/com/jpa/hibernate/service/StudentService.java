package com.jpa.hibernate.service;

import java.util.List;

import com.jpa.hibernate.dao.StudentDAO;
import com.jpa.hibernate.dao.StudentDAO_V2;
import com.jpa.hibernate.entities.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class StudentService {
    
    private final EntityManagerFactory emf;

    public StudentService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Student createStudent(String name, int age) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Student created = null ; 

        try {

            tx.begin();
            StudentDAO_V2 dao = new StudentDAO_V2(em);
            Student s = new Student(name, age);
            dao.create(s);
            tx.commit();
            created = s;
            System.out.println("Student created :" + s );

        } catch (Exception ex) {
            if(tx.isActive()) {
                tx.rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }

        return created;
    }

    public Student updateStudentAge(Long id, int newAge) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Student updated = null;
        try {

            tx.begin();
            StudentDAO_V2 dao = new StudentDAO_V2(em);
            Student s = dao.find(id);
            if(s!=null){
                s.setAge(newAge);
                updated = dao.update(s);
            }
            tx.commit();
            System.out.println("Student updated " + updated);

        } catch (Exception e) {
            if(tx.isActive()){
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
        return updated;
    }

    public void deleteStudent(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            StudentDAO_V2 dao = new StudentDAO_V2(em);
            dao.delete(id);
            tx.commit();
            System.out.println("Student deleted, id = " + id);
        } catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }

    }

    public List<Student> getAllStudents() {
        EntityManager em = emf.createEntityManager();
        List<Student> list = new StudentDAO_V2(em).findAll();
        em.close();
        return list;
    }

}
