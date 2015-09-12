package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.JDBC;

@WebServlet("/GetAllData")
public class GetAllData extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("keyword") != null) {
			String keyword = (String) request.getParameter("keyword");
			JDBC dao;
			try {
				dao = JDBC.getInstance();
				String serverTweetsJson = dao.getTweets(keyword);
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
}
