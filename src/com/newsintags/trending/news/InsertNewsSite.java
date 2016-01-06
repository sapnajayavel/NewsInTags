package com.newsintags.trending.news;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class InsertNewsSite
 */
@WebServlet("/InsertNewsSite")
public class InsertNewsSite extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertNewsSite() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject resultObj = new JSONObject();
		String callback = request.getParameter("callback");
		try{
			String siteId = request.getParameter("siteId");
			String screenName = request.getParameter("screenName");
			MongoDbUtil.insertUserNewsSite(siteId, screenName);
			resultObj.put("status", "success");
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+resultObj.toString()+")");
		}
		catch(Exception e){
			e.printStackTrace();
			resultObj.put("status", "failure");
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+resultObj.toString()+")");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
