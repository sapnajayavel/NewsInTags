package com.newsintags.trending.news;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class GetNewsForSite
 */
@WebServlet("/GetNewsForSite")
public class GetNewsForSite extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNewsForSite() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String siteId = request.getParameter("siteId");
		String callback = request.getParameter("callback");
		JSONObject finalObj = new JSONObject();
		try{
			JSONArray newsArray = MongoDbUtil.getNewsForSite(siteId,"NewsCollection");
			
			finalObj.put("newsList", newsArray);
			finalObj.put("siteDetails",new JSONObject(MongoDbUtil.getDBObject(siteId, "SiteCollection").toString()));
			finalObj.put("status", "success");
			
		}
		catch(Exception e){
			finalObj.put("status", "failure");
		}
		finally{
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+finalObj.toString()+")");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
