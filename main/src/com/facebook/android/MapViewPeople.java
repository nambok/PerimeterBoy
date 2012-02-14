package com.facebook.android;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapViewPeople extends MapActivity{

    protected static JSONArray jsonArray;
    private Handler mHandler;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		mHandler = new Handler();
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    
	    Bundle extras = getIntent().getExtras();
        String apiResponse = extras.getString("API_RESPONSE");
        
        try {
            jsonArray = new JSONObject(apiResponse).getJSONArray("data");
           
       } catch (JSONException e) {
           showToast("Error: " + e.getMessage());
           return;
       }
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    
	    //showToast(apiResponse);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable positionMarker = this.getResources().getDrawable(R.drawable.icon);
	    PeopleItemizedOverlay itemizedoverlay = new PeopleItemizedOverlay(positionMarker, mapView);
	    
	    int lat;
	    int lng;
	    String name;
	    String location;
	    
	    for(int i = 0; i<jsonArray.length(); i++)
	    {	    	
	    	try {
                lat = (int)(jsonArray.getJSONObject(i).getDouble("lat") * 1E6);
            } catch (JSONException e) {
                continue;
            }
	    	try {
                lng = (int)(jsonArray.getJSONObject(i).getDouble("lng") * 1E6);
            } catch (JSONException e) {
                continue;
            }
	    	try {
                name = jsonArray.getJSONObject(i).getString("name");
            } catch (JSONException e) {
                continue;
            }
	    	try {
                location = jsonArray.getJSONObject(i).getString("distance");
            } catch (JSONException e) {
                continue;
            }
	    	
	    	GeoPoint point = new GeoPoint(lat,lng);
		    OverlayItem overlayitem = new OverlayItem(point, name, location + " miles");
		    
		    itemizedoverlay.addOverlay(overlayitem);
		    mapOverlays.add(itemizedoverlay);
	    }
	    
	    lat = (int)(Utility.lat * 1E6);
	    lng = (int)(Utility.lng * 1E6);
	    GeoPoint pointFinal = new GeoPoint(lat, lng);
	    
	    //map view options
	    mapView.setSatellite(false);

        MapController mapController = mapView.getController();
        mapController.setCenter(pointFinal);
        mapController.setZoom(18);
        mapController.animateTo(pointFinal);

        mapView.setBuiltInZoomControls(true);
        mapView.displayZoomControls(true);
        
	}
	
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void showToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MapViewPeople.this, msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
