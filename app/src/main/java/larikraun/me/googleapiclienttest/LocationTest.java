package larikraun.me.googleapiclienttest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationTest extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ResultCallback<Status>, View.OnClickListener {

    private final String TAG = "Test App";
    public TextView latitude_tv, longitude_tv, detectedActivities;
    PendingIntent mActivityDetectionPendingIntent;
    private GoogleApiClient mGac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_test);

        latitude_tv = (TextView) findViewById(R.id.latitude_holder);
        longitude_tv = (TextView) findViewById(R.id.longitude_holder);
        detectedActivities = (TextView) findViewById(R.id.activity_update_holder);
        Button requestButton = (Button) findViewById(R.id.request_activity);
        Button removeButton = (Button) findViewById(R.id.remove_activity);
        requestButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
        buildGoogleApiClient();

    }

    private void buildGoogleApiClient() {
        mGac = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this) //add this for location
              //  .addApi(ActivityRecognition.API) //add this for Activity recognition
                .addOnConnectionFailedListener(this)
                .build();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGac);
        if (mLastLocation != null) {
            latitude_tv.setText("" + mLastLocation.getLatitude());
            longitude_tv.setText("" + mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        latitude_tv.setText(connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        //tv.setText(String.valueOf(location.getLatitude()));
        latitude_tv.setText("" + location.getLatitude());
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

    @Override
    public void onResult(Status status) {
        Toast.makeText(this, "Executed", Toast.LENGTH_SHORT).show();
    }


    public class DetectedActivityIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "me.larikraun") {
                ArrayList<DetectedActivity> activities = intent.getParcelableArrayListExtra("activities");
                if (activities != null && !activities.isEmpty()) {
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

        /**
         * @param typeCode code
         * @return Activity
         */
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
    }
}
