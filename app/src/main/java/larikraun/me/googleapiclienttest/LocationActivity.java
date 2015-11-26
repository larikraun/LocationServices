package larikraun.me.googleapiclienttest;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static Location mLastLocation;
    private final String TAG = "Test App";
    public TextView latitude_tv, longitude_tv;
    TextView locationUpdate;
    private GoogleApiClient mGac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationUpdate = (TextView) findViewById(R.id.location_text);
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

    private void buildGoogleApiClient() {
        mGac = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API) //add this for location
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override // from connectionCallbacks implementation
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGac, mLocationRequest, this);

    }

    @Override //from connectionCallbacks implementation
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Connection has been suspended", Toast.LENGTH_LONG).show();
    }

    @Override //from onConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection has failed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "Location has changed", Toast.LENGTH_LONG).show();
        String locationDetails = "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude();
        locationUpdate.setText(locationDetails);
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
}
