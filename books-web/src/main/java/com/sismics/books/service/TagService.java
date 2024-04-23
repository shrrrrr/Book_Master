package com.sismics.books.core.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.sismics.books.core.dao.jpa.TagDao;
import com.sismics.books.core.dao.jpa.dto.TagDto;
import com.sismics.books.core.model.jpa.Tag;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.util.ValidationUtil;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TagService {
    private TagDao tagDao = new TagDao();

    public List<JSONObject> getAllTagsByUserId(String userId) throws JSONException {
        List<Tag> tagList = tagDao.getByUserId(userId);
        List<JSONObject> tagJsonObjects = new ArrayList<>();
        for (Tag tag : tagList) {
            JSONObject tagJson = new JSONObject();
            tagJson.put("id", tag.getId());
            tagJson.put("name", tag.getName());
            tagJson.put("color", tag.getColor());
            tagJsonObjects.add(tagJson);
        }
        return tagJsonObjects;
    }

    public String createTag(String userId, String name, String color) throws JSONException {
        name = ValidationUtil.validateLength(name, "name", 1, 36, false);
        ValidationUtil.validateHexColor(color, "color", true);

        if (name.contains(" ")) {
            throw new ClientException("SpacesNotAllowed", "Spaces are not allowed in tag name");
        }

        Tag tag = tagDao.getByName(userId, name);
        if (tag != null) {
            throw new ClientException("AlreadyExistingTag", MessageFormat.format("Tag already exists: {0}", name));
        }

        tag = new Tag();
        tag.setName(name);
        tag.setColor(color);
        tag.setUserId(userId);
        return tagDao.create(tag);
    }

    public void updateTag(String userId, String tagId, String name, String color) throws JSONException {
        name = ValidationUtil.validateLength(name, "name", 1, 36, true);
        ValidationUtil.validateHexColor(color, "color", true);

        if (name.contains(" ")) {
            throw new ClientException("SpacesNotAllowed", "Spaces are not allowed in tag name");
        }

        Tag tag = tagDao.getByTagId(userId, tagId);
        if (tag == null) {
            throw new ClientException("TagNotFound", MessageFormat.format("Tag not found: {0}", tagId));
        }

        Tag tagDuplicate = tagDao.getByName(userId, name);
        if (tagDuplicate != null && !tagDuplicate.getId().equals(tagId)) {
            throw new ClientException("AlreadyExistingTag", MessageFormat.format("Tag already exists: {0}", name));
        }

        if (!name.isEmpty()) {
            tag.setName(name);
        }
        if (!color.isEmpty()) {
            tag.setColor(color);
        }
    }

    public void deleteTag(String userId, String tagId) throws JSONException {
        Tag tag = tagDao.getByTagId(userId, tagId);
        if (tag == null) {
            throw new ClientException("TagNotFound", MessageFormat.format("Tag not found: {0}", tagId));
        }
        tagDao.delete(tagId);
    }
}