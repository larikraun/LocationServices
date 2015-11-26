package larikraun.me.googleapiclienttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GeocodingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGac;
    Location mLastLocation;
    TextView addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoding);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        addressView = (TextView)findViewById(R.id.address_view);
        setSupportActionBar(toolbar);
        buildGoogleApiClient();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(GeocodingActivity.this, GeoFenceTransitionsIntentService.class);
                intent.putExtra("location_details", mLastLocation);
                startService(intent);
            }
        });

    }

    public void buildGoogleApiClient() {
        mGac = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
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
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(new GeocodingReceiver());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new GeocodingReceiver(), new IntentFilter("me.larikraun.geocoding"));
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGac);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class GeocodingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("me.larikraun.geocoding")) {
               String addresses = intent.getStringExtra("addresses");
                addressView.setText(addresses);
            }
        }
    }
}
