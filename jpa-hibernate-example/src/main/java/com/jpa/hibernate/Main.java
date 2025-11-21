package com.jpa.hibernate;

import com.jpa.hibernate.dao.StudentDAO;
import com.jpa.hibernate.dao.StudentDAO_V3;
import com.jpa.hibernate.entities.Student;
import com.jpa.hibernate.service.StudentService;
import com.jpa.hibernate.service.StudentService_V2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

    public static void  main(String... args){

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("studentPU");
        EntityManager em = emf.createEntityManager();

        // 1️⃣ create and persiste (Transient -> Managed)

        em.getTransaction().begin();
        Student s  = new Student("Alice",18);
        em.persist(s);
        em.getTransaction().commit(); // entity becomes managed and persisted

        System.out.println("After persist : " + s);

        // 2️⃣ Detach the entity (Managed → Detached)
        em.detach(s);
        System.out.println("Entity is now detached.");
        s.setAge(19); // change while detached
        System.out.println("Detached entity modified "+ s);


        // Merge back (detach -> managed)

        em.getTransaction().begin();
        Student merged = em.merge(s);
        em.getTransaction().commit();
        System.out.println("Merged entity persisted: " + merged);

        // Remove (Manage + Remove)
        em.getTransaction().begin();
        Student toRemove = em.find(Student.class, merged.getId());
        System.out.println("Removing Entity : " + toRemove);
        em.remove(toRemove); // mark for remove
        em.getTransaction().commit();
        System.out.println("Entity removed from DB. ");
        // Confirm from DB
        // Verify deletion

        Student check = em.find(Student.class, merged.getId());
        if (check == null) {
            System.out.println("✅ Verified: Entity no longer exists in DB.");
        } else {
            System.out.println("❌ Still found: " + check);
        }

        em.close();


        StudentDAO dao = new StudentDAO(emf);
        
        // Create

        Student s1 = dao.create(new Student("Alice", 18));
        Student s2 = dao.create(new Student("Bob", 19));

        // Find (returns detached object)
        Student found = dao.find(s1.getId());


        // update detached entity
        found.setAge(20);
        dao.update(found);

        // Delete one
        dao.delete(s2.getId());

        // List remaining
        System.out.println("Remaining Students : "+ dao.findAll());


        StudentService service = new StudentService(emf);

        // Create Student
        Student s3 = service.createStudent("Alice", 28);
        Student s4 = service.createStudent("Bob", 29);

        // Update 

        service.updateStudentAge(s3.getId(), 30);

        // Delete another 

        service.deleteStudent(s4.getId());

        // print all remaining

        System.out.println("Remaining Students : " + service.getAllStudents());

        // Create DAO , no EM yet
        StudentDAO_V3 dao_v3 = new StudentDAO_V3(null);

        // Inject dao into service 

        StudentService_V2 service_V2 = new StudentService_V2(emf, dao_v3);

        service_V2.createStudent("Alie", 49);
        service_V2.createStudent("Tarjan", 50);
        service_V2.updateStudentAge(2L, 55);
        System.out.println("Found student - " + service_V2.findStudent(2L));
        System.out.println("Find All count - " + service_V2.getAllStudents().size());
        service_V2.getAllStudents().forEach(x->System.out.println(x.toString()));
        service_V2.deleteStudent(2L);
        service_V2.getAllStudents().forEach(x->System.out.println(x.toString()));
        
        emf.close();



    }

}
