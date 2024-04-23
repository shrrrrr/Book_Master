package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;
import com.sismics.rest.util.ValidationUtil;

public interface ValidationStrategy {
    void validate(String input, String fieldName) throws JSONException;
}

