package larikraun.me.googleapiclienttest;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author: LANREWAJU
 * Date Created: Jul 13,2015
 * Time Created: 19:57
 * Project Name: PigeonEx2
 */
public class FetchData {
    Context mContext;
    ProgressDialog pDialog;

    public FetchData(Context context) {
        mContext = context;
    }


    /**
     * A method to download json data from url
     */
    public static String callUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return data;
    }
}
