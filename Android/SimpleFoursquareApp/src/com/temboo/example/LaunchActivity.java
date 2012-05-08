package com.temboo.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
 * The LaunchActivity is called when you first start this Android application. It simply provides an initial
 * splash screen, and a button to initiate the Foursquare Oauth process.
 */
public class LaunchActivity extends Activity {
	
	/**
	 * onCreate is called by Android when the activity is first created.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the UI with the "launchscreen" layout (defined in /res/layout/launchscreen.xml)
        setContentView(R.layout.launchscreen);
    
        // Attach a click listener to the "connect Foursquare" button; in this case, we simply want
        // the button to start a new activity (FoursquareOauthActivity) which will begin the Oauth
        // authentication process
        Button btn = (Button)findViewById(R.id.connectFoursquareButton);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, FoursquareOauthActivity.class);
                startActivity(intent);
            }
        });
    }
}