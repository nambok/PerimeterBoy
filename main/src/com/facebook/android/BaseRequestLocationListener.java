package com.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.util.Log;

import com.facebook.android.AsyncLocationRunner.RequestLocationListener;
import com.facebook.android.MyLocationError;

/**
 * Skeleton base class for RequestListeners, providing default error handling.
 * Applications should handle these error conditions.
 */
public abstract class BaseRequestLocationListener implements RequestLocationListener {

    @Override
    public void onMyLocationError(MyLocationError e, final Object state) {
        Log.e("MyLocation", e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onFileNotFoundException(FileNotFoundException e, final Object state) {
        Log.e("MyLocation", e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onIOException(IOException e, final Object state) {
        Log.e("MyLocation", e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onMalformedURLException(MalformedURLException e, final Object state) {
        Log.e("MyLocation", e.getMessage());
        e.printStackTrace();
    }

}
