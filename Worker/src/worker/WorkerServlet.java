package worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import model.TweetMessage;
import util.SNSManager;
import util.SentimentEvaluationManager;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws IOException
            {
        try {

            Scanner scan = new Scanner(request.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scan.hasNextLine()) {
                builder.append(scan.nextLine());
            }
            scan.close();
           
            ObjectMapper mapper = new ObjectMapper(); 
            //SQS string -> TweetMessage(same as data structure in SQS)
            TweetMessage msg = mapper.readValue(builder.toString(), TweetMessage.class);// get msg from SQS
            String strTweetId = msg.getTweetId();
            System.out.println(" Work Tier deal with the message from SQS!" +msg);
            String tweetContent = msg.getTweetContent();
            Long tweetID = Long.parseLong(strTweetId);

            double score = 0.0;
            SentimentEvaluationManager se = SentimentEvaluationManager.getInstance();
            try {
				score = se.EvaluateSentiment(tweetContent);
			} catch (Exception e) {
				Random rand = new Random();
				
				System.out.println("Catch Exception() !");
				score = rand.nextDouble();
				if (rand.nextBoolean()) {
					score = score * (-1);
				}
				e.printStackTrace();
			}

            SNSManager sns = SNSManager.getInstance();
            sns.publishToATopic(tweetID + ":" + score);// send to SNS

            response.setStatus(200);

        } catch (RuntimeException exception) {
            response.setStatus(500);
            try (PrintWriter writer =
                         new PrintWriter(response.getOutputStream())) {
                exception.printStackTrace(writer);
            }
        }
    }
}
