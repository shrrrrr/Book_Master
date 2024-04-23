package com.sismics.books.rest.service; 

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import org.apache.commons.lang.StringUtils;

import com.sismics.books.rest.resource.IConnectService;
import com.sismics.books.core.dao.jpa.UserAppDao;
import com.sismics.books.core.dao.jpa.UserContactDao;
import com.sismics.books.core.service.FacebookService;
import com.sismics.books.core.dao.jpa.dto.UserAppDto;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.UserApp;
import com.sismics.books.core.constant.AppId;
import com.sismics.rest.exception.ClientException;
import com.sismics.books.core.event.UserAppCreatedEvent;
import com.sismics.books.core.service.facebook.AuthenticationException;
import com.sismics.books.core.service.facebook.PermissionException;
import com.sismics.books.core.dao.jpa.dto.UserContactDto;
import com.sismics.books.core.dao.jpa.criteria.UserContactCriteria;
import com.sismics.books.core.util.jpa.PaginatedList;
import com.sismics.books.core.util.jpa.PaginatedLists;


public class ConnectService implements IConnectService{
    // Data Access Objects to use the Data Access Layer 
    private UserAppDao userAppDao = new UserAppDao();
    private UserContactDao userContactDao = new UserContactDao();
    // AppContext is a singelton class. getInstance() is to get the single instance of AppContext which has a function getFacebookService(). 
    private FacebookService facebookService;
    
    public ConnectService(FacebookService facebookService){
        this.facebookService = facebookService;
    }

    public List<UserAppDto> getUserApps(String userId){
        return userAppDao.findByUserId(userId);
    }

    public UserApp addUserApp(String userId, String appIdString, String accessToken) throws JSONException {
        AppId appId = getAppId(appIdString);
        UserApp userApp = null;

        switch (appId) {
            case FACEBOOK:
                userAppDao.deleteByUserIdAndAppId(userId, appId.name());

                String extendedAccessToken = null;
                try{
                    extendedAccessToken = facebookService.getExtendedAccessToken(accessToken);
                } catch (AuthenticationException e) {
                    throw new ClientException("InvalidAuthenticationToken", "Error validating authentication token", e);
                }

                try {
                    facebookService.validatePermission(extendedAccessToken);
                } catch (PermissionException e) {
                    throw new ClientException("PermissionNotFound", e.getMessage(), e);
                }

                userApp = new UserApp();
                userApp.setAppId(appId.name());
                userApp.setAccessToken(extendedAccessToken);
                userApp.setUserId(userId);
                userApp.setSharing(true);

                facebookService.updateUserData(extendedAccessToken, userApp);

                userAppDao.create(userApp);
                break;

        }

        UserAppCreatedEvent userAppCreatedEvent = new UserAppCreatedEvent();
        userAppCreatedEvent.setUserApp(userApp);
        AppContext.getInstance().getAsyncEventBus().post(userAppCreatedEvent);

        return userApp;
    }

    public void deleteUserApp(String userId, String appIdString)throws JSONException{
        AppId appId = getAppId(appIdString);
        userAppDao.deleteByUserIdAndAppId(userId, appId.name());
    }

    public void updateUserApp(String userId, String appIdString, Boolean sharing)throws JSONException{
        AppId appId = getAppId(appIdString);
        UserApp userApp = userAppDao.getActiveByUserIdAndAppId(userId, appId.name());

        if(userApp == null){
            throw new ClientException("AppNotConnected", MessageFormat.format("You are not connected to the app {0}", appId.name()));
        }

        userApp.setSharing(sharing);
        userAppDao.update(userApp);
    }

    public List<JSONObject> getContactList(String userId, String appIdString, String query, int offset, int limit) throws JSONException{
        AppId appId = getAppId(appIdString);
        UserApp userApp = userAppDao.getActiveByUserIdAndAppId(userId, appId.name());

        if(userApp == null){
            throw new ClientException("AppNotConnected", MessageFormat.format("You are not connected to the app {0}", appId.name()));
        }

        List<JSONObject> contacts = new ArrayList<JSONObject>();
        PaginatedList<UserContactDto> paginatedList = PaginatedLists.create(limit, offset);

        UserContactCriteria criteria = new UserContactCriteria();
        criteria.setAppId(appId.name());
        criteria.setUserId(userId);
        criteria.setQuery(query);

        userContactDao.findByCriteria(paginatedList, criteria);
        for (UserContactDto userContactDto : paginatedList.getResultList()) {
            JSONObject userContact = new JSONObject();
            userContact.put("id", userContactDto.getId());
            userContact.put("external_id", userContactDto.getExternalId());
            userContact.put("full_name", userContactDto.getFullName());
            contacts.add(userContact);
        }
        
        return contacts;
    }

    private AppId getAppId(String appIdString) throws JSONException {
        AppId appId = null;
        try {
            appId = AppId.valueOf(StringUtils.upperCase(appIdString));
        } catch (Exception e) {
            throw new ClientException("AppNotFound", MessageFormat.format("Application not found: {0}", appIdString));
        }
        return appId;
    }
}