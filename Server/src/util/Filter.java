package util;

import java.io.*;
import java.util.ArrayList;


public class Filter {
	private static int n_KEYWORDS_NUM;
	
	private String[] keywords; 
	
	public Filter(String keywordfile_path) throws IOException
	{
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		n_KEYWORDS_NUM = propertiesManager.getN_KEYWORDS_NUM();
		keywords = new String[n_KEYWORDS_NUM];
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(keywordfile_path)));
		String keyword = null;
		int i=0;
		while((keyword =reader.readLine()) != null)
		{
			keywords[i] = keyword;
			i++;
		}
		reader.close();
	}
	
	public String[] getTages()
	{
		return this.keywords;
	}
	
	public String getTag(String text) throws IOException
	{
		String tag = null;
		if(text != null)
		{
			for(String key : keywords)
			{
				if(text.contains(key))
					return key;
			}
		}
		return tag;
	}
}
