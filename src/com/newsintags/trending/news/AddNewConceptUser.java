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
 * Servlet implementation class AddNewConceptUser
 */
@WebServlet("/AddNewConceptUser")
public class AddNewConceptUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewConceptUser() {
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
			String conceptId = request.getParameter("conceptId");
			String screenName = request.getParameter("screenName");
			MongoDbUtil.insertUserConcept(conceptId, screenName);
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
