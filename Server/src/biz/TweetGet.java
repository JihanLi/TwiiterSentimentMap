package biz;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import model.TweetMessage;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.JSONException;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import util.Filter;
import util.JDBC;
import util.PropertiesManager;
import util.SQSManager;

public class TweetGet implements Runnable {
    private static TweetGet m_instance = null;

    private static String OAUTH_CONSUMER_KEY;
    private static String OAUTH_CONSUMER_SECRET;
    private static String OAUTH_CONSUMER_ACCESS_TOKEN;
    private static String OAUTH_CONSUMER_ACCESS_TOKEN_SECRET;
    
    private static ConfigurationBuilder builder = null;

    private TwitterStream twitterStream = null;

    public static String keyWordfilePath = "/keywords.txt";

    //to terminate the running thread
    public void terminate() {
        builder = null;
        twitterStream.cleanUp();
        m_instance = null;
    }

    public static TweetGet getInstance() throws IOException {
        if (m_instance == null) {
            m_instance = new TweetGet();
        }
        return m_instance;
    }

    public TweetGet() throws IOException {
        PropertiesManager propertiesManager = PropertiesManager.getInstance();
        OAUTH_CONSUMER_KEY = propertiesManager.getOAUTH_CONSUMER_KEY();
        OAUTH_CONSUMER_SECRET = propertiesManager.getOAUTH_CONSUMER_SECRET();
        OAUTH_CONSUMER_ACCESS_TOKEN = propertiesManager.getOAUTH_CONSUMER_ACCESS_TOKEN();
        OAUTH_CONSUMER_ACCESS_TOKEN_SECRET = propertiesManager.getOAUTH_CONSUMER_ACCESS_TOKEN_SECRET();
        
        if (builder == null)
            builder = new ConfigurationBuilder();

        builder.setDebugEnabled(true).setOAuthConsumerKey(OAUTH_CONSUMER_KEY).setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET)
                .setOAuthAccessToken(OAUTH_CONSUMER_ACCESS_TOKEN).setOAuthAccessTokenSecret(OAUTH_CONSUMER_ACCESS_TOKEN_SECRET);
        builder.setJSONStoreEnabled(true);
    }

    public void getTweetListen() throws TwitterException, IOException {
        twitterStream = new TwitterStreamFactory(builder.build()).getInstance();
        final Filter obfilter = new Filter(keyWordfilePath);
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                GeoLocation loc = status.getGeoLocation();
                User user = status.getUser();
                Long tweetId = status.getId();
                String text = status.getText();
                Date date = status.getCreatedAt();
                String userName = user.getName();
                if (loc != null) {
                    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());

                    String keyWord = null;
                    try {

                        keyWord = obfilter.getTag(text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("tweetId:" + tweetId + ", " + "; Lati: " + loc.getLatitude() + ", Longt:" + loc.getLongitude() + " ; Text: " + text + " ; UserName:" + userName + " ; Date:" + date + "keyWord:" + keyWord);
                    if (keyWord != null) {
                    	JDBC dao = null;
                    	SQSManager sqsc;
							try {
								dao = JDBC.getInstance();
								dao.insertToTweet(tweetId, userName, loc.getLatitude(), loc.getLongitude(), sqlDate.getTime(), keyWord, text);
								// insert tweetID to Amazon SQS
								sqsc = SQSManager.getInstance();
								//TweetMessage tweetMessage = new TweetMessage(tweetId+"", text);
								sqsc.sendMessage("{\"tweetId\" : \"" + tweetId + "\", \"tweetContent\" : \"" + dao.string2Json(text) + "\"}");
								sqsc.receiveMessage();
							} catch (Exception  e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    }
                }
            }

            @Override

            public void onDeletionNotice
                    (StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }


            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }


            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }


            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }


            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();

        FilterQuery filterquery = new FilterQuery();


        filterquery.track(obfilter.getTages());
        twitterStream.filter(filterquery);
    }

    public static void main(String[] args) throws TwitterException, JSONException, IOException {
        TweetGet ob = getInstance();
        ob.getTweetListen();
    }

    @Override
    public void run() {
        try {
            TweetGet ob = getInstance();
            ob.getTweetListen();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
