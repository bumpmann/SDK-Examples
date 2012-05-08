package AddTweetsToCalendar;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.temboo.Library.Google.Calendar.SearchCalendarsByName;
import com.temboo.Library.Google.Calendar.SearchCalendarsByName.SearchCalendarsByNameInputSet;
import com.temboo.Library.Google.Calendar.SearchCalendarsByName.SearchCalendarsByNameResultSet;
import com.temboo.Library.Twitter.Timelines.UserTimeline;
import com.temboo.Library.Twitter.Timelines.UserTimeline.UserTimelineInputSet;
import com.temboo.Library.Twitter.Timelines.UserTimeline.UserTimelineResultSet;
import com.temboo.Library.Google.Calendar.CreateEvent;
import com.temboo.Library.Google.Calendar.CreateEvent.CreateEventInputSet;
import com.temboo.aaron__167.MyTemboo;
import com.temboo.core.TembooException;

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


This simple Java application demonstrates how to get started building simple social media apps with the Temboo SDK. 
For people that are generating a lot of data through services like Twitter, Facebook, Foursquare, or Fitbit -- 
finding ways to visualize this online activity in one place can be interesting. For this demo, we'll tackle a portion of this idea
by moving Tweets to Google Calendar.

To run the demo, you'll need a Temboo account, and oAuth 1.0 credentials for Twitter and oAuth 2.0 credentials for Google Calendar.

The demo uses Temboo SDK functions to retrieve the most recent tweets from your User Timeline, extracts the status text, created date, and tweet id, 
and creates Google Calendar event using the information from Twitter.

@author aaronjennings
 */

public class AddTweetsToCalendar {

	/**********************************************************************************************
	 * UPDATE THE VALUES OF THESE CONSTANTS WITH YOUR OWN CREDENTIALS AND MESSAGES
	 *********************************************************************************************/

	// Use these constants to define the set of Twitter credentials that will be used to access your Twitter account. 
	// If you don't have these yet, sign up for a dev account and register your app here: https://dev.twitter.com/. You will be given the oauth creds that are needed.
	// (Replace with your own Twitter oauth credentials.)
	private static final String TWITTER_OAUTH_CONSUMER_KEY = "YOUR TWITTER CONSUMER KEY";
	private static final String TWITTER_OAUTH_CONSUMER_SECRET = "YOUR TWITTER CONSUMER SECRET";
	private static final String TWITTER_OAUTH_TOKEN = "YOUR TWITTER OAUTH TOKEN";
	private static final String TWITTER_OAUTH_TOKEN_SECRET = "YOUR TWITTER OAUTH TOKEN SECRET";
	private static final String TWITTER_SCREEN_NAME = "YOUR TWITTER SCREEN NAME";

	// Use this constant to define your Google oAuth 2.0 credentials.
	// If you don't already have the oAuth credentials associated with your Google account, login to your Google account, 
	// create a project and generate your oAuth 2.0 ClientID and ClientSecret here https://code.google.com/apis/console/.
	// After doing that, use Google's oAuth playground to generate your AccessToken and RefreshToken here: https://code.google.com/oauthplayground/.
	private static final String GOOGLE_CLIENT_ID = "YOUR GOOGLE CLIENT ID";
	private static final String GOOGLE_CLIENT_SECRET = "YOUR GOOGLE CLIENT SECRET";
	private static final String GOOGLE_ACCESS_TOKEN = "YOUR GOOGLE ACCESS TOKEN";
	private static final String GOOGLE_REFRESH_TOKEN = "YOUR GOOGLE REFRESH TOKEN";

	// Set your calendar name here. Make sure you provide the name of an existing Google calendar.
	// Note, if there are multiple calendars with the same name, the first one returned will be used.
	private static final String GOOGLE_CALENDAR_NAME = "MyTweetsCalendar";

	// Use these constants to define the set of credentials that will be used to connect with Temboo.
	// (Replace with your own Temboo Application Key.)
	private static final String TEMBOO_APPLICATIONKEY_NAME = "YOUR TEMBOO APP KEY NAME";
	private static final String TEMBOO_APPLICATIONKEY = "YOUR TEMBOO APP KEY";

	/**********************************************************************************************
	 * END CONSTANTS: NOTHING BELOW THIS POINT SHOULD NEED TO BE CHANGED
	 *********************************************************************************************/

	/**
	 * Main method: use the Temboo SDK to retrieve the most recent tweets from your User Timeline, extracts the status text, created date, and tweet id, 
and creates Google Calendar event using the information from Twitter.
	 * 
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {

		// Create a new Temboo session, that will be used to run Temboo SDK choreos.
		// (Replace with your own Temboo Application Key name + key).
		MyTemboo session = new MyTemboo(TEMBOO_APPLICATIONKEY_NAME, TEMBOO_APPLICATIONKEY);

		// Instantiate the Twitter.Timelines.UserTimeline Choreo, using the session object. 
		// See https://live.temboo.com/library/Library/Twitter/Timelines/UserTimeline for detailed documentation
		UserTimeline timeline = new com.temboo.Library.Twitter.Timelines.UserTimeline(session);

		// Get an InputSet object for the UserTimeline, and populate the inputs. This choreo takes inputs that specify
		// a "Count" of tweets to return, some optional parameters for including and excluding certain tweet information, a "SinceId" parameter
		// which indicates the first tweet id to use for the query, a screen name, and your oAuth 1.0 credentials.
		UserTimelineInputSet timelineInput = timeline.newInputSet();

		timelineInput.set_Count("5");
		timelineInput.set_ExcludeReplies("1");
		timelineInput.set_IncludeRetweets("1");
		timelineInput.set_OauthConsumerKey(TWITTER_OAUTH_CONSUMER_KEY);
		timelineInput.set_OauthConsumerSecret(TWITTER_OAUTH_CONSUMER_SECRET);
		timelineInput.set_OauthToken(TWITTER_OAUTH_TOKEN);
		timelineInput.set_OauthTokenSecret(TWITTER_OAUTH_TOKEN_SECRET);
		timelineInput.set_ScreenName(TWITTER_SCREEN_NAME);
		timelineInput.set_TrimUser("1");
		timelineInput.set_SinceId("1");

		// Execute UserTimeline choreo to retieve the most recent tweets from your Twitter timeline.
		UserTimelineResultSet timelineResults = timeline.execute(timelineInput);

		// Print some debugging information to see if this is working.
		System.out.println("Retrieved list of tweets from Twitter!");
		
		// To create a calendar event, we need the calendar id. Execute the SearchCalendarsByName to get this id.
		// Instantiate the Google.Calendar.SearchCalendarsByName Choreo, using the session object
		SearchCalendarsByName searchCals = new com.temboo.Library.Google.Calendar.SearchCalendarsByName(session);

		// Get an InputSet object for the SearchCalendarsByName, and populate the inputs. This choreo takes inputs
		// specifying a calendar name and your oAuth 2.0 credentials.
		SearchCalendarsByNameInputSet searchCalsInput = searchCals.newInputSet();

		searchCalsInput.set_AccessToken(GOOGLE_ACCESS_TOKEN);
		searchCalsInput.set_CalendarName(GOOGLE_CALENDAR_NAME);
		searchCalsInput.set_ClientID(GOOGLE_CLIENT_ID);
		searchCalsInput.set_ClientSecret(GOOGLE_CLIENT_SECRET);
		searchCalsInput.set_RefreshToken(GOOGLE_REFRESH_TOKEN);

		// Execute SearchCalendarsByName and retrieve the results from Google Calendar.
		SearchCalendarsByNameResultSet searchCalsResults = searchCals.execute(searchCalsInput);

		// Print some debugging info with the calendar id that was returned.
		System.out.println("Retrieved calendar id for " + GOOGLE_CALENDAR_NAME + ": " + searchCalsResults.get_CalendarId());

		// Convert the Twitter data into XML
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(
				new InputSource(
						new ByteArrayInputStream(
								timelineResults.get_Response().getBytes("utf-8"))));

		// Extract the set of <status> elements from the result XML.
		NodeList status = doc.getElementsByTagName("status");

		// Loop over the set of <statuses> elements; each item represents
		// a single twitter status.
		for(int i=0; i < status.getLength(); i++) {			

			Element statusEntry = (Element) status.item(i);

			// Extract the <created_at> element for this status.
			NodeList createdDates = statusEntry.getElementsByTagName("created_at");
			Element createdDate = (Element) createdDates.item(0);
			String tweetDate = createdDate.getTextContent();

			// Extract the <text> element for this status (this is the actual tweet).
			NodeList statuses = statusEntry.getElementsByTagName("text");
			Element tweet = (Element) statuses.item(0);
			String tweetText = tweet.getTextContent();

			// Extract the <id> element for this status in case we want to store that.
			// The next time we run this, we may want to pass in this id for the SinceId of Twitter.Users.UserTimeline so that we don't get any duplicate tweets.
			NodeList ids = statusEntry.getElementsByTagName("id");
			Element id = (Element) ids.item(0);
			String tweetId = id.getTextContent();

			// Print out some debugging info to make sure this is working.
			System.out.println("Processing Tweet ID: " + tweetId);

			// The date that was parsed from the Twitter response needs to be formatted differently when it is passed to the Google Calendar CreateEvent Choreo. 
			// Create SimpleDateFormat object with source string date format.
			SimpleDateFormat sdfSource = new SimpleDateFormat("EEE MMM dd HH:mm:ss +0000 yyyy");

			// Parse the string into Date object.
			Date date = sdfSource.parse(tweetDate);

			// Create SimpleDateFormat object with desired date and time format.
			// Note you may want to modify this depending on what timezone you're in.
			SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

			// Parse the date into another format.
			String formattedDate = sdfDestination.format(date);

			// Parse the time into another format.
			String formattedTime = sdfTime.format(date);

			// Print out this date conversion to see if it's working.
			System.out.println("Converted date is : " + formattedDate);
			System.out.println("Converted time is : " + formattedTime);

			// Now we're ready to create the event in the Google Calendar.
			// Instantiate the Google.Calendar.CreateEvent Choreo, using the session object.
			// See https://live.temboo.com/library/Library/Google/Calendar/CreateEvent for detailed documentation.
			CreateEvent createEvent = new com.temboo.Library.Google.Calendar.CreateEvent(session);

			// Get an InputSet object for the CreateEvent, and populate the inputs. This choreo takes inputs
			// specifying calendar id (which we retrieved already), start/end dates and times, event description, event title, and your oAuth 2.0 credentials.
			// Note that we'll just pass in the same date and time for start and end dates because the timeframe doesn't really appy here.
			CreateEventInputSet createEventInput = createEvent.newInputSet();

			createEventInput.set_AccessToken(GOOGLE_ACCESS_TOKEN);
			createEventInput.set_CalendarID(searchCalsResults.get_CalendarId());
			createEventInput.set_ClientID(GOOGLE_CLIENT_ID);
			createEventInput.set_ClientSecret(GOOGLE_CLIENT_SECRET);
			createEventInput.set_EndDate(formattedDate);
			createEventInput.set_EndTime(formattedTime);
			createEventInput.set_EventDescription("My Tweet");
			createEventInput.set_EventTitle(tweetText);
			createEventInput.set_RefreshToken(GOOGLE_REFRESH_TOKEN);
			createEventInput.set_StartDate(formattedDate);
			createEventInput.set_StartTime(formattedTime);

			try {
				// Execute CreateEvent (Note that in this case, we don't care about the results returned by the choreo).
				createEvent.execute(createEventInput);

				System.out.println("Created a Google Calendar event for Tweet Id: " + tweetId + " on " + formattedDate + " at " + formattedTime);
			} catch(TembooException e) {
				System.out.println("Uh-oh! Something went wrong created the event in the Google Calendar. The error from the choreo was: " + e.getMessage());

			}

		}
	}
}