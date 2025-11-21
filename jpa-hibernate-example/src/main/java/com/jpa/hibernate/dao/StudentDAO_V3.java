
package com.jpa.hibernate.dao;

import java.util.List;

import com.jpa.hibernate.entities.Student;

import jakarta.persistence.EntityManager;

public class StudentDAO_V3 {
    
    private EntityManager em;

    public StudentDAO_V3(EntityManager em){
        this.em = em;
    }

    public void setEntityManager(EntityManager em){
        this.em = em;
    }

    public void create(Student s){
        em.persist(s);
    }

    public Student find(Long id){
        return em.find(Student.class, id);
    }

    public Student update(Student s) {
        return em.merge(s);
    }

    public void delete(Long id){
        Student found = em.find(Student.class, id);
        if(found != null){
            em.remove(found);
        }
    }

    public List<Student> findAll() {
        return em.createQuery("from Student", Student.class).getResultList();
    }
}
