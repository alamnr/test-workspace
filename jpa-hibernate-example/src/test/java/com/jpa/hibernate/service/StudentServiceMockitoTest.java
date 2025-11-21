package com.jpa.hibernate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpa.hibernate.dao.StudentDAO_V3;
import com.jpa.hibernate.entities.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

@ExtendWith(MockitoExtension.class)
public class StudentServiceMockitoTest {
    
    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private StudentDAO_V3 dao;

    @InjectMocks
    private StudentService_V2 service;

    @BeforeEach
    void setUp() {
        // Mockito automatically injects mock into service
    }

    @Test
    void testCreateStudentCallDao() {
        
        // EntityManagerFactory -> EntityManager
        when(emf.createEntityManager()).thenReturn(em);

        // EntityManager -> Transaction
        when(em.getTransaction()).thenReturn(tx);
        // Mock behavior for DAO
        doNothing().when(dao).create(any(Student.class));

        // call  service
        Student created = service.createStudent("Alice", 18);

        assertNotNull(created);
        assertEquals("Alice", created.getName());
        assertEquals(18, created.getAge());

        // verify transaction flow

        verify(tx).begin();
        verify(tx).commit();

        // verify Dao is injected with em
        verify(dao).setEntityManager(em);

        // verify DAO interaction

        verify(dao, times(1)).create(any(Student.class));

        // verify em closed
        verify(em).close();
    }

    @Test
    void testUpdateStdentDelegatesToDAO(){
        Student existing = new Student("Bob", 20);
        existing.setId(1L);

        // EntityManagerFactory -> EntityManager
        when(emf.createEntityManager()).thenReturn(em);

        // EntityManager -> Transaction
        when(em.getTransaction()).thenReturn(tx);

        // Mock the begavior of dao
        when(dao.find(1L)).thenReturn(existing);
        when(dao.update(any(Student.class))).thenAnswer(inv->inv.getArgument(0));

        Student updated = service.updateStudentAge(1L, 25);

        assertNotNull(updated);
        assertEquals(25, updated.getAge());

        // verify correct calls
        verify(dao).find(1L);
        verify(dao).update(existing);

    }

    @Test
    void testDeleteStudentCallsDAO() {

        // EntityManagerFactory -> EntityManager
        when(emf.createEntityManager()).thenReturn(em);

        // EntityManager -> Transaction
        when(em.getTransaction()).thenReturn(tx);

        // Mock the behavior of dao
        doNothing().when(dao).delete(1L);

        service.deleteStudent(1L);

        verify(dao,times(1)).delete(1L);
    }

    @Test
    void testGetAllStudents() {

        // EntityManagerFactory -> EntityManager
        when(emf.createEntityManager()).thenReturn(em);


        // Mock the behavior of dao
        when(dao.findAll()).thenReturn(List.of(
                new Student("Alice", 18),
                new Student("Bob", 21))
            );
        List<Student> result = service.getAllStudents();

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());

        verify(dao,times(1)).findAll();
    }
}
