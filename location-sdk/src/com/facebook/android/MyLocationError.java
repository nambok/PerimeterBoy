/*
 * Copyright 2010 Visual Splash LLC CS.
 *
 */

package com.facebook.android;

/**
 */
public class MyLocationError extends Throwable {

    private static final long serialVersionUID = 1L;

    private int mErrorCode = 0;
    private String mErrorType;

    public MyLocationError(String message) {
        super(message);
    }

    public MyLocationError(String message, String type, int code) {
        super(message);
        mErrorType = type;
        mErrorCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorType() {
        return mErrorType;
    }

}
