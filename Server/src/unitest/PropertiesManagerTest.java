package unitest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import util.PropertiesManager;

public class PropertiesManagerTest {

	@Test
	public void test() {
		try {
			PropertiesManager prop = PropertiesManager.getInstance();
			System.out.println(prop.getAccessKey());
			System.out.println(prop.getSecretKey());
			System.out.println(prop.getQueueName());
			System.out.println(prop.getRegion());
			System.out.println(prop.getDB_CONNECTION_URL());
			System.out.println(prop.getDB_USER_NAME());
			System.out.println(prop.getDB_PASSWORD());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
