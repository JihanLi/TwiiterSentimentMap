package unitest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import util.JDBC;

public class JDBCTest {

	@Test
	public void test() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException {
		JDBC dao = JDBC.getInstance();
		dao.insertToTweet(Long.parseLong("111"), "aaaa", 52.1, 2.36, Long.parseLong("454555"), "sdfg", "asdf");
	}

}
