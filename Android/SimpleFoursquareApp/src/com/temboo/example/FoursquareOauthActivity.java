package com.temboo.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
 * The FoursquareOauthActivity is responsible for performing an Oauth handshake with Foursquare, to retrieve an 
 * Oauth token that will allow this app to connect with a specific user's Foursquare account. The basic steps
 * involved in this process are:
 * 
 * 1) The FoursquareOauthActivity creates a webview (an embedded browser) and points it to a dynamically
 * constructed URL at Foursquare. The URL contains the Oauth client ID and callback URL registered at 
 * https://foursquare.com/oauth/
 * 
 * 2) The embedded browser will display a page to the user, asking them to login to Foursquare and allow the 
 * app to access their account. 
 *
 */
public class FoursquareOauthActivity extends Activity {
 

	/**
	 * onCreate is called by Android when the activity is first created.
	 */        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the UI with the "launchscreen" layout (defined in /res/layout/oauth_webview.xml).
        // This layout simply consists of an unstyled webview.
        setContentView(R.layout.oauth_webview);
        
        // Define the URL that we will request via the webview. This URL is a call to Foursquare, including
        // the app client ID and callback URL, to which the user will be redirected after approving access
        // to their account. 
        String url =
            "https://foursquare.com/oauth2/authenticate" + 
                "?client_id=" + FoursquareConnectedActivity.FOURSQUARE_CLIENT_ID + 
                "&response_type=token" + 
                "&redirect_uri=" + FoursquareConnectedActivity.FOURSQUARE_CALLBACK_URL;
 
        // Find the webview, and make sure Javascript is enabled.
        WebView webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
        	
        	
        	// Here we override the onPageStarted method of the webview. If Foursquare authorization
        	// succeeds, we'll be redirected to a URL that looks like
        	// http://YOUR_REGISTERED_REDIRECT_URI/#access_token=ACCESS_TOKEN
            //
            // We check to see if the URL that is being opened in the webview includes an access token
        	// and, if it does, extract the token.
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "#access_token=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                	
                	// Great -- we found an access token! Extract the token, and store it 
                	// for future use.
                    String accessToken = url.substring(start + fragment.length(), url.length());                                             
                    FoursquareConnectedActivity.FOURSQUARE_OAUTH_TOKEN = accessToken;
                    
                    // Now, start the FoursquareConnectedActivity
                    Intent i = new Intent(getBaseContext(), FoursquareConnectedActivity.class);
                    startActivity(i);
                }
            }
        });
        webview.loadUrl(url);
    }

}
