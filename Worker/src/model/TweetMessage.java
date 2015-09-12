package model;


public class TweetMessage {// macthes to messages in SQS

    /*public TweetMessage(String tweetId, String tweetContent) {
		this.tweetId = tweetId;
		this.tweetContent = tweetContent;
	}*/

	@Override
    public String toString() {
        return "tweetMessage{" +
                "tweetId='" + tweetId + '\'' +
                ", tweetContent='" + tweetContent + '\'' +
                '}';
    }

    private String tweetId;
    private String tweetContent;

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public String getTweetContent() {
        return tweetContent;
    }

    public void setTweetContent(String tweetContent) {
        this.tweetContent = tweetContent;
    }

}
