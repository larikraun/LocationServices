package larikraun.me.googleapiclienttest;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GeofenceActivity extends AppCompatActivity {
    /**
     * Geofences Array
     */
    ArrayList<Geofence> mGeofences;

    /**
     * Geofence Coordinates
     */
    ArrayList<LatLng> mGeofenceCoordinates;

    /**
     * Geofence Radius'
     */
    ArrayList<Integer> mGeofenceRadius;
    private GeofenceStore mGeofenceStore;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        // Initializing variables
        mGeofences = new ArrayList<Geofence>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceRadius = new ArrayList<Integer>();


        // Adding geofence coordinates to array.
        mGeofenceCoordinates.add(new LatLng(6.590727, 3.3569938));
        mGeofenceCoordinates.add(new LatLng(6.6358033, 3.3851391));
//        mGeofenceCoordinates.add(new LatLng(43.039912, -87.897038));

        // Adding associated geofence radius' to array.
        mGeofenceRadius.add(100);
        mGeofenceRadius.add(100);
//        mGeofenceRadius.add(160);
//        mGeofenceRadius.add(160);

        // Adebola House
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Adebola House")
                        // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(0).latitude, mGeofenceCoordinates.get(0).longitude, mGeofenceRadius.get(0).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(1000)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());
        mGeofences.add(new Geofence.Builder()
                .setRequestId("my house")
                        // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(1).latitude, mGeofenceCoordinates.get(1).longitude, mGeofenceRadius.get(0))
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(1000)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());


        mGeofenceStore = new GeofenceStore(this, mGeofences);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGac.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGeofenceStore.disconnect();
        //mGac.connect();
    }

}
