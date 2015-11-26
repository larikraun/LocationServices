package larikraun.me.googleapiclienttest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class DetectionActivity extends AppCompatActivity implements View.OnClickListener, ResultCallback {
    TextView detectedActivities;
    PendingIntent mActivityDetectionPendingIntent;
    GoogleApiClient mGac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button requestButton = (Button) findViewById(R.id.request_activity);
        Button removeButton = (Button) findViewById(R.id.remove_activity);
        requestButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
        detectedActivities = (TextView) findViewById(R.id.detection);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        buildGoogleApiClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGac.isConnected()) {
            mGac.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGac.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(new DetectedActivityIntentReceiver());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new DetectedActivityIntentReceiver(), new IntentFilter("me.larikraun"));
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {

        // Reuse the PendingIntent if we already have it.
        if (mActivityDetectionPendingIntent != null) {
            return mActivityDetectionPendingIntent;
        }
        Intent intent = new Intent(this, DetectedActivityIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void buildGoogleApiClient() {
        mGac = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API) //add this for Activity recognition
                        //.addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remove_activity:
                removeActivityUpdate();
                break;
            case R.id.request_activity:
                requestActivityUpdate();
                break;
        }
    }

    private void requestActivityUpdate() {
        ActivityRecognition.ActivityRecognitionApi
                .requestActivityUpdates(mGac, 2000, getActivityDetectionPendingIntent())
                .setResultCallback(this);

    }

    private void removeActivityUpdate() {
        ActivityRecognition.ActivityRecognitionApi
                .removeActivityUpdates(mGac, getActivityDetectionPendingIntent())
                .setResultCallback(this);

    }

    @Override
    public void onResult(Result result) {
        Toast.makeText(getApplicationContext(), "Result has arrived " + result.getStatus(), Toast.LENGTH_LONG).show();
    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Toast.makeText(getApplicationContext(), "Result has arrived " + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
//    }

    private String getActivityName(int typeCode) {
        switch (typeCode) {
            case DetectedActivity.IN_VEHICLE:
                return "in vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on bike";
            case DetectedActivity.ON_FOOT:
                return "on foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.WALKING:
                return "walking";
            case DetectedActivity.RUNNING:
                return "running";
            default:
                return "unknown";
        }
    }

    public class DetectedActivityIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("me.larikraun")) {
                ArrayList<DetectedActivity> activities = intent.getParcelableArrayListExtra("activities");
                String activitiesList = "";
                for (DetectedActivity detectedActivity : activities) {
                    activitiesList = activitiesList + getActivityName(detectedActivity.getType()) + " " + detectedActivity.getConfidence() + "\n";
                    detectedActivities.setText(activitiesList);
                }
            } else {
                detectedActivities.setText("Empty");
            }
        }

    }
}

