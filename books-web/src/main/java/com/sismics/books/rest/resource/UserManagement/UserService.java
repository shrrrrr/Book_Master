package com.sismics.books.rest.resource;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import java.security.Principal;

/**
 * User service interface.
 */
public interface UserService {
    public Response register(String username,String password,String localeId,String email) throws JSONException; 
    public Response update(String password,String email, String themeId, String localeId, boolean firstConnection) throws JSONException;
    public Response update(String username, String password, String email, String themeId, String localeId) throws JSONException;
    public Response delete() throws JSONException;
    public Response delete(String Username) throws JSONException;
    public Response checkUsername(String username) throws JSONException;
    public Response login(String username, String password, boolean longLasted) throws JSONException;
    NewCookie createSessionCookie();
    public Response logout() throws JSONException;
    public Response getInfo() throws JSONException;
}