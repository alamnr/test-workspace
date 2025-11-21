package com.jpa.hibernate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.jpa.hibernate.dao.StudentDAO_V3;
import com.jpa.hibernate.entities.Student;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentServiceIT {

    private EntityManagerFactory emf;
    private StudentDAO_V3 dao;
    private StudentService_V2 service;

    @BeforeAll
    void init() {
        emf = Persistence.createEntityManagerFactory("studentPU-test");
        dao = new StudentDAO_V3(null);
        service = new StudentService_V2(emf, dao);
    }

    @AfterAll
    void cleanUp(){
        if(emf != null) {
            emf.close();
        }
    }

    @Test
    void testCreateAndFindStudents() {
        Student s1 = service.createStudent("Alice", 18);
        Student s2 = service.createStudent("Bob", 21);

        List<Student> all = service.getAllStudents();
        assertEquals(2, all.size(),"Should have 2 students in DB");

        assertEquals("Alice", all.get(0).getName());
        assertEquals("Bob", all.get(1).getName());
    }

    @Test
    void testUpdateStudentAge(){
        Student s1 = service.createStudent("Charlie", 15);
        Student updated = service.updateStudentAge(s1.getId(), 17);
        assertEquals(17, updated.getAge(),"Age should be updated to 17");
    }

    @Test
    void deleteStudent(){
        Student s1 = service.createStudent("David", 20);
        service.deleteStudent(s1.getId());
        List<Student> all = service.getAllStudents();
        assertTrue(all.stream().noneMatch(s->s.getName().equals("David")),"David should be deleted");

    }
    
    
}
