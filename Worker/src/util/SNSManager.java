package util;

import java.io.IOException;

import model.SNSMessage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

public class SNSManager {

	private static SNSManager m_instance = null;
	private static AmazonSNSClient snsClient = null;
	private static String topicName = null;
	private static String subscribeUrl = null;
//	private static final String subscribeUrl = "http://hs2807-assignment1.elasticbeanstalk.com/sns";
//	private static final String prefixArn = "arn:aws:sns:ap-southeast-1:627609625139:";
	private static String topicArn = null;

	public static SNSManager getInstance() throws IOException {
		if (m_instance == null) {
			m_instance = new SNSManager();
		}
		return m_instance;
	}
	private SNSManager() throws IOException {
		// create a new SNS client and set endpoint
		AWSCredentials credentials;
		PropertiesManager properitesManager = PropertiesManager.getInstance();
    	credentials = new BasicAWSCredentials(properitesManager.getAccessKey(), properitesManager.getSecretKey());
    	
    	topicName = properitesManager.getTopicName();
    	subscribeUrl = properitesManager.getSubscribeUrl();
    	
		snsClient = new AmazonSNSClient(credentials);
		Region apSouthEast = Region.getRegion(Regions.fromName(properitesManager.getRegion()));// Region	in Singapore
		snsClient.setRegion(apSouthEast);
		createATopic(topicName);
		subscribeToATopic(topicName, subscribeUrl);
	}

	// create a new SNS topic
	public void createATopic(String topic) {
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topic);
		CreateTopicResult createTopicResult = snsClient
				.createTopic(createTopicRequest);
		topicArn = createTopicResult.getTopicArn();
		// print TopicArn
		System.out.println(createTopicResult.getTopicArn());
		// get request id for CreateTopicRequest from SNS metadata
		System.out.println("CreateTopicRequest - "
				+ snsClient.getCachedResponseMetadata(createTopicRequest));
	}

	// subscribe to an SNS topic
	public void subscribeToATopic(String topic, String url) {
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "http",
				url);
		snsClient.subscribe(subRequest);
		// get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - "
				+ snsClient.getCachedResponseMetadata(subRequest));
		System.out.println("Check your http endpoint and confirm subscription.");
	}

	// publish to an SNS topic
	public void publishToATopic(String msg) {
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

	// delete an SNS topic
	public void deleteATopic() {
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
		// get request id for DeleteTopicRequest from SNS metadata
		System.out.println("DeleteTopicRequest - "
				+ snsClient.getCachedResponseMetadata(deleteTopicRequest));
	}
	
	public void confirmTopicSubmission(SNSMessage message) {
		ConfirmSubscriptionRequest confirmSubscriptionRequest = new ConfirmSubscriptionRequest()
		 							.withTopicArn(message.getTopicArn())
									.withToken(message.getToken());
		ConfirmSubscriptionResult resutlt = snsClient.confirmSubscription(confirmSubscriptionRequest);
		System.out.println("subscribed to " + resutlt.getSubscriptionArn());
		
	}
}