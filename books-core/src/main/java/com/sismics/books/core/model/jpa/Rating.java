package com.sismics.books.core.model.jpa;

import javax.persistence.*;

@Entity
@Table(name = "T_RATING")
public class Rating {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private String user;

    @Column(name = "book_id")
    private String book;

    @Column(name = "rating")
    private int rating;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
