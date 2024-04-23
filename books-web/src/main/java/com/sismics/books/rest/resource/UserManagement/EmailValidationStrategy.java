package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.books.rest.resource.ValidationStrategy;


class EmailValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(String email, String fieldName) throws JSONException {
        ValidationUtil.validateLength(email, fieldName, 3, 50);
        ValidationUtil.validateEmail(email, fieldName);
    }
}
