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
	
	//SQS
	private String queueName = null;
	private String region = null;
	
	//JDBC
	private String DB_CONNECTION_URL = null;
	private String DB_USER_NAME = null;
	private String DB_PASSWORD = null;
	private String TABLE_NAME = null;

	//Tweet
    private String OAUTH_CONSUMER_KEY;
    private String OAUTH_CONSUMER_SECRET;
    private String OAUTH_CONSUMER_ACCESS_TOKEN;
    private String OAUTH_CONSUMER_ACCESS_TOKEN_SECRET;
    
    private int n_KEYWORDS_NUM;
    
	public static PropertiesManager getInstance() throws IOException {
		if (m_instance == null) {
			m_instance = new PropertiesManager();
		}
		return m_instance;
	}

	private PropertiesManager() throws IOException {
		properties = new Properties();
		InputStream in = this.getClass().getResourceAsStream(propertiesFileName);
		properties.load(in);
		in.close();
		
		secretKey = properties.getProperty("secretKey");
		accessKey = properties.getProperty("accessKey");
		queueName = properties.getProperty("queueName");
		region = properties.getProperty("region");
		DB_CONNECTION_URL = properties.getProperty("DB_CONNECTION_URL");
		DB_USER_NAME = properties.getProperty("DB_USER_NAME");
		DB_PASSWORD = properties.getProperty("DB_PASSWORD");
		TABLE_NAME = properties.getProperty("TABLE_NAME");
		OAUTH_CONSUMER_KEY = properties.getProperty("OAUTH_CONSUMER_KEY");
		OAUTH_CONSUMER_SECRET = properties.getProperty("OAUTH_CONSUMER_SECRET");
		OAUTH_CONSUMER_ACCESS_TOKEN = properties.getProperty("OAUTH_CONSUMER_ACCESS_TOKEN");
		OAUTH_CONSUMER_ACCESS_TOKEN_SECRET = properties.getProperty("OAUTH_CONSUMER_ACCESS_TOKEN_SECRET");
		
		String strKeywordsNum = properties.getProperty("n_KEYWORDS_NUM");
		n_KEYWORDS_NUM = Integer.parseInt(strKeywordsNum);
	}
	public String getSecretKey() {
		return secretKey;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getRegion() {
		return region;
	}

	public String getDB_CONNECTION_URL() {
		return DB_CONNECTION_URL;
	}

	public String getDB_USER_NAME() {
		return DB_USER_NAME;
	}

	public String getDB_PASSWORD() {
		return DB_PASSWORD;
	}
	
	public String getTABLE_NAME() {
		return TABLE_NAME;
	}

	public String getOAUTH_CONSUMER_KEY() {
		return OAUTH_CONSUMER_KEY;
	}

	public String getOAUTH_CONSUMER_SECRET() {
		return OAUTH_CONSUMER_SECRET;
	}

	public String getOAUTH_CONSUMER_ACCESS_TOKEN() {
		return OAUTH_CONSUMER_ACCESS_TOKEN;
	}

	public String getOAUTH_CONSUMER_ACCESS_TOKEN_SECRET() {
		return OAUTH_CONSUMER_ACCESS_TOKEN_SECRET;
	}

	public int getN_KEYWORDS_NUM() {
		return n_KEYWORDS_NUM;
	}
	
}
