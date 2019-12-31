package com.sportstalk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
     * Completable future object
     **/
    private CompletableFuture completableFuture;
    /**
     * name of the current polling action
     **/
    private String action;

    //key:vfZSpHsWrkun7Yd_fUJcWAHrNjx6VRpEqMCEP3LJV9Tg
    public HttpClient(Context context, String httpMethod, String url, Map<String, String> apiHeaders, Map<String, String> data, APICallback apiCallback) {
        this.context = context;
        this.httpMethod = httpMethod;
        this.url = url;
        this.apiHeaders = apiHeaders;
        this.data = data;
        this.apiCallback = apiCallback;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    protected void execute() {
        Log.d(TAG, " url " + url);
        RequestQueue queue = Volley.newRequestQueue(context);
        int command = httpMethod.equals("GET") ? 0 : 1;
        if (data == null) data = new HashMap<>();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(command, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ApiResult result = new ApiResult();
                result.setData(response);
                apiCallback.execute(result, action);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " error -> " + error.getMessage());
                ApiResult result = new ApiResult();
                result.setData(error);
                apiCallback.error(result, action);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                return apiHeaders;
            }
        };
        queue.add(jsonObjectRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
            }
        });
    }
}