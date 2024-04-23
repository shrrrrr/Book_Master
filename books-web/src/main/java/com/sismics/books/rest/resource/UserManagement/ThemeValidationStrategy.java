package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.books.rest.resource.ValidationStrategy;


class ThemeValidationStrategy implements ValidationStrategy {
    public void validate(String themeId, String fieldName) throws JSONException {
        ValidationUtil.validateTheme(themeId, fieldName, true);
    }
}
