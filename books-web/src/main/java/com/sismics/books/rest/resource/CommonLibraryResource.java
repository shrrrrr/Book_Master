package com.sismics.books.rest.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sismics.books.core.dao.jpa.DAOFactory;
import com.sismics.books.core.dao.jpa.LibraryBookDao;
import com.sismics.books.core.dao.jpa.RatingDao;
import com.sismics.books.core.dao.jpa.TagDao;
import com.sismics.books.core.dao.jpa.UserBookDao;
import com.sismics.books.core.dao.jpa.RegisteredDao;
import com.sismics.books.core.dao.jpa.UserDao;
import com.sismics.books.core.dao.jpa.criteria.UserBookCriteria;
import com.sismics.books.core.dao.jpa.dto.TagDto;
import com.sismics.books.core.dao.jpa.dto.UserBookDto;
import com.sismics.books.core.event.BookImportedEvent;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.LibraryBook;
import com.sismics.books.core.model.jpa.Rating;
import com.sismics.books.core.model.jpa.Registered;
import com.sismics.books.core.model.jpa.Tag;
import com.sismics.books.core.model.jpa.User;
import com.sismics.books.core.model.jpa.UserBook;
import com.sismics.books.core.util.DirectoryUtil;
import com.sismics.books.core.util.jpa.PaginatedList;
import com.sismics.books.core.util.jpa.PaginatedLists;
import com.sismics.books.core.util.jpa.SortCriteria;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sismics.books.core.util.DirectoryUtil;

import java.io.*;

/**
 * Common library REST resource.
 * 
 * @author [Your Name]
 */
@Path("/common")
public class CommonLibraryResource extends BaseResource {
    public DAOFactory daoFactory = new DAOFactory(); 
    /**
     * Returns "Hello, World!" in JSON format.
     * 
     * @return Response
     * @throws JSONException
     */
    // @GET
    // @Path("/hello")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response hello() throws JSONException {
    //     JSONObject response = new JSONObject();
    //     LibraryBookDao bookDao = new LibraryBookDao();
    //     List<LibraryBook> a = bookDao.getAllBooks();
    //      int i=0;
    //      for (LibraryBook book : a) {
    //         RatingDao r = new RatingDao();
    //         int rating = (int)r.getAverageRatingByBookId(book.getId());
    //         book.setRating(rating);
    //         //a[i]=book;
    //         i++;
    //     }
    //     response.put("message", a);
    //     return Response.ok().entity(response).build();
    // }

    @GET
@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public Response hello() throws JSONException {
    JSONObject response = new JSONObject();
    LibraryBookDao bookDao = daoFactory.getLibraryBookDao();
    List<LibraryBook> books = bookDao.getAllBooks();
    
    List<JSONObject> booksJson = new ArrayList<>();
    
    for (LibraryBook book : books) {
        RatingDao ratingDao = daoFactory.getRatingDao();
        int rating = (int) ratingDao.getAverageRatingByBookId(book.getId());
        book.setRating(rating);
        Long numberOfRatings = (Long) ratingDao.getTotalRatingsByBookId(book.getId());
        
        JSONObject bookJson = new JSONObject();
        bookJson.put("author", book.getAuthor());
        bookJson.put("title", book.getTitle());
        bookJson.put("rating", book.getRating());
        bookJson.put("id", book.getId());
        bookJson.put("genre", book.getGenre());
        bookJson.put("totalRatings",numberOfRatings);
        
        booksJson.add(bookJson);
    }
    
    response.put("books", booksJson);
    return Response.ok().entity(response.toString()).build();
}

    @POST
    @Path("/getBook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(
        @FormParam("id") String id
    ) throws JSONException {
        JSONObject response = new JSONObject();
        if(id!=null){
        LibraryBookDao bookDao =daoFactory.getLibraryBookDao();
        LibraryBook a = bookDao.getById(id);
        RatingDao r = daoFactory.getRatingDao();
        int rating = (int)r.getAverageRatingByBookId(id);
        a.setRating(rating);
        response.put("message", a.getRating()+"");
        response.put("author",a.getAuthor());
        response.put("title",a.getTitle());
        response.put("genre",a.getGenre());
        response.put("ratting",a.getRating());
        return Response.ok().entity(response).build();
        //  for (LibraryBook book : a) {
        //     // Perform operations with each LibraryBook object
        //     System.out.println("Book Title: " + book.getTitle());
        //     System.out.println("Book Author: " + book.getAuthor());
        //     // Add more operations as needed
        // }
        }
        return Response.ok().entity(response).build();
    }

    @PUT
    @Path("rate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRating(
            @FormParam("user") String user,
            @FormParam("book") String book,
            @FormParam("rating") String rating12
            ) throws JSONException {
        
        // Check if this book is not already in database
        RatingDao ratingDao = daoFactory.getRatingDao();
        
        // Create the book
        Rating rating = new Rating();
        rating.setId(UUID.randomUUID().toString());
        
        if (user != null) {
            rating.setUser(user);
        }
        if (book != null) {
            rating.setBook(book);
        }
        if (rating12!=null) {
            rating.setRating(Integer.parseInt(rating12));
        }
        
        
        ratingDao.create(rating);
        
        // Returns the book ID
        JSONObject response = new JSONObject();
        // File dbDirectory = DirectoryUtil.getDbDirectory();
        // String dbFile = dbDirectory.getAbsoluteFile() + File.separator + "books";
        // System.out.println("ss18101999"+"hibernate.connection.url"+ "jdbc:hsqldb:file:" + dbFile + ";hsqldb.write_delay=false;shutdown=true");
        response.put("id", "rating_added");
        return Response.ok().entity(response).build();
    }
    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register() throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        // Check if this book is not already in database
        RegisteredDao registeredDao = daoFactory.getRegisteredDao();
        
        // Create the book
        

        if(registeredDao.findById(principal.getId())==null){
            Registered registered = new Registered();
            registered.setId(principal.getId());
            registeredDao.create(registered);
        }
        else{
        JSONObject response = new JSONObject();
        response.put("Error!!", "User Already  Registered!!");
        return Response.ok().entity(response).build();
        }        
        // Returns the book ID
        JSONObject response = new JSONObject();
        // File dbDirectory = DirectoryUtil.getDbDirectory();
        // String dbFile = dbDirectory.getAbsoluteFile() + File.separator + "books";
        // System.out.println("ss18101999"+"hibernate.connection.url"+ "jdbc:hsqldb:file:" + dbFile + ";hsqldb.write_delay=false;shutdown=true");
        response.put("id", "User Registered!!");
        return Response.ok().entity(response).build();
    }
    @GET
    @Path("isRegistered")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRegister() throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        // Check if this book is not already in database
        RegisteredDao registeredDao = daoFactory.getRegisteredDao();
        JSONObject response = new JSONObject();
        
        // Create the book
        

        if(registeredDao.findById(principal.getId())==null){
            response.put("val","false");
        }
        else{
        response.put("val", "true");
        }        
        return Response.ok().entity(response).build();
    }

    @PUT
    @Path("manual")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(
            @FormParam("title") String title,
            @FormParam("subtitle") String subtitle,
            @FormParam("author") String author,
            @FormParam("genre") String genre,
            @FormParam("description") String description,
            @FormParam("isbn10") String isbn10,
            @FormParam("isbn13") String isbn13,
            @FormParam("page_count") Long pageCount,
            @FormParam("language") String language,
            @FormParam("publish_date") String publishDateStr,
            @FormParam("tags") List<String> tagList,
            @FormParam("rating") double rating,
            @FormParam("ratings") String ratingsJson
            ) throws JSONException {
        if (false && !authenticate()) {
            throw new ForbiddenClientException();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Integer> ratings = null;
        // try {
        //     ratings = objectMapper.readValue(ratingsJson, new TypeReference<HashMap<String, Integer>>() {});
        // } catch (IOException e) {
        //     // Handle JSON parsing exception
        //     e.printStackTrace();
        // }

        // Check if ratings are successfully parsed
        // if (ratings == null) {
        //     throw new ClientException("ValidationError"+ratingsJson, "Invalid ratings format");
        // }
        // for (Map.Entry<String,Integer> mapElement : ratings.entrySet()) {
        //     String key = mapElement.getKey();
 
        //     // Adding some bonus marks to all the students
        //     int value = (mapElement.getValue() + 10);
 
        //     // Printing above marks corresponding to
        //     // students names
        //     System.out.println(key + " : " + value);
        // }
        // Validate input data
        // title = ValidationUtil.validateLength(title, "title", 1, 255, false);
        // subtitle = ValidationUtil.validateLength(subtitle, "subtitle", 1, 255, true);
        // author = ValidationUtil.validateLength(author, "author", 1, 255, false);
        // description = ValidationUtil.validateLength(description, "description", 1, 4000, true);
        // isbn10 = ValidationUtil.validateLength(isbn10, "isbn10", 10, 10, true);
        // isbn13 = ValidationUtil.validateLength(isbn13, "isbn13", 13, 13, true);
        // language = ValidationUtil.validateLength(language, "language", 2, 2, true);
        // Date publishDate = ValidationUtil.validateDate(publishDateStr, "publish_date", false);
        
        // if (Strings.isNullOrEmpty(isbn10) && Strings.isNullOrEmpty(isbn13)) {
        //     throw new ClientException("ValidationError"+title, "At least one ISBN number is mandatory");
        // }
        
        // Check if this book is not already in database
        LibraryBookDao bookDao = daoFactory.getLibraryBookDao();
        
        // Create the book
        LibraryBook book = new LibraryBook();
        book.setId(UUID.randomUUID().toString());
        
        if (title != null) {
            book.setTitle(title);
        }
        if (subtitle != null) {
            book.setSubtitle(subtitle);
        }
        if (author != null) {
            book.setAuthor(author);
        }
        if (genre != null) {
            book.setGenre(genre);
        }
        if (description != null) {
            book.setDescription(description);
        }
        if (isbn10 != null) {
            book.setIsbn10(isbn10);
        }
        if (isbn13 != null) {
            book.setIsbn13(isbn13);
        }
        if (pageCount != null) {
            book.setPageCount(pageCount);
        }
        if (language != null) {
            book.setLanguage(language);
        }
        // if (publishDate != null) {
        //     book.setPublishDate(publishDate);
        // }
        
        bookDao.create(book);
        
        // Returns the book ID
        JSONObject response = new JSONObject();
        File dbDirectory = DirectoryUtil.getDbDirectory();
        String dbFile = dbDirectory.getAbsoluteFile() + File.separator + "books";
        System.out.println("ss18101999"+"hibernate.connection.url"+ "jdbc:hsqldb:file:" + dbFile + ";hsqldb.write_delay=false;shutdown=true");
        response.put("id", "a");
        return Response.ok().entity(response).build();
    }

   
    
}
