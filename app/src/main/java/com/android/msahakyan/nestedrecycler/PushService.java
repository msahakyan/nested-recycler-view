package com.android.msahakyan.nestedrecycler;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.android.msahakyan.nestedrecycler.activity.MainActivity;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.PushNotification;
import com.android.msahakyan.nestedrecycler.net.Endpoint;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author msahakyan
 */
public class PushService extends IntentService {

    private static final String TAG = PushService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 555;
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 1024;

    public PushService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            PushNotification notification = getNotificationFromIntent(intent);
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(BundleKey.EXTRA_MOVIE_ID, notification.getId());
            getNotificationImagesAndShow(intent, notification, new HashMap<String, Bitmap>(2));
        }
    }

    private PushNotification getNotificationFromIntent(Intent intent) {
        PushNotification pn = intent.getParcelableExtra(BundleKey.EXTRA_NOTIFICATION);
        pn.setIconUrl(Endpoint.IMAGE + "/w185/" + pn.getIconUrl());
        pn.setImageUrl(Endpoint.IMAGE + "/w500/" + pn.getImageUrl());

        return pn;
    }

    private void getNotificationImage(final String imageUrl, final HashMap<String, Bitmap> images, final Runnable runnable) {
        if (imageUrl != null) {
            ImageRequest request = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    images.put(imageUrl, response);
                    runnable.run();
                }
            }, MAX_WIDTH, MAX_HEIGHT, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    runnable.run();
                }
            });
            AppController.getInstance().addToRequestQueue(request);

        } else {
            runnable.run();
        }
    }

    private void getNotificationImagesAndShow(final Intent intent, final PushNotification notification, final HashMap<String, Bitmap> images) {
        getNotificationImage(notification.getImageUrl(), images, new Runnable() {
            @Override
            public void run() {
                getNotificationImage(notification.getIconUrl(), images, new Runnable() {
                    @Override
                    public void run() {
                        showNotification(intent, notification, images);
                    }
                });
            }
        });
    }

    public void showNotification(Intent intent, PushNotification notification, Map<String, Bitmap> images) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_movies)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(notification.getMessage())
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);
        if (images != null) {
            Bitmap icon = images.get(notification.getIconUrl());
            if (icon != null) {
                builder.setLargeIcon(icon);
            }

            Bitmap image = images.get(notification.getImageUrl());
            if (image != null) {
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .setBigContentTitle(getString(R.string.app_name))
                    .bigLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .bigPicture(image)
                    .setSummaryText(notification.getMessage()));
            }
        }

        if (notification.getPriority() == PushNotification.PRIORITY_HIGH) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

