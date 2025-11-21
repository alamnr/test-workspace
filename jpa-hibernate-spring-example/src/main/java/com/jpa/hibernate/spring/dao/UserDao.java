package com.jpa.hibernate.spring.dao;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.jpa.hibernate.spring.entities.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    // Example: bulk update using JPQL
    public int bulkDisableOldUsers(OffsetDateTime cutoff) {
        return em.createQuery("update User u set u.active = false where u.lastLogin < :cutoff")
                    .setParameter("cutoff", cutoff)
                    .executeUpdate();
    }


    // Save in batches using EntityManager directly (memory control)
    public void batchInsert(List<User> users, int batchSize) {
            for (int i = 0; i < users.size(); i++) {
            em.persist(users.get(i));
            if (i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}