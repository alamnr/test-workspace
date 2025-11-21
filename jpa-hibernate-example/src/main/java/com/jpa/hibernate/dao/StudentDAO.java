package com.jpa.hibernate.dao;

import java.util.List;

import com.jpa.hibernate.entities.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class StudentDAO {
    
    private final EntityManagerFactory emf;

    public StudentDAO(EntityManagerFactory emf){
        this.emf = emf;
    }

    // CREATE (Transient -> Managed)
    public Student create(Student s){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(s); // becomes managed
        em.getTransaction().commit();
        em.close();
        System.out.println("Created : " + s);
        return s;
    }

    // READ (Managed while inside EM)
    public Student find(Long id) {
        EntityManager em = emf.createEntityManager();
        Student s = em.find(Student.class, id);
        em.close(); // after close -> detached
        System.out.println("Found (Now Detached)" + s);
        return s;
    }

    // UPDATE (Detached -> Managed via merge)
    public Student update(Student detached){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Student merged = em.merge(detached); 
        em.getTransaction().commit();
        em.close();
        System.out.println("Updated Detached -> Managed :" + merged);
        return merged;
    }

    // Delete (Requires detached to be managed before delete)
    public void delete(Long id){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Student managed = em.find(Student.class, id);
        if(managed!=null){
            em.remove(managed); // Managed -> Removed
            System.out.println("Removed : " + managed);
        } else {
            System.out.println("No student found with id: " + id);
        }

        em.getTransaction().commit();
        em.close();
    }

    // List all for debug

    public List<Student> findAll(){
        EntityManager em = emf.createEntityManager();
        List<Student> list = em.createQuery("from Student", Student.class).getResultList();
        return list;
    }
}
