package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biz.MimicTweetGet;
import biz.TweetGet;

@WebServlet("/ThreadController")
public class TweetGetThreadController extends HttpServlet {
    private static Thread TweetGetThread = null;
    private static final long serialVersionUID = 1L;

    public TweetGetThreadController() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestType = request.getParameter("request");

        //create thread
        if (requestType.equals("create")) {
            try {
                TweetGet tweetGet = TweetGet.getInstance();
//            	MimicTweetGet tweetGet = MimicTweetGet.getInstance();
                if (TweetGetThread == null) {
                	TweetGetThread = new Thread(tweetGet);
                	String tweetGetThreadId = "" + TweetGetThread.getId();
                	response.getWriter().write(tweetGetThreadId);
                	TweetGetThread.start();
                }
            } catch (Exception e) {
                response.getWriter().write(e.toString());
            }
            return;
        }

        //destroy thread
        if (requestType.equals("destroy")) {
            try {
                TweetGet tweetGet = TweetGet.getInstance();
            	//MimicTweetGet tweetGet = MimicTweetGet.getInstance();
                tweetGet.terminate();
//                TweetGetThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.getWriter().write(TweetGetThread.getId() + "");
            TweetGetThread = null;
        }
    }

}
