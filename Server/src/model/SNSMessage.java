package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "MessageAttributes" })
public class SNSMessage {
	private String type;
	private String messageId;
	private String topicArn;
	private String subject;
	private String message;//
	private String timestamp;
	private String signatureVersion;
	private String signature;
	private String signingCertURL;
	private String subscribeURL;
	private String unsubscribeURL;
	private String token;
	
	@Override
	public String toString() {
		return "Message [type=" + type + ", messageId=" + messageId
				+ ", topicArn=" + topicArn + ", subject=" + subject
				+ ", message=" + message + ", timestamp=" + timestamp
				+ ", signatureVersion=" + signatureVersion + ", signature="
				+ signature + ", signingCertURL=" + signingCertURL
				+ ", subscribeURL=" + subscribeURL + ", unsubscribeURL="
				+ unsubscribeURL + ", token=" + token + "]";
	}
	@JsonProperty("SigningCertURL")
	public String getSigningCertURL() {
		return signingCertURL;
	}
	public void setSigningCertURL(String signingCertURL) {
		this.signingCertURL = signingCertURL;
	}
	
	@JsonProperty("Type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty("MessageId")
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	@JsonProperty("TopicArn")
	public String getTopicArn() {
		return topicArn;
	}
	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}
	@JsonProperty("Subject")
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@JsonProperty("Message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@JsonProperty("Timestamp")
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@JsonProperty("SignatureVersion")
	public String getSignatureVersion() {
		return signatureVersion;
	}
	public void setSignatureVersion(String signatureVersion) {
		this.signatureVersion = signatureVersion;
	}
	@JsonProperty("Signature")
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	@JsonProperty("SubscribeURL")
	public String getSubscribeURL() {
		return subscribeURL;
	}
	public void setSubscribeURL(String subscribeURL) {
		this.subscribeURL = subscribeURL;
	}
	@JsonProperty("UnsubscribeURL")
	public String getUnsubscribeURL() {
		return unsubscribeURL;
	}
	public void setUnsubscribeURL(String unsubscribeURL) {
		this.unsubscribeURL = unsubscribeURL;
	}
	@JsonProperty("Token")
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	
}
