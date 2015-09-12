package util;

/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * This sample demonstrates how to make basic requests to Amazon SQS using the
 * AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web
 * Services developer account, and be signed up to use Amazon SQS. For more
 * information on Amazon SQS, see http://aws.amazon.com/sqs.
 * <p>
 * WANRNING:</b> To avoid accidental leakage of your credentials, DO NOT keep
 * the credentials file in your source directory.
 */
public class SQSManager {
	
	private static String  queueName = null;
	private static AmazonSQS sqs = null;
	private static String myQueueUrl = null;
	private static SQSManager m_instance = null;
	
	private SQSManager() throws Exception {
		//create a new queue or return the queue which named queueName
		PropertiesManager properitesManager = PropertiesManager.getInstance();
		queueName = properitesManager.getQueueName();
		createQueue(queueName);
	}
	
	public static SQSManager getInstance() throws Exception {
        if (m_instance == null) {
            m_instance = new SQSManager();
        }
		return m_instance;
	}
	
	private void createQueue(String name) throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * ().
		 */
		AWSCredentials credentials = null;
        try {
    		PropertiesManager properitesManager = PropertiesManager.getInstance();
        	credentials = new BasicAWSCredentials(properitesManager.getAccessKey(), properitesManager.getSecretKey());
        	sqs = new AmazonSQSClient(credentials);
        	Region apSouthEast = Region.getRegion(Regions.fromName(properitesManager.getRegion()));//Region in Singapore
        	sqs.setRegion(apSouthEast);
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (/Users/daniel/.aws/credentials), and is in valid format.",
                    e);
        }
        
        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");
        
        try {
            // Create a queue
            System.out.println("Creating a new SQS queue called " + name + ".\n");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(name);
            myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
            

            // List queues
            System.out.println("Listing all queues in your account.\n");
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println("Create queue successfully");
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            throw ace;
        }
	}
	
	public void sendMessage(String messageBody) {
		try {
        // Send a message
	        System.out.println("HTTP Sending "+messageBody+" a message to MyQueue"+myQueueUrl+".\n");
	        sqs.sendMessage(new SendMessageRequest(myQueueUrl, messageBody));
	        System.out.println(myQueueUrl);
		} catch (Exception e) {
			m_instance = null;
			e.printStackTrace();
			throw e;
		}
	}

	public List<Message> receiveMessage() {
		List<Message> messages = null;
		try {
            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
//            receiveMessageRequest.setMaxNumberOfMessages(1);
//            receiveMessageRequest.setWaitTimeSeconds(20);
            messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            for (Message message : messages) {
                System.out.println("  Message");
                System.out.println("    MessageId:     " + message.getMessageId());
                System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                System.out.println("    Body:          " + message.getBody());
                for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                    System.out.println("  Attribute");
                    System.out.println("    Name:  " + entry.getKey());
                    System.out.println("    Value: " + entry.getValue());
                }
            }
            System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return messages;
	}
	
	public void deleteMessage(List<Message> messages) {
		
		try {
			// Delete a message
            System.out.println("Deleting a message.\n");
            String messageRecieptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		SQSManager ob = SQSManager.getInstance();
		ob.sendMessage("Hello");
		ob.receiveMessage();
	}
	
}