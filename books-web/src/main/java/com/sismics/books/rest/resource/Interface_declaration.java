package com.sismics.books.rest.resource;

import org.codehaus.jettison.json.JSONException;

import com.restfb.WebRequestor.Response;

/**
 * interface_declaration
 */
public interface Interface_declaration {

    Response list(String principal) throws JSONException;

    Response add(String principal,String appIdString,String accessIdString) throws JSONException; 

    Response remove(String principal,String appIdString) throws JSONException;

    Response update(String principle,String appIdString,boolean sharing) throws JSONException;

    Response contactList (String principal,String appIdString,String query,Integer limit,Integer offset) throws JSONException;

    Response getAppId(String appIdString) throws JSONException;
}