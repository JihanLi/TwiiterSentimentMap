package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.JDBC;


@WebServlet("/GetData")
public class GetData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("keyword") != null) {
        	Cookie[] cookies = request.getCookies();
        	long tweetTimeStamp = Long.valueOf(getCookieValue(cookies, "tweetTimeStamp"));
            String keyword = (String) request.getParameter("keyword");
            JDBC dao;
			try {
				dao = JDBC.getInstance();
				String serverTweetsJson = dao.getLatestTweets(keyword, tweetTimeStamp);
				response.getWriter().write(serverTweetsJson);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            response.getWriter().write("{}");
        }
    }
    
    private String getCookieValue(Cookie[] cookies, String cookieName){
    	Cookie c = null;
    	for(int i = 0;i < cookies.length; i++){
    		c = cookies[i];
    		if(c.getName().equals(cookieName))
    			return c.getValue();
    	}
		return null;
    }

}
