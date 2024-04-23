package com.sismics.books.rest.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.util.List;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import com.sismics.books.core.dao.jpa.dto.TagDto;
import com.sismics.books.core.service.TagService;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;

@Path("/tag")
public class TagResource extends BaseResource {
   

    private TagService tagService = new TagService();

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        

        List<JSONObject> tagJSONObjects = tagService.getAllTagsByUserId(principal.getId());
        JSONObject response = new JSONObject();
      
        response.put("tags", tagJSONObjects);
        return Response.ok().entity(response).build();
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response add(@FormParam("name") String name, @FormParam("color") String color) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        

        String tagId = tagService.createTag(principal.getId(), name, color);
        JSONObject response = new JSONObject();
        response.put("id", tagId);
        // system.out.println(tagId);
        return Response.ok().entity(response).build();
    }


    @POST
    @Path("{id: [a-z0-9\\-]+}")
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response update(@PathParam("id") String id, @FormParam("name") String name, @FormParam("color") String color) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        

        tagService.updateTag(principal.getId(), id, name, color);
        JSONObject response = new JSONObject();
        response.put("id", id);
        return Response.ok().entity(response).build();
    }


    @DELETE
    @Path("{id: [a-z0-9\\-]+}")
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response delete(@PathParam("id") String tagId) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        

        tagService.deleteTag(principal.getId(), tagId);
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }
}