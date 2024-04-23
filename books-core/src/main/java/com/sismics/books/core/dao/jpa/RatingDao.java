package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.model.jpa.Rating;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import com.sismics.util.context.ThreadLocalContext;
import java.util.*;

public class RatingDao {
    private EntityManager entityManager = ThreadLocalContext.get().getEntityManager();;

    public Rating create(Rating rating) {
        entityManager.persist(rating);
        return rating;
    }

    public Rating getById(String id) {
        return entityManager.find(Rating.class, id);
    }

    public Rating update(Rating rating) {
        return entityManager.merge(rating);
    }

    public void delete(Rating rating) {
        entityManager.remove(entityManager.contains(rating) ? rating : entityManager.merge(rating));
    }

    public double getAverageRatingByBookId(String bookId) {
    Query query = entityManager.createQuery("SELECT AVG(r.rating) FROM Rating r WHERE r.book = :book", Double.class);
    try{
        query.setParameter("book", bookId);
        double rating=(double)query.getSingleResult();
        return rating;
    }
    catch(Exception e){
        return 0;
    }
    
}

    public Long getTotalRatingsByBookId(String bookId) {
            Query query = entityManager.createQuery(
                "SELECT COUNT(r) FROM Rating r WHERE r.book = :bookId",
                Long.class
            );
            try {
                query.setParameter("bookId", bookId);
                return (Long) query.getSingleResult();
            } catch (NoResultException e) {
                return (long)0;
            }
        }
}
