package unitest;

import java.io.IOException;

import org.junit.Test;

import util.SNSManager;

public class SNSTest {
	
	@Test
	public void test() throws IOException
	{
		SNSManager manager = SNSManager.getInstance();
		manager.publishToATopic("aaa");
	}
	
}
