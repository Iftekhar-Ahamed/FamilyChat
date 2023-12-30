package com.example.familychat.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class API<T> {
    public String BaseUrl = "http://familychat.somee.com/";
    private T data;
    private Context context;

    public API(Context context) {
        this.context = context;
    }

    public void fetchData(String url, Class<T> responseType,String token, UserCallback<T> callback) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, BaseUrl+url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                ObjectMapper om = new ObjectMapper();
                                T result = om.readValue(response.toString(), responseType);
                                callback.onUserReceived(result);
                            } catch (Exception e) {
                                callback.onUserError("Error parsing response data");
                            }
                        }
                    },  new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onUserError("Error fetching data");
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }};

            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            callback.onUserError("Error in fetchData");
        }
    }

    public interface UserCallback<T> {
        void onUserReceived(T data);
        void onUserError(String errorMessage);
    }

    public void fetchDataList(String url, Class<T> responseType, String token, UserCallback<List<T>> callback) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                    (Request.Method.GET, BaseUrl + url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray  response) {
                            try {
                                ObjectMapper om = new ObjectMapper();
                                List<T> result = om.readValue(response.toString(), om.getTypeFactory().constructCollectionType(List.class, responseType));
                                callback.onUserReceived(result);
                            } catch (Exception e) {
                                callback.onUserError("Error parsing response data");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onUserError("Error fetching data");
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            callback.onUserError("Error in fetchData");
        }
    }

}
