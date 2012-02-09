/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;

/**
 */
public class MyLocation {

    // Strings used in the authorization flow
    public static final String REDIRECT_URI = "http://location.visual-splash.com/api/success";
    public static final String CANCEL_URI = "http://location.visual-splash.com/api/cancel";

    // Facebook server endpoints: may be modified in a subclass for testing
    protected static String DIALOG_BASE_URL =
        "http://location.visual-splash.com/api/dialog/";
    protected static String BASE_URL =
        "http://location.visual-splash.com/api/";

    /**
     * Constructor 
     */
    public MyLocation() {
        
    }

    /**
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * Example:
     * <code>
     *  Bundle parameters = new Bundle();
     *  parameters.putString("method", "auth.expireSession");
     *  String response = request(parameters);
     * </code>
     *
     * @param parameters
     *            Key-value pairs of parameters to the request. Refer to the
     *            documentation: one of the parameters must be "method".
     * @throws IOException
     *            if a network error occurs
     * @throws MalformedURLException
     *            if accessing an invalid endpoint
     * @throws IllegalArgumentException
     *            if one of the parameters is not "method"
     * @return JSON string representation of the response
     */
    public String request(Bundle parameters)
            throws MalformedURLException, IOException {
        if (!parameters.containsKey("method")) {
            throw new IllegalArgumentException("API method must be specified. "
                    + "(parameters must contain key \"method\" and value)");
        }
        return request(null, parameters, "GET");
    }

    /**
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath)
            throws MalformedURLException, IOException {
        return request(graphPath, new Bundle(), "GET");
    }

    /**
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath
     * @param parameters
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath, Bundle parameters)
            throws MalformedURLException, IOException {
        return request(graphPath, parameters, "GET");
    }

    /**
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath
     * @param params
     * @param httpMethod
     *            http verb, e.g. "GET", "POST", "DELETE"
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath, Bundle params, String httpMethod)
            throws FileNotFoundException, MalformedURLException, IOException {
        params.putString("format", "json");
        
        String url = BASE_URL + graphPath;
        return UtilLocation.openUrl(url, httpMethod, params);
    }

    /**
     * Generate a UI dialog for the request action in the given Android context.
     *
     * Note that this method is asynchronous and the callback will be invoked in
     * the original calling thread (not in a background thread).
     *
     * @param context
     *            The Android context in which we will generate this dialog.
     * @param action
     *            String representation of the desired method: e.g. "login",
     *            "stream.publish", ...
     * @param listener
     *            Callback interface to notify the application when the dialog
     *            has completed.
     */
    public void dialog(Context context, String action,
    		DialogLocationListener listener) {
        dialog(context, action, new Bundle(), listener);
    }

    /**
     * Generate a UI dialog for the request action in the given Android context
     * with the provided parameters.
     *
     * Note that this method is asynchronous and the callback will be invoked in
     * the original calling thread (not in a background thread).
     *
     * @param context
     *            The Android context in which we will generate this dialog.
     * @param action
     *            String representation of the desired method: e.g. "feed" ...
     * @param parameters
     *            String key-value pairs to be passed as URL parameters.
     * @param listener
     *            Callback interface to notify the application when the dialog
     *            has completed.
     */
    public void dialog(Context context, String action, Bundle parameters,
            final DialogLocationListener listener) {

        String endpoint = DIALOG_BASE_URL + action;
        parameters.putString("display", "touch");
        parameters.putString("redirect_uri", REDIRECT_URI);

        String url = endpoint + "?" + UtilLocation.encodeUrl(parameters);
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            UtilLocation.showAlert(context, "Error",
                    "Application requires permission to access the Internet");
        } else {
            new LcDialogLocation(context, url, listener).show();
        }
    }

    /**
     * Callback interface for dialog requests.
     *
     */
    public static interface DialogLocationListener {

        /**
         * Called when a dialog completes.
         *
         * Executed by the thread that initiated the dialog.
         *
         * @param values
         *            Key-value string pairs extracted from the response.
         */
        public void onComplete(Bundle values);

        /**
         * Called when a Facebook responds to a dialog with an error.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onLocationError(MyLocationError e);

        /**
         * Called when a dialog has an error.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onError(DialogErrorLocation e);

        /**
         * Called when a dialog is canceled by the user.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onCancel();

    }
    
    /**
     * Callback interface for service requests.
     */
    public static interface ServiceLocationListener {

        /**
         * Called when a service request completes.
         * 
         * @param values
         *            Key-value string pairs extracted from the response.
         */
        public void onComplete(Bundle values);

        /**
         * Called when a Facebook server responds to the request with an error.
         */
        public void onLocationError(MyLocationError e);

        /**
         * Called when a Facebook Service responds to the request with an error.
         */
        public void onError(Error e);

    }

}
