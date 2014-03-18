package FrontEndHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FrontEndHandler extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		out.println("lmao, " + "0992-3171-4790");
		Date date = new Date();
		out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	}

}
