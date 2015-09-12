package util;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Connection;

public class JDBC {

	public Connection m_connection = null;

	private static final String DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	private static String DB_CONNECTION_URL;
	private static String DB_USER_NAME;
	private static String DB_PASSWORD;
	private static String TABLE_NAME;

	private static JDBC m_instance = null;

	private JDBC() throws SQLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		DB_CONNECTION_URL = propertiesManager.getDB_CONNECTION_URL();
		DB_USER_NAME = propertiesManager.getDB_USER_NAME();
		DB_PASSWORD = propertiesManager.getDB_PASSWORD();
		TABLE_NAME = propertiesManager.getTABLE_NAME();
		
		loadDbDriver();
		createDbConnection();
	}

	public static JDBC getInstance() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		if (m_instance == null) {
			m_instance = new JDBC();
		}

		return m_instance;
	}

	public void loadDbDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName(DB_DRIVER_CLASS_NAME).newInstance();
		//System.out.println("load JDBC Driver successful!");

	}

	public void createDbConnection() throws SQLException {

		if (m_connection == null) {
			m_connection = (com.mysql.jdbc.Connection) DriverManager
					.getConnection(DB_CONNECTION_URL, DB_USER_NAME, DB_PASSWORD);
			//System.out.println("connect to database successfully.");
		}

	}

	public void createTweetTable() throws SQLException {
			String tableName = "tweet";
			String strsql = "CREATE TABLE " + tableName + " "
					+ "(ID int(200) NOT NULL AUTO_INCREMENT,"
					+ " tweetID  bigint(200)," + " userName varchar(100),"
					+ " Latitude double," + " Longtitude double,"
					+ " tweetTimeStamp bigint(200) default '0',"
					+ "scoreTimeStamp bigint(200) default '0'," + " keyWord varchar(10),"
					+ " text varchar(200)," + " SentimentScore double default '-65536',"
					+ " primary key(ID)" + " )";
			Statement stmt;
			try {
				stmt = m_connection.createStatement();
				stmt.execute(strsql);
				//System.out.println("createTable table succeed!");
			} catch (SQLException e) {
				m_instance = null;
				e.printStackTrace();
				throw e;
			}
	}

	/* Insert tweet to TweetTable with tweetTimeStamp */
	public void insertToTweet(Long tweetID, String userName, double lat,
			double log, long tweetTimeStamp, String keyWord, String text) throws SQLException {
			Statement stmt;
			try {
				stmt = m_connection.createStatement();
				String sql = "INSERT INTO "
						+ TABLE_NAME
						+ "(tweetID,userName,Latitude,Longtitude,tweetTimeStamp,scoreTimeStamp,keyWord, text)"
						+ " VALUES ('" + tweetID + "','" + userName + "','" + lat
						+ "','" + log + "','" + tweetTimeStamp + "','0','"
						+ keyWord + "','" + text + "')";
				int cRecordsInserted = stmt.executeUpdate(sql);
				//System.out.println("insert tweet to DB successful, this number of inserted records is:"+ cRecordsInserted);
			} catch (SQLException e) {
				m_instance = null;
				e.printStackTrace();
				throw e;
			}
	}

	/* Update tweet with score and scoreTimeStamp */
	public void updateSentimentScore(Long tweetID, double sentimentScore,
			long scoreTimeStamp) throws SQLException {
			Statement stmt;
			try {
				stmt = m_connection.createStatement();
				String sql = "update " + TABLE_NAME + " set SentimentScore = '"
						+ sentimentScore + "', scoreTimeStamp = '" + scoreTimeStamp
						+ "' where tweetID=" + tweetID + "";
				stmt.executeUpdate(sql);
				//System.out.println("update successful:");
			} catch (SQLException e) {
				m_instance = null;
				e.printStackTrace();
				throw e;
			}
	}

	/* Get all tweets in the TweetTable */
	public String getTweets(String keyWord) throws SQLException {
		StringBuffer sbResult = new StringBuffer("{\"geo\": [");
		try {
			String strsql = "select * from " + TABLE_NAME
					+ " where keyWord =  '" + keyWord + "'";
			Statement stmt = m_connection.createStatement();
			ResultSet rs = stmt.executeQuery(strsql);
			boolean flag = true;

			while (rs.next()) {
				System.out.println("keyword" + rs.getString("keyWord"));
				if (!flag)
					sbResult.append(",");

				sbResult.append("{\"lat\"");

				double lat = rs.getDouble("Latitude");
				sbResult.append(":" + lat + ",\"log\"");

				double log = rs.getDouble("Longtitude");
				sbResult.append(":" + log + ",\"userName\"");

				String userName = rs.getString("userName");
				sbResult.append(":\"" + string2Json(userName) + "\",\"text\"");

				String text = rs.getString("text");
				sbResult.append(":\"" + string2Json(text) + "\",\"tweetTimeStamp\"");

				long tweetTimeStamp = rs.getLong("tweetTimeStamp");
				sbResult.append(":" + tweetTimeStamp + ",\"scoreTimeStamp\"");

				long scoreTimeStamp = rs.getLong("scoreTimeStamp");
				sbResult.append(":" + scoreTimeStamp + ",\"tweetID\"");
				
				long tweetID = rs.getLong("tweetID");
				sbResult.append(":" + tweetID + ",\"SentimentScore\"");
				
				double SentimentScore = rs.getDouble("SentimentScore");
				sbResult.append(":" + SentimentScore + "}");
				

				flag = false;
			}

		} catch (SQLException e) {
			m_instance = null;
			System.out.println("[1] Get tweets from " + TABLE_NAME
					+ "failed. exception = [" + e.getMessage() + "]");
			throw e;
		}
		sbResult = sbResult.append("]}");
		System.out.println("Get all Tweets:" + sbResult);
		return sbResult.toString();
	}

	/* Get tweets that is not transferred yet */
	public String getLatestTweets(String keyWord, long tweetTimeStamp) throws SQLException {

		StringBuffer sbResult = new StringBuffer("{\"geo\": [");
			String strsql = "select * from " + TABLE_NAME
					+ " where keyWord = '" + keyWord + "' and tweetTimeStamp >"
					+ tweetTimeStamp + "";
			Statement stmt;
			try {
				stmt = m_connection.createStatement();
				
				ResultSet rs = stmt.executeQuery(strsql);
				boolean flag = true;
				
				while (rs.next()) {
					if (!flag)
						sbResult.append(",");
					
					sbResult.append("{\"lat\"");
					
					double lat = rs.getDouble("Latitude");
					sbResult.append(":" + lat + ",\"log\"");
					
					double log = rs.getDouble("Longtitude");
					sbResult.append(":" + log + ",\"userName\"");
					
					String userName = rs.getString("userName");
					sbResult.append(":\"" + string2Json(userName) + "\",\"text\"");
					
					String text = rs.getString("text");
					sbResult.append(":\"" + string2Json(text) + "\",\"tweetTimeStamp\"");
					
					long newTweetTimeStamp = rs.getLong("tweetTimeStamp");
					sbResult.append(":" + newTweetTimeStamp + ",\"scoreTimeStamp\"");
					
					long scoreTimeStamp = rs.getLong("scoreTimeStamp");
					sbResult.append(":" + scoreTimeStamp + ",\"tweetID\"");
					
					long tweetID = rs.getLong("tweetID");
					sbResult.append(":" + tweetID + ",\"SentimentScore\"");
					
					double SentimentScore = rs.getDouble("SentimentScore");
					sbResult.append(":" + SentimentScore + "}");
					
					flag = false;
					
				}
				
			} catch (SQLException e) {
				m_instance = null;
				e.printStackTrace();
				throw e;
			}
			sbResult = sbResult.append("]}");
			System.out.println("Get latest Tweets:" + sbResult);
			return sbResult.toString();
	}

	/* Get tweets that is scored but not transferred yet */
	public String getLatestScoredTweets(String keyWord,
			long scoreTimeStamp) throws SQLException {

		StringBuffer sbResult = new StringBuffer("{\"geo\": [");
			String strsql = "select * from " + TABLE_NAME
					+ " where keyWord = '" + keyWord
					+ "' and  scoreTimeStamp >" + scoreTimeStamp + "";
			Statement stmt;
			try {
				stmt = m_connection.createStatement();
				ResultSet rs = stmt.executeQuery(strsql);
				boolean flag = true;
				
				while (rs.next()) {
					if (!flag)
						sbResult.append(",");
					
					sbResult.append("{\"lat\"");
					
					double lat = rs.getDouble("Latitude");
					sbResult.append(":" + lat + ",\"log\"");
					
					double log = rs.getDouble("Longtitude");
					sbResult.append(":" + log + ",\"userName\"");
					
					String userName = rs.getString("userName");
					sbResult.append(":\"" + string2Json(userName) + "\",\"text\"");
					
					String text = rs.getString("text");
					sbResult.append(":\"" + string2Json(text) + "\",\"tweetTimeStamp\"");
					
					long newTweetTimeStamp = rs.getLong("tweetTimeStamp");
					sbResult.append(":" + newTweetTimeStamp + ",\"scoreTimeStamp\"");
					
					long newScoreTimeStamp = rs.getLong("scoreTimeStamp");
					sbResult.append(":" + newScoreTimeStamp + ",\"tweetID\"");
					
					
					long tweetID = rs.getLong("tweetID");
					sbResult.append(":" + tweetID + ",\"SentimentScore\"");
					
					double SentimentScore = rs.getDouble("SentimentScore");
					sbResult.append(":" + SentimentScore + "}");
					
					flag = false;
					System.out.println("Get latest Tweets with SentimentScore:" + sbResult);
				}
			} catch (SQLException e) {
				m_instance = null;
				e.printStackTrace();
				throw e;
			}
			sbResult = sbResult.append("]}");
			return sbResult.toString();
	}
	
	/**
     * deal with JSON special character 
     * @param s
     * @return String
     */
    public String string2Json(String s) {      
        StringBuffer sb = new StringBuffer();      
        for (int i=0; i<s.length(); i++) {
        	char c = s.charAt(i);  
        	 switch (c){
        	 case '\"':      
                 sb.append("\\\"");      
                 break;      
             case '\\':      
                 sb.append("\\\\");      
                 break;      
             case '/':      
                 sb.append("\\/");      
                 break;      
             case '\b':      
                 sb.append("\\b");      
                 break;      
             case '\f':      
                 sb.append("\\f");      
                 break;      
             case '\n':      
                 sb.append("\\n");      
                 break;      
             case '\r':      
                 sb.append("\\r");      
                 break;      
             case '\t':      
                 sb.append("\\t");      
                 break;      
             default:      
                 sb.append(c);   
        	 }
         }    
        return sb.toString();
    }

	public void closeDbConnection() throws SQLException {
			if (m_connection != null) {
				m_connection.close();
				m_connection = null;
			}
	}
/*
	public static void main(String[] args) throws SQLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		JDBC test = JDBC.getInstance();
		test.createTweetTable();
	}
*/
}