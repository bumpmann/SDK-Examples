###Using the Temboo SDK: Simple Foursquare Example App (Android)

This example is a working Android app that uses the Temboo SDK to perform Foursquare venue lookups and checkins 
based on your current location. (Note that you'll need the Android SDK, and some basic experience creating Android 
apps, to use this example.)

###Quickstart

 * Sign up for a free Temboo account (if you don't already have one) and download the Temboo Java SDK
at https://www.temboo.com/download. Add the SDK as a library to your Java project. You can find instructions
for this process on the Temboo site, under "getting started" (https://www.temboo.com/public/support/getting-started).
 * Sign up for a Foursquare account (if you don't already have one) and register a new Oauth app at https://foursquare.com/oauth 
For the callback URL, use "foursquareautomator-androidapp://register"
 * Update FoursquareConnectedActivity.java with the Oauth client ID and secret from the Foursquare app registration 
page, and with your Temboo Application Key information
 * Run it!

###About Temboo

The Temboo SDK Library allows you to implement complex interactions with 3rd party services 
without worrying about the specific syntax of a 3rd-party API, by providing simple, 
native-language functions that trigger Temboo choreos. Temboo choreos are reusable
code snippets that can do almost anything, from updating your status on Facebook, to creating
a new Amazon RDS DB instance, to checking the weather in your neighborhood. 
