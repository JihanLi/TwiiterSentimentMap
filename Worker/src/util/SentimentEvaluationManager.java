package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;

public class SentimentEvaluationManager {
	private static SentimentEvaluationManager m_instance = null;
	private static AlchemyAPI alchemyObj = null;
	
	public static SentimentEvaluationManager getInstance() throws FileNotFoundException, IOException {
		if (m_instance == null) {
			m_instance = new SentimentEvaluationManager();
		} 
		return m_instance;
	}
	
	private SentimentEvaluationManager() throws FileNotFoundException, IOException {

		String apiKeyFilePath = this.getClass().getResource("/api_key.txt").getPath();
		alchemyObj = AlchemyAPI.GetInstanceFromFile(apiKeyFilePath);

	}
	
	public double EvaluateSentiment(String text) throws Exception  {

		// Extract sentiment for a text string.
		Document doc;
		double sentimentScore = 0;
		try {
			doc = alchemyObj.TextGetTextSentiment(text);
			sentimentScore = findSentimentScore(doc);
		} catch (XPathExpressionException | IOException | SAXException
				| ParserConfigurationException e) {
			e.printStackTrace();
			throw e;
		}
		
		return sentimentScore;
	}

	private double findSentimentScore(Document doc) {
		NodeList typeNodeList = doc.getElementsByTagName("type");
		Node typeNode = typeNodeList.item(0);
		String typeValue = typeNode.getTextContent();
		double score = 0.0;
		if ("positive".equals(typeValue) || "negative".equals(typeValue)) {
			NodeList scoreNodeList = doc.getElementsByTagName("score");
			Node scoreNode = scoreNodeList.item(0);
			String strScore = scoreNode.getTextContent();
			score = 0.0;
			try {
				score = Double.parseDouble(strScore);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			score = 0.0;
		}
		return score;
	}
	
	// utility method
	private String getStringFromDocument(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);

			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
