package larikraun.me.googleapiclienttest;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GeocodingIntentService extends IntentService {


    public GeocodingIntentService() {
        super("GeocodingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (intent != null) {
            Location location = intent.getParcelableExtra("location_details");
            List<Address> addresses;
            ArrayList<String> addressFragments = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent newIntent = new Intent("me.larikraun.geocoding");
           String address= TextUtils.join(", ",
                    addressFragments);
            newIntent.putExtra("addresses", address);
            Log.d("Geocoding Test", "addresses sent");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);

        }
    }

}
