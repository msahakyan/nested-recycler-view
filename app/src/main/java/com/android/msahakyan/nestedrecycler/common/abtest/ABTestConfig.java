package com.android.msahakyan.nestedrecycler.common.abtest;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.amazon.insights.AmazonInsights;
import com.amazon.insights.InsightsCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by msahakyan on 05/12/15.
 */
public class ABTestConfig {

    private AmazonInsights sInstance;

    /**
     * Returns singleton instance of AmazonInsights
     *
     * @param context
     * @return {com.amazon.insights.AmazonInsights}
     */
    public AmazonInsights getInstance(Context context) {
        if (sInstance == null) {
            final InsightsCredentials credentials = getCredentials(context);
            sInstance = AmazonInsights.newInstance(credentials, context.getApplicationContext());
        }

        return sInstance;
    }

    /**
     * Returns instance of InsightsCredentials
     *
     * @param context
     * @return {com.amazon.insights.InsightsCredentials}
     */
    private InsightsCredentials getCredentials(Context context) {
        // Create a credentials object using the keys from the
        // Amazon Apps & Games Developer Portal's A/B Testing site.
        String[] amazonCreds = loadCredentials(context);
        return AmazonInsights.newCredentials(amazonCreds[0], amazonCreds[1]);
    }

    /**
     * Loads Amazon credentials from properties file
     *
     * @return {String[]}
     */
    private String[] loadCredentials(Context context) {

        Resources resources = context.getResources();
        AssetManager assetManager = resources.getAssets();
        String[] creds = new String[2];

        // Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open("amazon.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            String publicKey = properties.getProperty("public_key");
            String privateKey = properties.getProperty("private_key");

            creds[0] = publicKey;
            creds[1] = privateKey;

        } catch (IOException e) {
            System.err.println("Failed get property file");
            e.printStackTrace();
        }

        return creds;
    }
}
