package com.android.msahakyan.nestedrecycler.net;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * @author msahakyan
 */
public interface NetworkRequestListener {
    /**
     * Success callback
     *
     * @param jsonResponse
     */
    void onSuccess(JSONObject jsonResponse);

    /**
     * Error callback
     *
     * @param error
     */
    void onError(VolleyError error);
}
