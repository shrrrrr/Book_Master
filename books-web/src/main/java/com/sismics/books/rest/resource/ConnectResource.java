package com.sismics.books.rest.resource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.core.constant.AppId;
import com.sismics.books.core.dao.jpa.UserAppDao;
import com.sismics.books.core.dao.jpa.UserContactDao;
import com.sismics.books.core.dao.jpa.criteria.UserContactCriteria;
import com.sismics.books.core.dao.jpa.dto.UserAppDto;
import com.sismics.books.core.dao.jpa.dto.UserContactDto;
import com.sismics.books.core.event.UserAppCreatedEvent;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.UserApp;
import com.sismics.books.core.service.FacebookService;
import com.sismics.books.core.service.facebook.AuthenticationException;
import com.sismics.books.core.service.facebook.PermissionException;
import com.sismics.books.core.util.jpa.PaginatedList;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.ValidationUtil;

import com.sismics.books.rest.services.ConnectService; 



public class ConnectResource extends BaseResource{
    private ConnectService connectService = new ConnectService(AppContext.getInstance().getFacebookService());
    /**
     * Returns current user's connected applications.
     * 
     * @return Response
     * @throws JSONException
     */
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        List<UserAppDto> userAppList = connectService.getUserApps(principal.getId());

        List<JSONObject> items = new ArrayList<JSONObject>();
        for(UserAppDto userAppDto : userAppList){
            JSONObject userApp = new JSONObject();
            userApp.put("id", userAppDto.getAppId());
            userApp.put("connected", userAppDto.getId() != null && userAppDto.getAccessToken() != null);
            userApp.put("username", userAppDto.getUsername());
            userApp.put("sharing", userAppDto.isSharing());
            items.add(userApp);
        }

        JSONObject response = new JSONObject();
        response.put("app", items);
        return Response.ok().entity(response).build();
    }

    /**
     * Add a connected application.
     * 
     * @param appId App ID
     * @param authToken OAuth authorization token
     * @return Response
     * @throws JSONException
     */
    @POST
    @Path("{id: [a-z]+}/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(
            @PathParam("id") String appIdString,
            @FormParam("access_token") String accessToken) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Validate input data
        accessToken = ValidationUtil.validateStringNotBlank(accessToken, "access_token");

        UserApp userApp = connectService.addUserApp(principal.getId(), appIdString, accessToken);
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
}

    /**
     * Remove a connected application.
     * 
     * @param appIdString App ID
     * @return Response
     * @throws JSONException
     */
    @POST
    @Path("{id: [a-z0-9\\-]+}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(
            @PathParam("id") String appIdString) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        connectService.deleteUserApp(principal.getId(), appIdString);

        // Always return OK
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }

    /**
     * Updates connected application.
     * 
     * @param appIdString App ID
     * @param sharing If true, share on this application
     * @return Response
     * @throws JSONException
     */
    @POST
    @Path("{id: [a-z0-9\\-]+}/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("id") String appIdString,
            @FormParam("sharing") boolean sharing) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        connectService.updateUserApp(principal.getId(), appIdString, sharing);

        // Always return OK
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }

    /**
     * Returns contact list on a connected application.
     * 
     * @param appIdString App ID
     * @param limit Page limit
     * @param offset Page offset
     * @return Response
     * @throws JSONException
     */
    @GET
    @Path("{id: [a-z0-9\\-]+}/contact/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response contactList(
            @PathParam("id") String appIdString,
            @QueryParam("query") String query,
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset) throws JSONException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        List<JSONObject> contacts = connectService.getContactList(principal.getId(), appIdString, query, offset, limit);

        JSONObject response = new JSONObject();
        response.put("total", contacts.size());
        response.put("contacts", contacts);
        return Response.ok().entity(response).build();
    }

}
