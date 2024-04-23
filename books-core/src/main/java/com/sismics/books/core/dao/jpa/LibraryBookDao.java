package com.sismics.books.core.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.sismics.books.core.model.jpa.LibraryBook;
import com.sismics.util.context.ThreadLocalContext;
import java.util.*;


/**
 * Book DAO.
 * 
 * @author bgamard
 */
public class LibraryBookDao {
    /**
     * Creates a new book.
     * 
     * @param book Book
     * @return New ID
     * @throws Exception
     */
    public String create(LibraryBook book) {
        // Create the book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        //EntityTransaction transaction = em.getTransaction();
        //transaction.begin();
        em.persist(book);
        //transaction.commit();
        return book.getId();
    }
    
    /**
     * Gets a book by its ID.
     * 
     * @param id Book ID
     * @return Book
     */
    public LibraryBook getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(LibraryBook.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<LibraryBook> getAllBooks() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query query = em.createQuery("SELECT b FROM LibraryBook b",LibraryBook.class);
            //System.out.println("ss9876543"+" "+query.getResultList());
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a book by its ISBN number (10 or 13)
     * 
     * @param isbn ISBN Number (10 or 13)
     * @return Book
     */
}
