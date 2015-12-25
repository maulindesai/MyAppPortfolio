package com.maulin.popularmovies;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by maulin on 11/12/15.
 */
public class utility {

    private static final String TAG = "utility";

    private utility(){}

    private static final utility mUtility;

    static {
        mUtility=new utility();
    }

    /**
     * get utility object
     * @return
     */
    public static utility getUtility() {
        return mUtility;
    }

    /**
     * download url
     */
    public String downloadUrl(String tmdb_url) {
        InputStream inputStream=null;
        try {
            URL url=new URL(tmdb_url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            //check status code
            int responseCode=httpURLConnection.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK) {
                inputStream=httpURLConnection.getInputStream();
                //pares input stream
                return readIt(inputStream);
            } else {
                Log.d(TAG,responseCode+"");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(inputStream!=null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * convert input stream to string
     * @param inputStream inputStream
     */
    private String readIt(InputStream inputStream) {
        InputStreamReader reader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(reader);
        StringBuilder builder=new StringBuilder();
        String thisLine="";
        try {
            while((thisLine=bufferedReader.readLine())!=null){
                builder.append(thisLine+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
