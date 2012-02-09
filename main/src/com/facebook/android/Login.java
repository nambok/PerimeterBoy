/*
 * Copyright 2004 - Present Facebook, Inc.
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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

public class Login extends Activity implements View.OnClickListener {

	//Buttons
	Button facebookBtn;
	Button googleBtn;
	
    /*
     * Your Facebook Application ID must be set before running this example See
     * http://www.facebook.com/developers/createapp.php
     */
    public static final String APP_ID = "309733309078545";

    private LoginButton mLoginButton;
    private TextView mText;
    private ImageView mUserPic;
    private Handler mHandler;
    ProgressDialog dialog;

    final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;
    
    String[] permissions = { "offline_access" };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
                    + "specified before running this example: see FbAPIs.java");
            return;
        }

        setContentView(R.layout.main);
        mHandler = new Handler();

        mText = (TextView) Login.this.findViewById(R.id.txt);
        mUserPic = (ImageView) Login.this.findViewById(R.id.user_pic);

        // Create the Facebook Object using the app id.
        Utility.mFacebook = new Facebook(APP_ID);
        // Instantiate the asynrunner object for asynchronous api calls.
        Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
        
        
        //instantiate
        Utility.mLocation = new MyLocation();
        Utility.mAsyncLocationRunner = new AsyncLocationRunner(Utility.mLocation);
        
        
        mLoginButton = (LoginButton) findViewById(R.id.login);
        
        //Activity buttons 
        facebookBtn = (Button) findViewById(R.id.btnFacebook);
        facebookBtn.setOnClickListener(this);

        // restore session if one exists
        SessionStore.restore(Utility.mFacebook, this);
        SessionEvents.addAuthListener(new FbAPIsAuthListener());
        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

        /*
         * Source Tag: login_tag
         */
        mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, Utility.mFacebook, permissions);

        if (Utility.mFacebook.isSessionValid()) {
            requestUserData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utility.mFacebook != null) {
            if (!Utility.mFacebook.isSessionValid()) {
                mText.setText("You are logged out! ");
                mUserPic.setImageBitmap(null);
            } else {
                Utility.mFacebook.extendAccessTokenIfNeeded(this, null);
            }
        }
    }
    
    public void onClick(View view) {
    	if (!Utility.mFacebook.isSessionValid()) {
    		Util.showAlert(this,"Warning", "You must first log in.");
    	}
    	else
    	{
    		Intent myIntent = new Intent(getApplicationContext(), MainMenu.class);
    		startActivity(myIntent);
    	}
    }

    /*
     * callback for the apprequests dialog which sends an app request to user's
     * friends.
     */
    public class AppRequestsListener extends BaseDialogListener {
        @Override
        public void onComplete(Bundle values) {
            Toast toast = Toast.makeText(getApplicationContext(), "App request sent",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onFacebookError(FacebookError error) {
            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast toast = Toast.makeText(getApplicationContext(), "App request cancelled",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*
     * Callback for fetching current user's name, picture, uid.
     */
    public class UserRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(response);

                final String picURL = jsonObject.getString("picture");
                final String name = jsonObject.getString("name");
                Utility.userUID = jsonObject.getString("id");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mText.setText("Welcome " + name + "!");
                        mUserPic.setImageBitmap(Utility.getBitmap(picURL));
                    }
                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /*
     * The Callback for notifying the application when authorization succeeds or
     * fails.
     */

    public class FbAPIsAuthListener implements AuthListener {

        @Override
        public void onAuthSucceed() {
            requestUserData();
        }

        @Override
        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }

    /*
     * The Callback for notifying the application when log out starts and
     * finishes.
     */
    public class FbAPIsLogoutListener implements LogoutListener {
        @Override
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }

        @Override
        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
            mUserPic.setImageBitmap(null);
        }
    }

    /*
     * Request user name, and picture to show on the main screen.
     */
    public void requestUserData() {
        mText.setText("Fetching user name, profile pic...");
        Bundle params = new Bundle();
        params.putString("fields", "name, picture");
        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
    }

}
