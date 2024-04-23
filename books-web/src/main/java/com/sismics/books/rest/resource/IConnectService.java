package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import com.sismics.books.core.dao.jpa.dto.UserAppDto;
import com.sismics.books.core.model.jpa.UserApp;
import java.util.List;

public interface IConnectService {
    List<UserAppDto> getUserApps(String userId);
    UserApp addUserApp(String userId, String appIdString, String accessToken) throws JSONException;
    void deleteUserApp(String userId, String appIdString) throws JSONException;
    void updateUserApp(String userId, String appIdString, Boolean sharing) throws JSONException;
    List<JSONObject> getContactList(String userId, String appIdString, String query, int offset, int limit) throws JSONException;
}

