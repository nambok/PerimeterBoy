package com.facebook.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Places extends Activity implements OnItemClickListener {
    private Handler mHandler;
    private JSONObject location;
    
    protected ListView placesList;
    protected LocationManager lm;

    protected static JSONArray jsonArray;

    protected ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        location = new JSONObject();

        setContentView(R.layout.places_list);
        
        fetchPlaces();
    }

    /*
     * Fetch nearby places by providing the search type as 'place' within 1000
     * mtrs of the provided lat & lon
     */
    private void fetchPlaces() {
        if (!isFinishing()) {
            dialog = ProgressDialog.show(Places.this, "", getString(R.string.nearby_places), true,
                    true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast("No places fetched.");
                        }
                    });
        }
        
        Bundle params = new Bundle();
        Utility.mAsyncLocationRunner.request("location/getLocations/lat/" + Utility.lat + "/lng/" + Utility.lng + "/format/json", params, new placesRequestListener());
    }

    /*
     * Callback after places are fetched.
     */
    public class placesRequestListener extends BaseRequestLocationListener {

        @Override
        public void onComplete(final String response, final Object state) {
            dialog.dismiss();

            try {
                jsonArray = new JSONObject(response).getJSONArray("data");
                if (jsonArray == null) {
                    showToast("Error: nearby places could not be fetched");
                    return;
                }
            } catch (JSONException e) {
                showToast("Error: " + e.getMessage());
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    placesList = (ListView) findViewById(R.id.places_list);
                    placesList.setOnItemClickListener(Places.this);
                    placesList.setAdapter(new PlacesListAdapter(Places.this));
                }
            });

        }

        public void onMyLocationError(MyLocationError error) {
            dialog.dismiss();
            showToast("Fetch Places Error: " + error.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        
        try {
            
            final String name = jsonArray.getJSONObject(position).getString("name");
            final String placeID = jsonArray.getJSONObject(position).getString("id");
            
        } catch (JSONException e) {
            showToast("Error: " + e.getMessage());
        }
        
    }

    public class placesCheckInListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response, final Object state) {
            showToast("API Response: " + response);
        }

        public void onFacebookError(FacebookError error) {
            dialog.dismiss();
            showToast("Check-in Error: " + error.getMessage());
        }
    }

    public void showToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(Places.this, msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /**
     * Definition of the list adapter
     */
    public class PlacesListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        Places placesList;

        public PlacesListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(position);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            View hView = convertView;
            if (convertView == null) {
                hView = mInflater.inflate(R.layout.place_item, null);
                ViewHolder holder = new ViewHolder();
                holder.name = (TextView) hView.findViewById(R.id.place_name);
                holder.location = (TextView) hView.findViewById(R.id.place_location);
                hView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) hView.getTag();
            
            try {
                holder.name.setText(jsonObject.getString("name"));
            } catch (JSONException e) {
                holder.name.setText("");
            }
            try {
                String vicinity = jsonObject.getString("vicinity");
                
                holder.location.setText(vicinity);
            } catch (JSONException e) {
                holder.location.setText("");
            }
            return hView;
        }

    }

    class ViewHolder {
        TextView name;
        TextView location;
    }
}
