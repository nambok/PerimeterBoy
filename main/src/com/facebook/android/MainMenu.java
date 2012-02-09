package com.facebook.android;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;

public class MainMenu extends Activity implements View.OnClickListener {
	

	//location manager
    public LocationManager lm;
    public MyLocationListener locationListener;
    
	Button viewPeopleBtn;
	Button viewSettingsBtn;
	Button checkInBtn;
	Button exitBtn;

	ProgressDialog dialog;
	
	int NotificationBroadcast = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);
        
        this.getLocation();
        
        viewPeopleBtn = (Button) findViewById(R.id.findPplBtn);
        viewPeopleBtn.setOnClickListener(this);
        
        viewSettingsBtn = (Button) findViewById(R.id.settingsBtn);
        viewSettingsBtn.setOnClickListener(this);
        
        checkInBtn = (Button) findViewById(R.id.checkInBtn);
        checkInBtn.setOnClickListener(this);
        
        exitBtn = (Button) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		
		if(view == viewPeopleBtn)
		{
			Intent myIntent = new Intent(getApplicationContext(), FriendsList.class);
            startActivity(myIntent);
		}
		else if(view == viewSettingsBtn)
		{
			Intent myIntent = new Intent(getApplicationContext(), SettingsView.class);
            startActivity(myIntent);
		}
		else if(view == checkInBtn)
		{
			Intent myIntent = new Intent(getApplicationContext(), Places.class);
            startActivity(myIntent);			
		}
		else if(view == exitBtn)
		{
			endGPS();
		}
	}
	
	public void endGPS()
	{

		try
		{           
			lm.removeGpsStatusListener(null);
			lm = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		Toast.makeText(MainMenu.this, "GPS stopped",
             Toast.LENGTH_SHORT).show();
		
		String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    	
    	mNotificationManager.cancel(NotificationBroadcast);
		
		super.finish();
	}
    
    public void getLocation() {
    	
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    	int icon = R.drawable.icon;
    	CharSequence tickerText = "MyLocation";
    	long when = System.currentTimeMillis();
    	
    	Notification notification = new Notification(icon, tickerText, when);
    	
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "MyLocation";
    	CharSequence contentText = "Broacasting your location using your Facebook profile";
    	Intent notificationIntent = new Intent(this, MainMenu.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	
    	final int NOTIFICATION_ID = NotificationBroadcast;

    	mNotificationManager.notify(NOTIFICATION_ID, notification);
    	
        /*
         * launch a new Thread to get new location
         */    	
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                if (lm == null) {
                    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                }

                if (locationListener == null) {
                    locationListener = new MyLocationListener();
                }

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                if (provider != null && lm.isProviderEnabled(provider)) {
                	lm.requestLocationUpdates(provider, 1, 0, locationListener,
                            Looper.getMainLooper());
                    
                } else {
                }
            }
        }.start();
    }
    
    class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			Utility.lat = new Double(loc.getLatitude());
			Utility.lng = new Double(loc.getLongitude());
			
			if(Utility.userUID != null)
			{
				// TODO Auto-generated method stub
				Bundle params = new Bundle();
				Utility.mAsyncLocationRunner.request("location/update/id/" + Utility.userUID + "/lat/" + Utility.lat + "/lng/" + Utility.lng + "/format/json", params
						, new BaseRequestLocationListener(){
	
							@Override
							public void onComplete(String response, Object state) {
								// TODO Auto-generated method stub
								
							}
					
				});
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
    }
}
