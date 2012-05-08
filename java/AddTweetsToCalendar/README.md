
###USING THE TEMBOO SDK: ADD TWEETS TO GOOGLE CALENDAR (Java)

This example is a working Java application that uses the Temboo SDK to retrieve a list of your most recent Tweets from Twitter,
and create events in your Google Calendar that correspond with the dates and times that you tweeted. This application:

 * Retrieves the most recent tweets from your Twitter feed
 * Searches a list calendars for the calendar name you specify and returns a calendar id
 * Iterates over the XML from twitter, and extracts information about the tweets
 * Creates a Google Calendar event that corresponds to the date and time that the tweet was created.

###TO RUN THIS EXAMPLE:

 * Sign up for a free Temboo account (if you don't already have one) and Download the Temboo Java SDK
at https://www.temboo.com/download. Add the SDK as a library to your Java project. You can find instructions
for this process on the Temboo site, under "getting started" (https://www.temboo.com/public/support/getting-started).

 * Create a Twitter account (if you don't already have one) and register your app at https://dev.twitter.com/ to get the Twitter
oAuth credentials, that you'll need to configure the example. 

 * Create a Google account (if you don't already have one). If you don't have the oAuth 2.0 credentials associated with your Google account, 
login to your Google account, create a project and generate your oAuth 2.0 ClientID and ClientSecret here https://code.google.com/apis/console/. 
After doing that, use Google's oAuth playground to generate your AccessToken and RefreshToken here: https://code.google.com/oauthplayground/

 * Edit the Java code to contain your Temboo, Twitter, and Google Calendar credentials. 

 * Run it!

###ABOUT TEMBOO

The Temboo SDK Library allows you to implement complex interactions with 3rd party services 
without worrying about the specific syntax of a 3rd-party API, by providing simple, 
native-language functions that trigger Temboo choreos. Temboo choreos are reusable
code snippets that can do almost anything, from updating your status on Facebook, to creating
a new Amazon RDS DB instance, to checking the weather in your neighborhood. 