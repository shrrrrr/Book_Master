package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.books.rest.resource.ValidationStrategy;

class UsernameValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(String username, String fieldName) throws JSONException {
        ValidationUtil.validateLength(username, fieldName, 3, 50);
        ValidationUtil.validateAlphanumeric(username, fieldName);
    }
}