package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.books.rest.resource.ValidationStrategy;

class LocaleValidationStrategy implements ValidationStrategy {
    public void validate(String localeId, String fieldName) throws JSONException {
        ValidationUtil.validateLocale(localeId, fieldName, true);
    }
}
