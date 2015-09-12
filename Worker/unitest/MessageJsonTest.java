package unitest;

import model.TweetMessage;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageJsonTest {

	@Test
	public void test() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TweetMessage msg = mapper.readValue("{\n" +
                    "\t\"tweetId\":\"asdf\",\n" +
                    "\t\"tweetContent\":\"fas;ldkf\"\n" +
                    "}", TweetMessage.class);
            System.out.println(msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
