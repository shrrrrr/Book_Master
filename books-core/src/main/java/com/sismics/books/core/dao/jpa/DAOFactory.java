package com.sismics.books.core.dao.jpa;

public class DAOFactory{
    public LibraryBookDao getLibraryBookDao(){
        LibraryBookDao l = new LibraryBookDao();
        return l;
    }
    public RatingDao getRatingDao(){
        RatingDao r = new RatingDao();
        return r;
    }
    public RegisteredDao getRegisteredDao(){
        RegisteredDao r = new RegisteredDao();
        return r;
    }
}