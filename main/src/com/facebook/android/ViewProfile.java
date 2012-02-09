package com.facebook.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewProfile extends Activity implements View.OnClickListener{

	private Handler mHandler;
	protected String jsonObject;
	protected static JSONArray jsonArray;
	
	private String id;
	
	Button btnSendMessage;
	Button btnViewPictures;
	
	//fields
	TextView userTxt;
	TextView locationTxt;
	TextView workTxt;
	TextView educationTxt;
	ImageView profile_pic;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        
        setContentView(R.layout.view_profile);
        
        btnSendMessage = (Button) findViewById(R.id.messageBtn);
        btnSendMessage.setOnClickListener(this);
        
        btnViewPictures =(Button) findViewById(R.id.viewPicturesBtn);
        
        //set layout id
        userTxt = (TextView) findViewById(R.id.userTxt);
        locationTxt = (TextView) findViewById(R.id.locationTxt);
        workTxt = (TextView) findViewById(R.id.workTxt);
        educationTxt = (TextView) findViewById(R.id.educationTxt);
        profile_pic = (ImageView) findViewById(R.id.imageViewMain);
        
        Bundle extras = getIntent().getExtras();
        String apiResponse = extras.getString("API_RESPONSE");
        
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(apiResponse);
            
            userTxt.setText(jsonObject.getString("name"));
            locationTxt.setText(jsonObject.getString("distance"));
            workTxt.setText(jsonObject.getString("work"));
            educationTxt.setText(jsonObject.getString("education"));
            profile_pic.setImageBitmap(Utility.model.getImage(
                    jsonObject.getString("id"), jsonObject.getString("pic")));
            
            id = jsonObject.getString("id");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btnSendMessage)
		{
			if(id != null)
			{
				Intent myIntent = new Intent(getApplicationContext(), SendMessage.class);
				myIntent.putExtra("ID", id);
				startActivity(myIntent);
			}
		}
	}
	
	public void showToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ViewProfile.this, msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
