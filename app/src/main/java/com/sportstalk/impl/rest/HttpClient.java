package com.sportstalk.impl.rest;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sportstalk.api.APICallback;
import com.sportstalk.api.chat.EventHandler;
import com.sportstalk.models.common.ApiResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.RequiresApi;

@TargetApi(Build.VERSION_CODES.N)
@RequiresApi(api = Build.VERSION_CODES.N)
public class HttpClient {

    /**
     * log handler
     **/
    private final String TAG = HttpClient.class.getName();
    /**
     * Volley request queue
     **/
    private RequestQueue queue;
    /**
     * Android application context
     **/
    private Context context;
    /**
     * HTTP method
     **/
    private String httpMethod;
    /**
     * endpoint
     **/
    private String url;
    /**
     * request headers
     **/
    private Map<String, String> apiHeaders;
    /**
     * data payload
     **/
    private Map<String, String> data;
    /**
     * callback object
     **/
    private APICallback apiCallback;
    /**
     * name of the current polling action
     **/
    private String action;

    private JsonObjectRequest jsonObjectRequest;

    private EventHandler eventHandler;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private JSONObject jsonObject;

    private VolleyError volleyError;

    public HttpClient(Context context, String httpMethod, String url, Map<String, String> apiHeaders, Map<String, String> data, APICallback apiCallback) {
        this.context = context;
        this.httpMethod = httpMethod;
        this.url = url;
        this.apiHeaders = apiHeaders;
        this.data = data;
        this.apiCallback = apiCallback;
        queue = Volley.newRequestQueue(context);
        initVolley();
    }

    public HttpClient(Context context, String httpMethod, String url, Map<String, String> apiHeaders, Map<String, String> data, EventHandler eventHandler) {
        this.context = context;
        this.httpMethod = httpMethod;
        this.url = url;
        this.apiHeaders = apiHeaders;
        this.data = data;
        this.eventHandler = eventHandler;
        queue = Volley.newRequestQueue(context);
        initVolley();
    }

    public void setAction(final String action) {
        this.action = action;
    }

    private void initVolley() {
        int command = getHttpCommandType(httpMethod);
        if (data == null) data = new HashMap<>();
        jsonObjectRequest = new JsonObjectRequest(command, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;
                ApiResult result = new ApiResult();
                result.setData(response);
                countDownLatch.countDown();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " error -> " + error.getMessage());
                volleyError = error;
                String actualError = new String(error.networkResponse.data);
                Log.d(TAG, " actual error -> " + actualError);
                ApiResult result = new ApiResult();
                result.setErrors(actualError);
                countDownLatch.countDown();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                return apiHeaders;
            }

            @Override
            public Priority getPriority() {
                if (!url.endsWith("updates")) return Priority.IMMEDIATE;
                return super.getPriority();
            }

        };
        Log.d(TAG, "request::: " + new String(jsonObjectRequest.getBody()));
    }

    /**
     * execute the HTTP requests using Volley
     **/
    public ApiResult execute() {
        Log.d(TAG, " request " + jsonObjectRequest);

        if (url.endsWith("updates")) {
            jsonObjectRequest.setTag("updates");
        } else queue.cancelAll("updates");

        queue.add(jsonObjectRequest);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ApiResult apiResult = new ApiResult();
        if (volleyError != null) apiResult.setErrors(volleyError);
        else
            apiResult.setData(jsonObject);
        return apiResult;
    }

    private int getHttpCommandType(String method) {
        int command = 0;

        if ("POST".equals(method)) return 1;
        else if ("PUT".equals(method)) return 2;
        else if ("DELETE".equals(method)) return 3;

        return command;
    }

}