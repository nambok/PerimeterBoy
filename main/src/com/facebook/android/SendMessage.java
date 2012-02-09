package com.facebook.android;

import android.app.Activity;
import android.os.Bundle;

public class SendMessage extends Activity {

    private String UserId;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        
        Bundle extras = getIntent().getExtras();
        UserId = extras.getString("ID");
    }
}
