package com.sismics.books.core.dao.jpa;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.Favourite;
import com.sismics.util.context.ThreadLocalContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;


/**
 * Favourite DAO.
 * 
 * @author 
 */
public class FavouriteDao {

    /**
     * Creates a new favourite.
     * 
     * @param favourite favourite
     * 
     * @throws Exception
     */

      public String create(Favourite favourite) {
        // Create the book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(favourite);
        return favourite.getuserid();
    }

    public List<Favourite> getFavouriteBooks(String UserId){
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query query = em.createQuery("SELECT b FROM Favourite b where b.userid = :userid",Favourite.class);
            //System.out.println("ss9876543"+" "+query.getResultList());
            query.setParameter("userid", UserId);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }




    


 
    
    
  

    /**
     * Retrieves all favourite items for the specified user.
     * 
     * @param userId User ID
     * @return List of favourites
     
    public List<Favourite> getFavourites(String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query query = em.createQuery("SELECT f FROM Favourite f WHERE f.userId = :userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    */
}
