package larikraun.me.googleapiclienttest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GeoFenceTransitionsIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 0;
    private String TAG = "GeoFencing";

    public GeoFenceTransitionsIntentService() {
        super("GeoFenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("Geofence", "Yayyyyayyay!!! It entered here 2");
        if (intent != null) {
            final String action = intent.getAction();
            Log.e(TAG, "Yayyyyayyay!!! It entered here ooo");
         //   Toast.makeText(GeoFenceTransitionsIntentService.this, "Yayyyyayyay!!! It entered here ooo", Toast.LENGTH_SHORT).show();
            GeofencingEvent gfe = GeofencingEvent.fromIntent(intent);
            if (gfe.hasError()) {
                Log.e(TAG, "error " + String.valueOf(gfe.getErrorCode()));
                return;
            }
            // Get the transition type.
            int geofenceTransition = gfe.getGeofenceTransition();
            Log.e(TAG, "Transition code " + geofenceTransition);
            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = gfe.getTriggeringGeofences();
                // Get the transition details as a String.
                String geoFencingDetails = onHandleIntentHelper(triggeringGeofences);
                // Send notification and log the transition details.
                sendNotification(geoFencingDetails);
                Log.i(TAG, geoFencingDetails);
            } else {
                // Log the error.
                Log.e(TAG, "" + geofenceTransition);
            }
        }
    }

    private String onHandleIntentHelper(List<Geofence> geofenceList) {
        String ids = " ";
        for (Geofence geoFence : geofenceList) {
            ids = ids + geoFence.getRequestId();
        }
        return ids;
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NewGeofenceActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("GeoFencing Test")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
