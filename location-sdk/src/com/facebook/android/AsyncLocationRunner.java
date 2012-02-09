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

import android.content.Context;
import android.os.Bundle;

/**
 * A sample implementation of asynchronous API requests. This class provides
 * the ability to execute API methods and have the call return immediately,
 * without blocking the calling thread. This is necessary when accessing the
 * API in the UI thread, for instance. The request response is returned to 
 * the caller via a callback interface, which the developer must implement.
 *
 * This sample implementation simply spawns a new thread for each request,
 * and makes the API call immediately.  This may work in many applications,
 * but more sophisticated users may re-implement this behavior using a thread
 * pool, a network thread, a request queue, or other mechanism.  Advanced
 * functionality could be built, such as rate-limiting of requests, as per
 * a specific application's needs.
 *
 * @see RequestListener
 *        The callback interface.
 *
 */
public class AsyncLocationRunner {

    MyLocation lc;

    public AsyncLocationRunner(MyLocation lc) {
        this.lc = lc;
    }

    /**
     * Invalidate the current user session by removing the access token in
     * memory, clearing the browser cookies, and calling auth.expireSession
     * through the API. The application will be notified when logout is
     * complete via the callback interface.
     *
     * Note that this method is asynchronous and the callback will be invoked
     * in a background thread; operations that affect the UI will need to be
     * posted to the UI thread or an appropriate handler.
     *
     * @param context
     *            The Android context in which the logout should be called: it
     *            should be the same context in which the login occurred in
     *            order to clear any stored cookies
     * @param listener
     *            Callback interface to notify the application when the request
     *            has completed.
     * @param state
     *            An arbitrary object used to identify the request when it
     *            returns to the callback. This has no effect on the request
     *            itself.
     */
    public void logout(final Context context,
                       final RequestLocationListener listener,
                       final Object state) {
        new Thread() {
            @Override public void run() {
                
            }
        }.start();
    }

    public void logout(final Context context, final RequestLocationListener listener) {
        logout(context, listener, /* state */ null);
    }

    /**
     * Note that this method is asynchronous and the callback will be invoked
     * in a background thread; operations that affect the UI will need to be
     * posted to the UI thread or an appropriate handler.
     *
     * Example:
     * <code>
     *  Bundle parameters = new Bundle();
     *  parameters.putString("method", "auth.expireSession", new Listener());
     *  String response = request(parameters);
     * </code>
     *
     * @param parameters
     *            Key-value pairs of parameters to the request. Refer to the
     *            documentation: one of the parameters must be "method".
     * @param listener
     *            Callback interface to notify the application when the request
     *            has completed.
     * @param state
     *            An arbitrary object used to identify the request when it
     *            returns to the callback. This has no effect on the request
     *            itself.
     */
    public void request(Bundle parameters,
    					RequestLocationListener listener,
                        final Object state) {
        request(null, parameters, "GET", listener, state);
    }

    public void request(Bundle parameters, RequestLocationListener listener) {
        request(null, parameters, "GET", listener, /* state */ null);
    }

    /**
     *
     * Note that this method is asynchronous and the callback will be invoked
     * in a background thread; operations that affect the UI will need to be
     * posted to the UI thread or an appropriate handler.
     *
     * @param graphPath
     * @param listener
     *            Callback interface to notify the application when the request
     *            has completed.
     * @param state
     *            An arbitrary object used to identify the request when it
     *            returns to the callback. This has no effect on the request
     *            itself.
     */
    public void request(String graphPath,
    					RequestLocationListener listener,
                        final Object state) {
        request(graphPath, new Bundle(), "GET", listener, state);
    }

    public void request(String graphPath, RequestLocationListener listener) {
        request(graphPath, new Bundle(), "GET", listener, /* state */ null);
    }

    /**
     *
     * Note that this method is asynchronous and the callback will be invoked
     * in a background thread; operations that affect the UI will need to be
     * posted to the UI thread or an appropriate handler.
     *
     * @param graphPath
     * @param parameters
     * @param listener
     *            Callback interface to notify the application when the request
     *            has completed.
     * @param state
     *            An arbitrary object used to identify the request when it
     *            returns to the callback. This has no effect on the request
     *            itself.
     */
    public void request(String graphPath,
                        Bundle parameters,
                        RequestLocationListener listener,
                        final Object state) {
        request(graphPath, parameters, "GET", listener, state);
    }

    public void request(String graphPath,
                        Bundle parameters,
                        RequestLocationListener listener) {
        request(graphPath, parameters, "GET", listener, /* state */ null);
    }

    /**
     *
     * Note that this method is asynchronous and the callback will be invoked
     * in a background thread; operations that affect the UI will need to be
     * posted to the UI thread or an appropriate handler.
     *
     * @param graphPath
     * @param parameters
     * @param httpMethod
     *            http verb, e.g. "POST", "DELETE"
     * @param listener
     *            Callback interface to notify the application when the request
     *            has completed.
     * @param state
     *            An arbitrary object used to identify the request when it
     *            returns to the callback. This has no effect on the request
     *            itself.
     */
    public void request(final String graphPath,
                        final Bundle parameters,
                        final String httpMethod,
                        final RequestLocationListener listener,
                        final Object state) {
        new Thread() {
            @Override public void run() {
                try {
                    String resp = lc.request(graphPath, parameters, httpMethod);
                    listener.onComplete(resp, state);
                } catch (FileNotFoundException e) {
                    listener.onFileNotFoundException(e, state);
                } catch (MalformedURLException e) {
                    listener.onMalformedURLException(e, state);
                } catch (IOException e) {
                    listener.onIOException(e, state);
                }
            }
        }.start();
    }

    /**
     * Callback interface for API requests.
     *
     * Each method includes a 'state' parameter that identifies the calling
     * request. It will be set to the value passed when originally calling the
     * request method, or null if none was passed.
     */
    public static interface RequestLocationListener {

        /**
         * Called when a request completes with the given response.
         *
         * Executed by a background thread: do not update the UI in this method.
         */
        public void onComplete(String response, Object state);

        /**
         * Called when a request has a network or request error.
         *
         * Executed by a background thread: do not update the UI in this method.
         */
        public void onIOException(IOException e, Object state);

        /**
         * Called when a request fails because the requested resource is
         * invalid or does not exist.
         *
         * Executed by a background thread: do not update the UI in this method.
         */
        public void onFileNotFoundException(FileNotFoundException e,
                                            Object state);

        /**
         * Called if an invalid graph path is provided (which may result in a
         * malformed URL).
         *
         * Executed by a background thread: do not update the UI in this method.
         */
        public void onMalformedURLException(MalformedURLException e,
                                            Object state);

        /**
         * Called when the server-side Facebook method fails.
         *
         * Executed by a background thread: do not update the UI in this method.
         */
        public void onMyLocationError(MyLocationError e, Object state);

    }

}
