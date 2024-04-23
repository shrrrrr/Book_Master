package com.sismics.books.core.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


import com.google.common.base.Objects;


/*
 * create cached table T_FAVOURITE ( 
    
USER_ID VARCHAR(255) not null, 
SERVICE_PROVIDER VARCHAR(50) not null,
CONTENT_TYPE VARCHAR(50) not null,
ITEM_NAME VARCHAR(50),
AUTHOR_NAME VARCHAR(50)

primary key (USER_ID) 

);
 * 
 */





@Entity
@Table(name = "T_FAVOURITE")
public class Favourite {



    /**
     * Fav _ID.
     */
    
    @Column(name = "FAV_ID", length = 36)
    private String favid;

    /*
     * userid
     */
    
    @Column(name = "USER_ID",length = 255)
    private String userid; 

    @Column(name = "SERVICE_PROVIDER", length = 255)
    private String serviceprovider;

    @Column(name = "CONTENT_TYPE", length = 255)
    private String contenttype;

    @Id
    @Column(name = "ITEM_NAME",  length = 255)
    private String itemname;

    @Column(name = "AUTHOR_NAME", length = 255)
    private String authorname;





    /**
     * Getter of id.
     * 
     * @return id
     */
    public String getfavid() {
        return favid;
    }

    /**
     * Setter of id.
     * 
     * @param id id
     */
    public void setfavid(String id) {
        this.favid = id;
    }
    


     /**
     * Getter of id.
     * 
     * @return id
     */
    public String getuserid() {
        return userid;
    }

    /**
     * Setter of id.
     * 
     * @param userid id
     */
    public void setuserid(String id) {
        this.userid = id;
    }



     /**
     * Getter of id.
     * 
     * @return serviceprovider
     */
    public String getserviceprovider() {
        return serviceprovider;
    }

    /**
     * Setter of id.
     * 
     * @param serviceprovider id
     */
    public void setserviceprovider(String id) {
        this.serviceprovider = id;
    }


     /**
     * Getter of id.
     * 
     * @return content type
     */
    public String getcontenttype() {
        return contenttype;
    }

    /**
     * Setter of id.
     * 
     * @param content type
     */
    public void setcontenttype(String id) {
        this.contenttype = id;
    }


     /**
     * Getter Itemname name.
     * 
     * @return name
     */
    public String getitemname() {
        return itemname;
    }

    /**
     * Setter of id.
     * 
     * @param item Name
     */
    public void setitemname(String id) {
        this.itemname = id;
    }



     /**
     * Getter of id.
     * 
     * @return author
     */
    public String getauthorname() {
        return authorname;
    }

    /**
     * Setter of id.
     * 
     * @param authorname name
     */
    public void setauthorname(String id) {
        this.authorname = id;
    }
/* 
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("title", title)
                .toString();
    }
*/
}
