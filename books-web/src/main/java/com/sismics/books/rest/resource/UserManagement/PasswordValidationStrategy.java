package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.books.rest.resource.ValidationStrategy;

class PasswordValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(String password, String fieldName) throws JSONException {
        ValidationUtil.validateLength(password, fieldName, 8, 50);
    }
}