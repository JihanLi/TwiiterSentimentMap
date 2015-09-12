package biz;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import model.TweetMessage;
import util.JDBC;
import util.SQSManager;

public class MimicTweetGet implements Runnable {

	private static MimicTweetGet m_instance = null;
	private static boolean stopSignal = true; 
	
	public static MimicTweetGet getInstance() {
		if (m_instance == null) {
			m_instance = new MimicTweetGet();
		}
		return m_instance;
	}

	private MimicTweetGet() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		stopSignal = false;
		// String loc = status.getUser().getLocation();
		double locLatitude = 20.13;
		double locLongitude = 80.13;
		String userName = "aaa";
		Long tweetId = Long.parseLong("584600311257497600");
		String text = "happy love";
		Date date = new Date();

		java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());

		String keyWord = null;

		for (int i = 0; i < 1000; i ++) {
			JDBC dao = null;
			try {
				dao = JDBC.getInstance();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (stopSignal == true) 
				break;
			tweetId++;
			if (text != null) {
				System.out.println("============text=" + text);
				keyWord = "happy";
				
				System.out.println("tweetId:" + tweetId + ", " + "; Lati: "
						+ locLatitude + ", Longt:" + locLongitude + " ; Text: "
						+ text + " ; UserName:" + userName + " ; Date:" + date
						+ "keyWord:" + keyWord);
				if (keyWord != null) {
					try {
						dao.insertToTweet(tweetId, userName, locLatitude, locLongitude,
								sqlDate.getTime(), keyWord, text);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// insert tweetID to Amazon SQS
				SQSManager sqsc;
				try {
					sqsc = SQSManager.getInstance();
					TweetMessage tweetMessage = new TweetMessage(tweetId+"", text);
					sqsc.sendMessage("{\"tweetId\" : \"" + tweetId + "\", \"tweetContent\" : \"" + text + "\"}");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

    //to terminate the running thread
    public void terminate() {
        stopSignal = true;
    }
}
