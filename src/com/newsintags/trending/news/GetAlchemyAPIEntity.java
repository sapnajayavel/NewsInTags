package com.newsintags.trending.news;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBObject;
import com.newsintags.alchemy.api.AlchemyAPI;
import com.newsintags.util.MongoDbUtil;

public class GetAlchemyAPIEntity {
	public static void getEntityFromNews(String news, String newsId, Date createdDate)
	{
		try {
			AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");
			Document doc = null;
			doc = alchemyObj.TextGetRankedNamedEntities(news);
			String xmlResult = getStringFromDocument(doc);
			JSONObject soapDatainJsonObject = XML.toJSONObject(xmlResult);
			//System.out.println(soapDatainJsonObject.toString());
			if(soapDatainJsonObject.getJSONObject("results").get("entities") != "")
			{
				if(soapDatainJsonObject.getJSONObject("results").getJSONObject("entities").get("entity") instanceof JSONArray)
				{
					JSONArray entityArray = soapDatainJsonObject.getJSONObject("results").getJSONObject("entities").getJSONArray("entity");
					JSONObject entity;
					for(int i = 0; i< entityArray.length() ; i++){
						entity = (JSONObject) entityArray.get(i);
						BasicDBObject document = new BasicDBObject();
				        document.put("concept", entity.get("text"));
				        document.put("relevance", entity.get("relevance"));
				        document.put("type", entity.get("type"));
				        if(MongoDbUtil.checkIfConceptExists((String)entity.get("text")))
				        {
				        	MongoDbUtil.updateNewsConcept((String)entity.get("text"),newsId,createdDate);
				        }
				        else
				        {
				        	MongoDbUtil.insertConcepts(document,newsId,createdDate);
				        }
					}
				//System.out.println(soapDatainJsonObject.getJSONObject("results").getJSONObject("entities").getJSONArray("entity").toString());
				}
				else
				{
					JSONObject entityObject = soapDatainJsonObject.getJSONObject("results").getJSONObject("entities").getJSONObject("entity");
					BasicDBObject document = new BasicDBObject();
			        document.put("concept", entityObject.get("text"));
			        document.put("relevance", entityObject.get("relevance"));
			        document.put("type", entityObject.get("type"));
			        if(MongoDbUtil.checkIfConceptExists((String)entityObject.get("text")))
			        {
			        	MongoDbUtil.updateNewsConcept((String)entityObject.get("text"),newsId,createdDate);
			        }
			        else
			        {
			        	MongoDbUtil.insertConcepts(document,newsId,createdDate);
			        }
				}
				
			}
			else
			{
				System.out.println("Entity not found");
			}
			
			
		} catch (XPathExpressionException | SAXException
				| ParserConfigurationException  |IOException  e) {
			e.printStackTrace();
		}
	}
	
	private static String getStringFromDocument(Document doc) {
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
	
	public static void main(String[] args) {
		/*//getEntityFromNews("Hello there, my name is Bob Jones.  I live in the United States of America.  " +
            "Where do you live, Fred?");*/
	}
	
}
