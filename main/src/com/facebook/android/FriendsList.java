package com.facebook.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView; 
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsList extends Activity implements OnItemClickListener {
	
    
    private Handler mHandler;

    protected ListView friendsList;
    protected static JSONArray jsonArray;
    
    ProgressDialog dialog;

    /*
     * Layout the friends' list
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        setContentView(R.layout.friends_list);
        
        final Button refreshBtn = (Button) findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh();
			}
		});
        
        refresh();

        //showToast(getString(R.string.can_post_on_wall));
    }
    
    public void refresh()
    {
    	
    	new Thread() {
    		@Override
    		public void run(){
    			Looper.prepare();
    			
    			dialog = ProgressDialog.show(FriendsList.this, "",
                        "Finding People", false, true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                showToast("No People Fetched.");
                            }
                        });
    			
    			Bundle params = new Bundle();
    			Utility.mAsyncLocationRunner.request("location/users/lat/" + Utility.lat + "/lng/" + Utility.lng + "/format/json", params,
    							new PeopleRequestListener());

    			Looper.loop();
    		}
    	}.start();
    	
    }    
    
   public class PeopleRequestListener extends BaseRequestLocationListener {

       @Override
       public void onComplete(final String response, final Object state) {
    	   dialog.dismiss();
    	   mHandler.post(new Runnable() {
               @Override
               public void run() {

            	   buildList(response);
               }
           });           
       }

       public void onMyLocationError(MyLocationError error) {
           Toast.makeText(getApplicationContext(), "Location Error: " + error.getMessage(),
                   Toast.LENGTH_SHORT).show();
       }
   }
    
    public void buildList(String api)
    {
    	 try {
             jsonArray = new JSONObject(api).getJSONArray("data");
            
        } catch (JSONException e) {
            showToast("Error: " + e.getMessage());
            return;
        }
        friendsList = (ListView) findViewById(R.id.friends_list);
        friendsList.setOnItemClickListener(FriendsList.this);
        friendsList.setAdapter(new FriendListAdapter(FriendsList.this));
    }

    
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        try {
            final long friendId;
            
            friendId = jsonArray.getJSONObject(position).getLong("id");
            
            dialog = ProgressDialog.show(FriendsList.this, "", getString(R.string.please_wait, true, true));
    		// TODO Auto-generated method stub
    		Bundle params = new Bundle();
    		Utility.mAsyncLocationRunner.request("user/profile/id/" + String.valueOf(friendId) + "/format/json", params,
    				new ProfileRequestListener());

            
        } catch (JSONException e) {
            showToast("Error: " + e.getMessage());
        }
    }
    
    /*
    *
    */
   public class ProfileRequestListener extends BaseRequestLocationListener {

       @Override
       public void onComplete(final String response, final Object state) {
           dialog.dismiss();
           
           Intent myIntent = new Intent(getApplicationContext(), ViewProfile.class);
           myIntent.putExtra("API_RESPONSE", response);
           startActivity(myIntent);
           
       }

       public void onFacebookError(FacebookError error) {
           dialog.dismiss();
           Toast.makeText(getApplicationContext(), "MyLocation Error: " + error.getMessage(),
                   Toast.LENGTH_SHORT).show();
       }
   }

    public void showToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(FriendsList.this, msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /**
     * Definition of the list adapter
     */
    public class FriendListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        FriendsList friendsList;

        public FriendListAdapter(FriendsList friendsList) {
            this.friendsList = friendsList;
            if (Utility.model == null) {
                Utility.model = new FriendsGetProfilePics();
            }
            Utility.model.setListener(this);
            mInflater = LayoutInflater.from(friendsList.getBaseContext());
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
                hView = mInflater.inflate(R.layout.friend_item, null);
                ViewHolder holder = new ViewHolder();
                holder.profile_pic = (ImageView) hView.findViewById(R.id.profile_pic);
                holder.name = (TextView) hView.findViewById(R.id.name);
                holder.info = (TextView) hView.findViewById(R.id.info);
                hView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) hView.getTag();
            try {
                 holder.profile_pic.setImageBitmap(Utility.model.getImage(
                   jsonObject.getString("id"), jsonObject.getString("pic_small")));
                
            } catch (JSONException e) {
                holder.name.setText("");
            }
            try {
                holder.name.setText(jsonObject.getString("name"));
            } catch (JSONException e) {
                holder.name.setText("");
            }
            
            //set info text
            try {
                holder.info.setText(jsonObject.getString("distance"));
            } catch (JSONException e) {
                holder.info.setText("");
            }
          
            return hView;
        }

    }

    class ViewHolder {
        ImageView profile_pic;
        TextView name;
        TextView info;
    }
}
