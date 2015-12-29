package com.android.msahakyan.nestedrecycler.net;

import java.util.Map;

/**
 * Created by msahakyan on 23/12/15.
 */
public interface INetworkUtils {

    /**
     * Creates JsonRequest
     *
     * @param method
     * @param endpoint
     * @param urlParams
     * @param requestListener
     */
    void executeJsonRequest(int method, StringBuilder endpoint, Map<String, String> urlParams, NetworkRequestListener requestListener);
}
