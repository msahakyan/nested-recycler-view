package com.android.msahakyan.nestedrecycler.net;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;

import java.net.HttpURLConnection;

/**
 * @author msahakyan
 */
public class CustomRetryPolicy extends DefaultRetryPolicy {

    public CustomRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
        super(initialTimeoutMs, maxNumRetries, backoffMultiplier);
    }

    @Override
    public void retry(VolleyError error) throws VolleyError {
        if ((error.networkResponse.statusCode < HttpURLConnection.HTTP_INTERNAL_ERROR &&
            error.networkResponse.statusCode != HttpURLConnection.HTTP_NOT_FOUND)) {
            throw error;
        }
        super.retry(error);
    }
}
