package com.temboo.example;

import org.json.simple.JSONValue;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.temboo.Library.Foursquare.Checkins.CreateCheckin;
import com.temboo.Library.Foursquare.Checkins.CreateCheckin.CreateCheckinInputSet;
import com.temboo.Library.Foursquare.Venues.SearchVenues;
import com.temboo.Library.Foursquare.Venues.SearchVenues.SearchVenuesInputSet;
import com.temboo.Library.Foursquare.Venues.SearchVenues.SearchVenuesResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;
import com.temboo.matthew__167.MyTemboo;

/**
Copyright 2012, Temboo Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This simple Android app demonstrates how to get started building mobile applications with the Temboo SDK. 
The demo illustrates how to handle the Foursquare Oauth process, and how Foursquare venue lookups and checkins
can be streamlined with the Temboo toolkit.

To run the demo, you'll need a Temboo account, a Foursquare account, and the Oauth client ID, and client secret 
that are provided when you register an app at from registering the app at https://foursquare.com/oauth/

IMPORTANT NOTE: Update the constants in FoursquareConnectedActivity with your Temboo credentials, and your
Foursquare client ID and client secret, before running this demo!

@author matthewflaming
 */


/**
 * The FoursquareConnectedActivity is launched after the user of the app has successfully authorized
 * access to their account (via the Oauth process). It contains a simple UI, allowing the user to 
 * perform a Foursquare venue lookup based on their current location, and to do Foursquare checkins.
 */
public class FoursquareConnectedActivity extends Activity {

	/**********************************************************************************************
	 * UPDATE THE VALUES OF THESE CONSTANTS WITH YOUR OWN CREDENTIALS
	 *********************************************************************************************/

	
	// Use these constants to define the Foursquare client ID and secret, which are available when you register
	// a new app at at https://foursquare.com/oauth
	// (Replace with your own Foursquare client ID and secret)
	protected static final String FOURSQUARE_CLIENT_ID = "YOUR FOURSQUARE CLIENT ID";
	protected static final String FOURQUARE_CLIENT_SECRET = "YOUR FOURSQUARE CLIENT SECRET";

	// Callback URI define for this application: this needs to be specified when registering the 
	// app as a consumer at Foursquare, and also in AndroidManifest.xml. 
	// MAKE SURE TO SUPPLY THE EXACT URL BELOW AS THE CALLBACK WHEN REGISTERING YOUR APP AT FOURSQUARE.
	// If you want to use a different callback URL, you will need to edit AndroidManifest.xml accordingly.
	protected static final String FOURSQUARE_CALLBACK_URL = "foursquareautomator-androidapp://register";

	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	protected final static String TEMBOO_APPKEY_NAME = "YOUR TEMBOO APP KEY NAME";
	protected final static String TEMBOO_APPKEY = "YOUR TEMBOO APP KEY";

	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/

	
	// The Oauth token that we'll retrieve from Foursquare; this will be populated
	// by the FoursquareOauthActivity
	protected static String FOURSQUARE_OAUTH_TOKEN = null;

	// The Temboo session object
	private TembooSession session;

	// A reference to the TextView used to display the current location
	private TextView currentVenueTextView;
	
	// The current geographic location, as reported by the phone's GPS
	public Location currentLocation;
	
	// Information about the current Foursquare venue.
	private String currentVenueID = null;
	private String currentVenueName = null;
	
	// Where we last checked into Foursquare; used to prevent duplicate checkins
	private String lastCheckinVenueID = null;
	

	/**
	 * onCreate is called by Android when the activity is first created.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
        // Initialize the UI with the "connected mode" layout (defined in /res/layout/foursquare_connected.xml)
        setContentView(R.layout.foursquare_connected);

        // Obtain a reference to the "current venue" textview
        currentVenueTextView = (TextView) findViewById(R.id.foursquareVenueField);
                
        // Initiate the Temboo session
        try {
        	session = new MyTemboo(TEMBOO_APPKEY_NAME, TEMBOO_APPKEY);
        } catch(Exception e) {
        	currentVenueTextView.setText("Uh-oh! Something has gone horribly wrong.");
        	Log.e("TEMBOO", "Error starting Temboo session.", e);
        }
        
        // Debug: display the Fourquare Oauth token retrieved by FoursquareOauthActivity
        Toast.makeText(FoursquareConnectedActivity.this, "Successfully connected to Foursquare. Oauth token: " 
        		+ FOURSQUARE_OAUTH_TOKEN, Toast.LENGTH_SHORT).show();
        
        // Obtain a reference to the Android LocationManager, which is (surprisingly) responsible for managing GPS/location data
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        // Get and store the last known location
		currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Register a listener with the Location Manager to receive location updates. Currently, this is configured
		// to request GPS updates every 3 minutes, with a minimum location-differential of 3 meters per update. 
		// See http://developer.android.com/reference/android/location/LocationManager.html
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000, 3, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {				
			}
			
			@Override
			public void onProviderEnabled(String provider) {				
			}
			
			@Override
			public void onProviderDisabled(String provider) {				
			}
			
			// When the location changes, store the current location in the parent activity
			@Override
			public void onLocationChanged(Location location) {
				currentLocation = location;
			}
		});
  
      
        // Attach the "lookup location" button click handler
        Button lookupButton = (Button) findViewById(R.id.getVenue);
        lookupButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					getFoursquareVenueForCurrentLocation();
				} catch(Exception e) {
					Log.e("TEMBOO", "Error performing Foursquare venue lookup", e);
                    Toast.makeText(FoursquareConnectedActivity.this, "Error performing foursquare venue lookup! " 
                    		+ e.getMessage(), Toast.LENGTH_SHORT).show();
				}		
			}
		});
		
		// Attach the "Foursquare checkin" button click handler
		Button checkinButton = (Button) findViewById(R.id.doFoursquareCheckin);
		checkinButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					doFoursquareCheckin();
				} catch(Exception e) {
					Log.e("TEMBOO", "Error performing Foursquare checkin", e);
                    Toast.makeText(FoursquareConnectedActivity.this, "Error performing foursquare checkin! " 
                    		+ e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});        
    }


		
		
	/**
	 * Get the nearest Foursquare venue ID for the current location
	 * @throws TembooException
	 */
	public void getFoursquareVenueForCurrentLocation() throws Exception {

		if(currentLocation == null) {
			Toast.makeText(FoursquareConnectedActivity.this, "Waiting for location", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Show spinner
    	ProgressBar b = (ProgressBar) findViewById(R.id.spinner);
    	b.setVisibility(View.VISIBLE);
    	
    	// Begin a new thread that will actually perform the Foursquare venue query; after completing the query, the thread
    	// will call the gotVenue handler below, to return results to the UI. We do this so that the Foursquare lookup
    	// doesn't block the UI.
		Thread t = new Thread(new FoursquareVenueGetter(session, currentLocation));
		t.start();
	}
	
    
	/**
	 * Perform a Foursquare checkin for the current venue
	 * @throws TembooException
	 */
	private void doFoursquareCheckin() throws TembooException {
		
		// If the current venue isn't set, display a message and return
		if(currentVenueID == null) {
			Toast.makeText(FoursquareConnectedActivity.this, "Please look up the current venue first.", Toast.LENGTH_SHORT).show();
			return;
		}

		// Prevent duplicate checkins
		if(lastCheckinVenueID != null && lastCheckinVenueID.equals(currentVenueID)) {
                Toast.makeText(FoursquareConnectedActivity.this, "You've already checked in at "  
                		+ currentVenueName, Toast.LENGTH_SHORT).show();
                return;
		}
		
		// Show spinner
    	ProgressBar b = (ProgressBar) findViewById(R.id.spinner);
    	b.setVisibility(View.VISIBLE);
    	
    	// Start a new thread that will actually perform the Foursquare checkin; after completing the checkin, the thread
    	// will call the finishedCheckin handler below, to return results to the UI. We do this so that the Foursquare checkin
    	// doesn't block the UI.
		Thread t = new Thread(new FoursquareCheckinTask(session, currentVenueID));
		t.start();
	}  	
	
	
	/**
	 * Handler to accept venue lookup results; this is called by the venue-lookup thread upon completion
	 */
	private Handler gotVenue = new Handler() {
	    public void handleMessage (Message msg) {
	    	// Hide spinner
	    	ProgressBar b = (ProgressBar) findViewById(R.id.spinner);
	    	b.setVisibility(View.GONE);
	    	
	    	// Extract message data
	    	Bundle data = msg.getData();
			currentVenueName = data.getString("name");
			currentVenueID = data.getString("id");
			
			// Update the currentVenueTextView to display the lookup result
			if(currentVenueID != null)
				currentVenueTextView.setText("It looks like maybe you're at " + currentVenueName + "?");
			else
	    		currentVenueTextView.setText("Couldn't find Foursquare venue! Maybe you're nowhere?");
	    }
	};
	
	
	/**
	 * Handler to accept successful checkins; this is called by the checkin thread upon completion
	 */
	private Handler finishedCheckin = new Handler() {
	    public void handleMessage (Message msg) {
	    	// Hide spinner
	    	ProgressBar b = (ProgressBar) findViewById(R.id.spinner);
	    	b.setVisibility(View.GONE);
	    	
	    	// Update the currentVenueTextView to display the checkin result
	    	currentVenueTextView.setText("Successfully checked in at " + currentVenueName);
	    	
	    	// Store the last checkin venue ID, to prevent duplicate checkins
	    	lastCheckinVenueID = currentVenueID;
	    }		
	};
	
	
	/**
	 * Handler to respond to exceptions that occur in other threads; this is called by the checkin or venue lookup
	 * threads, if an error occurs
	 */
	private Handler gotError = new Handler() {
	    public void handleMessage (Message msg) {
	    	// Hide spinner
	    	ProgressBar b = (ProgressBar) findViewById(R.id.spinner);
	    	b.setVisibility(View.GONE);
	    	

	    	// Extract message data
	    	Bundle data = msg.getData();
			String errMsg = data.getString("error");
			
			// Update the currentVenueTextView to display the error 
			currentVenueTextView.setText("Uh-oh, an error occurred! " + errMsg);
	    }		
	};
	
	
	
	/**
	 * Runnable to get Foursquare venue (via a Temboo choreo), based on current location
	 */
	private class FoursquareVenueGetter implements Runnable {
		
		private TembooSession session;
		private Location location;
		
		
		public FoursquareVenueGetter(TembooSession session, Location currentLocation) {
			this.session = session;
			this.location = currentLocation;
		}
		
		public void run() {
			
			String venueID = null;
			String venueName = null;
			
			// Query for venue, by running the Temboo SearchVenues choreo; we instantiate the choreo with the 
			// passed-in Temboo session
			SearchVenues venueSearch = new com.temboo.Library.Foursquare.Venues.SearchVenues(session);
			SearchVenuesInputSet inputs = venueSearch.newInputSet();
			
			// Populate the inputs for the SearchVenues choreo; the choreo needs the Foursquare Oauth token,
			// and the current latitude/longitude
			inputs.set_OauthToken(FOURSQUARE_OAUTH_TOKEN);
			inputs.set_Latitude(String.valueOf(location.getLatitude()));
			inputs.set_Longitude(String.valueOf(location.getLongitude()));
			inputs.set_Limit("20");	// Retrieve the 20 closest venues

			try {
				
				// Execute the choreo
				SearchVenuesResultSet results = venueSearch.execute(inputs);

				org.json.simple.JSONObject venueData = null;
				
				// Parse the choreo result as a JSON object
				Object raw = JSONValue.parse(results.get_Response());
				venueData = (org.json.simple.JSONObject) raw;
			
				// Get the response code
				org.json.simple.JSONObject metaData = (org.json.simple.JSONObject) venueData.get("meta");
				long responseCode = (Long) metaData.get("code");
			 
				// If we got a successful response (code 200), extract the set of results from the response; 
				// we'll loop over the results to calculate which venue is closest to the current location
				if(responseCode == 200) {
					org.json.simple.JSONObject response = (org.json.simple.JSONObject) venueData.get("response");
					Object venues = response.get("venues");
					
					if(venues != null && venues instanceof org.json.simple.JSONArray) {
						org.json.simple.JSONArray array = (org.json.simple.JSONArray) venues;
						long distance = Long.MAX_VALUE;
					
						// Find the closest venue of the returned results
						for(int i=0; i < array.size(); i++) {
							org.json.simple.JSONObject venue = (org.json.simple.JSONObject) array.get(i);
						
							long thisDistance = (Long) ((org.json.simple.JSONObject) venue.get("location")).get("distance");
							if(thisDistance < distance) {
								distance = thisDistance;
								venueID = (String) venue.get("id");
								venueName = (String) venue.get("name");
							}	
						}
					}
				}
				
				// Construct the message object, which will be sent back to the main UI thread via the gotVenue handler
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("name", venueName);
				b.putString("id", venueID);
				msg.setData(b);
				gotVenue.sendMessage(msg);
			} catch(Exception e) {
				// Send the error back to the main UI thread, via the gotError handler
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("error", e.getMessage());
				gotError.sendMessage(msg);
			}
		}
	}
	
	
	/**
	 * Runnable to perform Foursquare checkin (via a Temboo choreo) based on current venue
	 */
	private class FoursquareCheckinTask implements Runnable {

		private TembooSession session;
		private String venueID;
		
		public FoursquareCheckinTask(TembooSession session, String venueID) {
			this.session = session;
			this.venueID = venueID;
		}
		
		@Override
		public void run() {
			try {
				// Instantiate the Temboo CreateCheckin choreo, which will be used to perform the checkin operation,
				// via the passed-in Temboo session
				CreateCheckin checkin = new com.temboo.Library.Foursquare.Checkins.CreateCheckin(session);
				
				// Configure inputs for the choreo; the choreo needs the Foursquare Oauth token, and the venue ID 
				CreateCheckinInputSet checkinInputs = checkin.newInputSet();
				checkinInputs.set_OauthToken(FOURSQUARE_OAUTH_TOKEN);
				checkinInputs.set_VenueID(venueID);
				
				// Run the choreo, to perform the checkin
				checkin.execute(checkinInputs);	
				
				// Call the finishedCheckin handler to inform the main thread that the checkin is complete
				// (we just send it an empty message, since there's no data we need to pass)
				finishedCheckin.sendEmptyMessage(0);
			} catch(Exception e) {
				// Send the error back to the main UI thread, via the gotError handler
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("error", e.getMessage());
				gotError.sendMessage(msg);			}
		}
	}
	
}



