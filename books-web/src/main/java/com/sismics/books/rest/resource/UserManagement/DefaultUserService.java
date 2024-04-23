package com.sismics.books.rest.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;
import com.sismics.books.core.dao.jpa.AuthenticationTokenDao;
import com.sismics.books.core.dao.jpa.UserDao;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.books.core.model.jpa.User;
import org.codehaus.jettison.json.JSONObject;
import javax.ws.rs.core.Response;
import com.sismics.books.core.constant.Constants;
import com.sismics.rest.exception.ServerException;
import org.codehaus.jettison.json.JSONException;
import org.apache.commons.lang.StringUtils;
import com.sismics.books.core.model.jpa.AuthenticationToken;
import com.sismics.util.filter.TokenBasedSecurityFilter;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.security.UserPrincipal;
import com.sismics.books.rest.constant.BaseFunction;
import javax.servlet.http.Cookie;
import com.sismics.rest.exception.ClientException;
import com.sismics.books.core.dao.jpa.RoleBaseFunctionDao;
import com.sismics.books.rest.resource.UserBuilder;
import com.sismics.books.rest.resource.ValidationStrategy;
import com.sismics.books.rest.resource.UsernameValidationStrategy;
import com.sismics.books.rest.resource.EmailValidationStrategy;
import com.sismics.books.rest.resource.PasswordValidationStrategy;
import com.sismics.books.rest.resource.LocaleValidationStrategy;
import com.sismics.books.rest.resource.ThemeValidationStrategy;


/**
 * Default implementation of UserService.
 */
public class DefaultUserService extends BaseResource implements UserService{
    /**  */
    private final UserDao userDao;
    private final AuthenticationTokenDao authenticationTokenDao;
    private final ValidationStrategy usernameValidation , passwordValidation, emailValidation, themeValidation, localeIdValidation;
    
    public DefaultUserService() 
    {
        this.userDao = new UserDao();
        this.authenticationTokenDao = new AuthenticationTokenDao();
        this.usernameValidation = new UsernameValidationStrategy();
        this.passwordValidation = new PasswordValidationStrategy();
        this.emailValidation = new EmailValidationStrategy();
        this.themeValidation = new ThemeValidationStrategy();
        this.localeIdValidation = new LocaleValidationStrategy();
    }

    @Override
    public Response register(String username,String password,String localeId,String email)  throws JSONException{
        // System.out.println(principal.getName());
        // Validate the input data
        // username = ValidationUtil.validateLength(username, "username", 3, 50);
        // ValidationUtil.validateAlphanumeric(username, "username");
        // password = ValidationUtil.validateLength(password, "password", 8, 50);
        // email = ValidationUtil.validateLength(email, "email", 3, 50);
        // ValidationUtil.validateEmail(email, "email");
        
        usernameValidation.validate(username, "username");
        passwordValidation.validate(password, "password");
        emailValidation.validate(email, "email");

        User user = new UserBuilder()
            .withUsername(username)
            .withPassword(password)
            .withEmail(email)
            .withRoleId(Constants.DEFAULT_USER_ROLE)
            .withLocaleId(Constants.DEFAULT_LOCALE_ID)
            .withCreateDate(new Date())
            .build();
        
        // Create the user
        UserDao userDao = new UserDao();
        try 
        {
            userDao.create(user);
        } 
        catch (Exception e) 
        {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ServerException("AlreadyExistingUsername", "Login already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown Server Error", e);
            }
        }
        
        // Always return OK
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }

    @Override
    public Response update(String password,String email, String themeId, String localeId, boolean firstConnection) throws JSONException{
        
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Validate the input data
        // password = ValidationUtil.validateLength(password, "password", 8, 50, true);
        // email = ValidationUtil.validateLength(email, "email", null, 100, true);
        // localeId = ValidationUtil.validateLocale(localeId, "locale", true);
        // themeId = ValidationUtil.validateTheme(themeId, "theme", true);

        passwordValidation.validate(password, "password");
        emailValidation.validate(email, "email");
        localeIdValidation.validate(localeId, "locale");
        themeValidation.validate(themeId, "theme");

        
        // Update the user
        UserDao userDao = new UserDao();
        User user = userDao.getActiveByUsername(principal.getName());
        if (email != null) {
            user.setEmail(email);
        }
        if (themeId != null) {
            user.setTheme(themeId);
        }
        if (localeId != null) {
            user.setLocaleId(localeId);
        }
        if ((firstConnection==false || firstConnection==true)  && hasBaseFunction(BaseFunction.ADMIN)) {
            user.setFirstConnection(firstConnection);
        }
        
        user = userDao.update(user);
        
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(password);
            userDao.updatePassword(user);
        }
        
        // Always return "ok"
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }

    @Override
    public Response update(String username, String password, String email, String themeId, String localeId) throws JSONException{
        
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Validate the input data
        passwordValidation.validate(password, "password");
        emailValidation.validate(email, "email");
        localeIdValidation.validate(localeId, "locale");
        themeValidation.validate(themeId, "theme");
        
        // Check if the user exists
        UserDao userDao = new UserDao();
        User user = userDao.getActiveByUsername(username);
        if (user == null) {
            throw new ClientException("UserNotFound", "The user doesn't exist");
        }

        // Update the user
        if (email != null) {
            user.setEmail(email);
        }
        if (themeId != null) {
            user.setTheme(themeId);
        }
        if (localeId != null) {
            user.setLocaleId(localeId);
        }
        
        user = userDao.update(user);
        
        if (StringUtils.isNotBlank(password)) {
            // Change the password
            user.setPassword(password);
            userDao.updatePassword(user);
        }
        
        // Always return "ok"
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }
    @Override
    public Response delete() throws JSONException{
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Ensure that the admin user is not deleted
        if (hasBaseFunction(BaseFunction.ADMIN)) {
            throw new ClientException("ForbiddenError", "The admin user cannot be deleted");
        }
        
        // Delete the user
        UserDao userDao = new UserDao();
        userDao.delete(principal.getName());
        
        // Always return ok
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }

    @Override
    public Response delete(String username) throws JSONException{
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Check if the user exists
        UserDao userDao = new UserDao();
        User user = userDao.getActiveByUsername(username);
        if (user == null) {
            throw new ClientException("UserNotFound", "The user doesn't exist");
        }
        
        // Ensure that the admin user is not deleted
        RoleBaseFunctionDao userBaseFuction = new RoleBaseFunctionDao();
        Set<String> baseFunctionSet = userBaseFuction.findByRoleId(user.getRoleId());
        if (baseFunctionSet.contains(BaseFunction.ADMIN.name())) {
            throw new ClientException("ForbiddenError", "The admin user cannot be deleted");
        }
        
        // Delete the user
        userDao.delete(user.getUsername());
        
        // Always return ok
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        return Response.ok().entity(response).build();
    }
    @Override
    public Response checkUsername(String username) throws JSONException{
         UserDao userDao = new UserDao();
        User user = userDao.getActiveByUsername(username);
        
        JSONObject response = new JSONObject();
        if (user != null) {
            response.put("status", "ko");
            response.put("message", "Username already registered");
        } else {
            response.put("status", "ok");
        }
        
        return Response.ok().entity(response).build();
    }

    @Override
    public Response login(String username, String password, boolean longLasted) throws JSONException{
        // Validate the input data
        username = StringUtils.strip(username);
        password = StringUtils.strip(password);

        // Get the user
        UserDao userDao = new UserDao();
        String userId = userDao.authenticate(username, password);
        if (userId == null) {
            throw new ForbiddenClientException();
        }
            
        // Create a new session token
        AuthenticationTokenDao authenticationTokenDao = new AuthenticationTokenDao();
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setUserId(userId);
        authenticationToken.setLongLasted(longLasted);
        String token = authenticationTokenDao.create(authenticationToken);
        
        // Cleanup old session tokens
        authenticationTokenDao.deleteOldSessionToken(userId);

        JSONObject response = new JSONObject();
        int maxAge = longLasted ? TokenBasedSecurityFilter.TOKEN_LONG_LIFETIME : -1;
        NewCookie cookie = new NewCookie(TokenBasedSecurityFilter.COOKIE_NAME, token, "/", null, null, maxAge, false);
        return Response.ok().entity(response).cookie(cookie).build();
    }

    @Override
    public NewCookie createSessionCookie() {
        // Implementation for creating session cookie
        return null; // Placeholder return value
    }

    @Override
    public Response logout() throws JSONException{
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // Get the value of the session token
        String authToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TokenBasedSecurityFilter.COOKIE_NAME.equals(cookie.getName())) {
                    authToken = cookie.getValue();
                }
            }
        }
        
        AuthenticationTokenDao authenticationTokenDao = new AuthenticationTokenDao();
        AuthenticationToken authenticationToken = null;
        if (authToken != null) {
            authenticationToken = authenticationTokenDao.get(authToken);
        }
        
        // No token : nothing to do
        if (authenticationToken == null) {
            throw new ForbiddenClientException();
        }
        
        // Deletes the server token
        try {
            authenticationTokenDao.delete(authToken);
        } catch (Exception e) {
            throw new ServerException("AuthenticationTokenError", "Error deleting authentication token: " + authToken, e);
        }
        
        // Deletes the client token in the HTTP response
        JSONObject response = new JSONObject();
        NewCookie cookie = new NewCookie(TokenBasedSecurityFilter.COOKIE_NAME, null);
        return Response.ok().entity(response).cookie(cookie).build();
    }

    @Override
    public Response getInfo() throws JSONException {
        JSONObject response = new JSONObject();
        if (!authenticate()) {
            response.put("anonymous", true);

            // Check if admin has the default password
            UserDao userDao = new UserDao();
            User adminUser = userDao.getById("admin");
            if (adminUser != null && adminUser.getDeleteDate() == null) {
                response.put("is_default_password", Constants.DEFAULT_ADMIN_PASSWORD.equals(adminUser.getPassword()));
            }
        } else {
            response.put("anonymous", false);
            UserDao userDao = new UserDao();
            User user = userDao.getById(principal.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("theme", user.getTheme());
            response.put("locale", user.getLocaleId());
            response.put("first_connection", user.isFirstConnection());
            JSONArray baseFunctions = new JSONArray(((UserPrincipal) principal).getBaseFunctionSet());
            response.put("base_functions", baseFunctions);
            response.put("is_default_password", hasBaseFunction(BaseFunction.ADMIN) && Constants.DEFAULT_ADMIN_PASSWORD.equals(user.getPassword()));
        }
        
        return Response.ok().entity(response).build();
    }
}