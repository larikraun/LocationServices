package larikraun.me.googleapiclienttest;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Author: LANREWAJU
 * Date Created: Sep 11,2015
 * Time Created: 20:16
 * Project Name: GoogleAPIClientTest
 */
public class DetectedActivityIntentService extends IntentService {
    private static final String TAG = "DetectedIntentService";
    ArrayList<DetectedActivity> dActivities;

    public DetectedActivityIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult aar = ActivityRecognitionResult.extractResult(intent);
        dActivities = (ArrayList) aar.getProbableActivities();
        Intent newIntent = new Intent("me.larikraun");
        newIntent.putParcelableArrayListExtra("activities", dActivities);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);
    }
}
