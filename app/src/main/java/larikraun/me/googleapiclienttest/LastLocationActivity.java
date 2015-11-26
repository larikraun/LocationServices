package larikraun.me.googleapiclienttest;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LastLocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    public TextView lastLocation;
    private GoogleApiClient mGac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lastLocation = (TextView) findViewById(R.id.last_location);
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
                .addApi(LocationServices.API).addConnectionCallbacks(this) //add this for location
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
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGac);
        if (mLastLocation != null) {
            lastLocation.setText("Location: " + mLastLocation.getLatitude() + "\nLongitude: " + mLastLocation.getLongitude());
            Log.d("lo","Location: " + mLastLocation.getLatitude() + "\nLongitude: " + mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
