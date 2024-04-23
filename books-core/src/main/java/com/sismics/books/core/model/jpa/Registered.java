package com.sismics.books.core.model.jpa;
import javax.persistence.*;

@Entity
@Table(name = "T_REGISTERED")
public class Registered {
    @Id
    @Column(name = "id")
    private String id;

    // Getter and setter for id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}