package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
	private static PropertiesManager m_instance = null;
	private static Properties properties;
	private String propertiesFileName = "/configuration.properties";
	
	//AWS
	private String secretKey = null;
	private String accessKey = null;

	private String region = null;
	
	//SNS
	private String topicName = null;
	private String subscribeUrl = null;

	public static PropertiesManager getInstance() throws IOException {
		if (m_instance == null) {
			m_instance = new PropertiesManager();
		}
		return m_instance;
	}

	private PropertiesManager() throws IOException {
		properties = new Properties();
//		String path = System.getProperty("user.dir");
		InputStream in = this.getClass().getResourceAsStream(propertiesFileName);
		properties.load(in);
		in.close();
		
		secretKey = properties.getProperty("secretKey");
		accessKey = properties.getProperty("accessKey");
		region = properties.getProperty("region");
		topicName = properties.getProperty("topicName");
		subscribeUrl = properties.getProperty("subscribeUrl");
	}
	public String getSecretKey() {
		return secretKey;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getRegion() {
		return region;
	}

	public String getTopicName() {
		return topicName;
	}

	public String getSubscribeUrl() {
		return subscribeUrl;
	}
	
}
