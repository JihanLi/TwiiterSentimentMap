package unitest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import util.JDBC;
import util.SQSManager;
import biz.MimicTweetGet;

public class mimicTweecGetTest {

	public static void main(String[] args) {
		MimicTweetGet mimicTweetGet = MimicTweetGet.getInstance();
		Thread thread = new Thread(mimicTweetGet);
		System.out.println("thread id is : " + thread.getId());
		thread.start();
	}
	@Test
	public void test() throws Exception {
		JDBC dao = JDBC.getInstance();
		// String loc = status.getUser().getLocation();
		double locLatitude = 20.13;
		double locLongitude = 80.13;
		String userName = "aaa";
		Long tweetId = Long.parseLong("584600311257497600");
		String text = "happy love";
		Date date = new Date();

		java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
		; // your sql date

		String keyWord = null;

		for (int i = 0; i < 1000; i ++) {
			tweetId++;
			if (text != null) {
				System.out.println("============text=" + text);
				keyWord = "happy";
				
				System.out.println("tweetId:" + tweetId + ", " + "; Lati: "
						+ locLatitude + ", Longt:" + locLongitude + " ; Text: "
						+ text + " ; UserName:" + userName + " ; Date:" + date
						+ "keyWord:" + keyWord);
				if (keyWord != null) {
					dao.insertToTweet(tweetId, userName, locLatitude, locLongitude,
							sqlDate.getTime(), keyWord, text);
				}
				// insert tweetID to Amazon SQS
				SQSManager sqsc = SQSManager.getInstance();
				sqsc.sendMessage(tweetId + ":" + text);
			}
		}
	}

}
