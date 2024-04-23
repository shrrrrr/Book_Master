package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.model.jpa.Registered;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import com.sismics.util.context.ThreadLocalContext;
import java.util.*;

public class RegisteredDao {
    
    private EntityManager entityManager = ThreadLocalContext.get().getEntityManager();;;

    public Registered findById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select b from Registered b where b.id = :isbn");
        q.setParameter("isbn", id);
        System.out.print("Finding if user already registered??");
        try {
            Registered r = (Registered) q.getSingleResult();
            System.out.println("ss111"+" "+r);
            return r;
        } catch (Exception e) {
            System.out.println("Not Found Baby!!");
            return null;
        }
    }

    public Registered create(Registered registered){
        entityManager.persist(registered);
        return registered;
    } 
}