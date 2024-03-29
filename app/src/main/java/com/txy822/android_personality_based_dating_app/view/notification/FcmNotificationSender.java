package com.txy822.android_personality_based_dating_app.view.notification;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.txy822.android_personality_based_dating_app.BuildConfig;
import com.txy822.android_personality_based_dating_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender  {

    private static final String TAG = "TAG";
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    //posting url for postman using HTTP protocol
    //Select POST . Enter request URL as postUrl

    //private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/datingapp-5b017/messages:send";

    //Add Headers Authorization: key , key from fcm server key
    private final String fcmServerKey  = BuildConfig.fcmServerKey;

    public FcmNotificationSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    /**
     * Sends notification
     */
    public void sendNotification() {


       // Create a HTTP request using Volley  library y
        requestQueue = Volley.newRequestQueue(mActivity);
        //Put all your notification content in a JSON OBJECT
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.drawable.ic_app);

            mainObj.put("notification", notiObject);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "request created");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error
                    Log.d(TAG, "Error in creating request");
                }
            }) {
                //Customize the header of the  request
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                   // header.put("authorization", "key=" + fcmServerKey);
                    header.put("authorization", "Bearer" + fcmServerKey);

                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}