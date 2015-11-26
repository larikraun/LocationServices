package larikraun.me.googleapiclienttest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Author: Omolara Adejuwon
 * Date Created: Nov 26,2015
 * Time Created: 06:51
 * Project Name: GoogleAPIClientTest
 */
public class GeofenceStore implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener
        , ResultCallback<Status> {
    String TAG = "Geofence";
    private Context mContext;
    /**
     * Google API client object.
     */
    public  GoogleApiClient mGoogleApiClient;
    /**
     * Geofencing PendingIntent
     */
    private PendingIntent mPendingIntent;

    /**
     * Location Request object.
     */
    /**
     * Geofence request.
     */
    private GeofencingRequest mGeofencingRequest;
    /**
     * List of geofences to monitor.
     */
    private ArrayList<Geofence> mGeofences;
    private LocationRequest mLocationRequest;
private Location mLastLocation;
    public GeofenceStore(Context context, ArrayList<Geofence> geofences) {
        mContext = context;
        mGeofences =geofences;
        mPendingIntent = null;
        buildApiClient();


    }

    private  void buildApiClient() {
        // Build a new GoogleApiClient, specify that we want to use LocationServices
        // by adding the API to the client, specify the connection callbacks are in
        // this class as well as the OnConnectionFailed method.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

    }

    @Override
    public void onConnected(Bundle bundle) {
// We're connected, now we need to create a GeofencingRequest with
        // the geofences we have stored.
        mGeofencingRequest = new GeofencingRequest.Builder().addGeofences(
                mGeofences).build();
        mPendingIntent = createRequestPendingIntent();
        // Submitting the request to monitor geofences.
        PendingResult<Status> pendingResult = LocationServices.GeofencingApi
                .addGeofences(mGoogleApiClient, mGeofencingRequest,
                        mPendingIntent);
        // Set the result callbacks listener to this class.
        pendingResult.setResultCallback(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Connection failed.");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location Information\n"
                + "==========\n"
                + "Provider:\t" + location.getProvider() + "\n"
                + "Lat & Long:\t" + location.getLatitude() + ", "
                + location.getLongitude() + "\n"
                + "Altitude:\t" + location.getAltitude() + "\n"
                + "Bearing:\t" + location.getBearing() + "\n"
                + "Speed:\t\t" + location.getSpeed() + "\n"
                + "Accuracy:\t" + location.getAccuracy() + "\n");
    }

    @Override
    public void onResult(Status result) {
        if (result.isSuccess()) {
            Log.v(TAG, "Success!");
        } else if (result.hasResolution()) {
            // TODO Handle resolution
        } else if (result.isCanceled()) {
            Log.v(TAG, "Canceled");
        } else if (result.isInterrupted()) {
            Log.v(TAG, "Interrupted");
        } else {

        }
    }
    /**
     * This creates a PendingIntent that is to be fired when geofence transitions
     * take place. In this instance, we are using an IntentService to handle the
     * transitions.
     *
     * @return A PendingIntent that will handle geofence transitions.
     */
    private PendingIntent createRequestPendingIntent() {
        if (mPendingIntent == null) {
            Log.v(TAG, "Creating PendingIntent");
            Intent intent = new Intent(mContext, GeoFenceTransitionsIntentService.class);
            mPendingIntent = PendingIntent.getService(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mPendingIntent;
    }
    public void disconnect(){
        mGoogleApiClient.disconnect();
    }
}
