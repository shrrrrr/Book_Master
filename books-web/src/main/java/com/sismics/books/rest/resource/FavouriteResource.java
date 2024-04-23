package com.sismics.books.rest.resource;

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


import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.core.dao.jpa.FavouriteDao;
import com.sismics.books.core.model.jpa.Favourite;
import com.sismics.rest.exception.ForbiddenClientException;
import javax.persistence.PersistenceException;

import java.util.ArrayList;
import java.util.List;

//
@Path("/favourite")
public class FavouriteResource extends BaseResource {

    

    private final FavouriteDao favouriteDao;

    public FavouriteResource() {
        this.favouriteDao = new FavouriteDao();
    }

   
    
/* 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavourites(
            @QueryParam("userId") String userId,
            @QueryParam("serviceProvider") ServiceProvider serviceProvider,
            @QueryParam("contentType") ContentType contentType) {
        List<Favourite> favourites = favouriteDao.getFavourites(userId);
        return Response.ok().entity(favourites).build();
    }
    */
    @GET
    @Path("/favourite")
    @Produces(MediaType.APPLICATION_JSON)
    public Response solve() throws JSONException{
        if (!authenticate()){
            throw new ForbiddenClientException();
        }
        System.out.println("Favourite is executing");
        JSONObject response = new JSONObject();
        FavouriteDao favouritedao=new FavouriteDao();
        List<Favourite> books = favouriteDao.getFavouriteBooks(principal.getId());
        System.out.println("These are the books");
    List<JSONObject> booksJson = new ArrayList<>();
    
    for (Favourite book : books) {
        
        JSONObject bookJson = new JSONObject();
        bookJson.put("itemname", book.getitemname());
        bookJson.put("authorname",book.getauthorname());
        bookJson.put("serviceprovider",book.getserviceprovider());
        bookJson.put("contenttype",book.getcontenttype());

        //System.out.println(book.getauthorname());
       // System.out.println("Inside loop");
        
        booksJson.add(bookJson);
    }
    
    response.put("books", booksJson);
    return Response.ok().entity(response.toString()).build();
        
    }





     
  @POST
  @Path("/favourites")
  @Produces (MediaType.APPLICATION_JSON)
  public Response createFavourite(

    @FormParam("favid") String favid,
    @FormParam("userid") String userid,
    @FormParam("serviceprovider") String serviceprovider,
    @FormParam("contenttype") String contenttype,
    @FormParam("itemname") String itemname,
    @FormParam("authorname") String authorname

  )throws JSONException
  {
    if (!authenticate()){
        throw new ForbiddenClientException();
    }
    // Update the Favourite
System.out.println("This is the User ID");
System.out.println(principal.getId());
    FavouriteDao favouritedao=new FavouriteDao();

    Favourite fav=new Favourite();

   
    fav.setuserid(principal.getId());
    fav.setserviceprovider(serviceprovider);
    fav.setcontenttype(contenttype);
    fav.setitemname(itemname);
    fav.setauthorname(authorname);

    favouritedao.create(fav);


    // Always return ok
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    
    }

    /* 
    @GET
    @Path("/{favouriteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavourite(
            @PathParam("favouriteId") String favouriteId) {
        Favourite favourite = favouriteDao.getFavourite(favouriteId);
        return Response.ok().entity(favourite).build();
    }
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFavourites() {
        List<Favourite> allFavourites = favouriteDao.getFavourites();
        return Response.ok().entity(allFavourites).build();
    }
    */
}
