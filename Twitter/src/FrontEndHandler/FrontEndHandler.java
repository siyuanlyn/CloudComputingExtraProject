package FrontEndHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.genericdao.RollbackException;

import model.Model;
import model.MyDAOException;
import model.TweetDAO;
import databeans.TweetBean;
import databeans.User;


public class FrontEndHandler extends HttpServlet{
	/**
	 * 
	 */
	private static Model model = null;
	private static FrontEndHandler handler = null;
			
	private static final long serialVersionUID = 1L;
	
	public void init() throws ServletException{
		try {
			model = new Model(getServletConfig());
		} catch (MyDAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Model getModel () throws ServletException, MyDAOException {
		if (model == null) {
			model = new Model(getServletConfig());
		} 
		return model;
	}
	
	public static FrontEndHandler getFrontEndHandler() {
		if (handler == null) {
			handler = new FrontEndHandler();
		}
		return handler;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			try {
				performTheAction(request, response);
			} catch (MyDAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void performTheAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, NumberFormatException, RollbackException, MyDAOException{
		HttpSession session = request.getSession(true);
		String servletPath = request.getServletPath();
		//User user = (User)session.getAttribute("user");
		String action = getActionName(servletPath);

		PrintWriter out = response.getWriter();
		if (action.equals("q1")){
			//return Action.perform(action, request);
			
			out.println("lmao, " + "0992-3171-4790");
			Date date = new Date();
			out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		}

		else if (action.equals("q2")){
			//return Action.perform("list.do", request);
			String userId = request.getParameter("userid");
			String tweet_time = request.getParameter("tweet_time");
			TweetDAO tweetDAO = Model.getTweetDAO();
			//System.out.println(userId + " " + tweet_time);
			TweetBean[] tweet = tweetDAO.getTweet(Long.parseLong(userId), tweet_time);
			//System.out.println(tweet);
			Arrays.sort(tweet, new Comparator<TweetBean>() {
				@Override
				public int compare(TweetBean arg0, TweetBean arg1) {
					// TODO Auto-generated method stub
					if (arg0.getTweetId() < arg1.getTweetId()) return -1;
					else if (arg0.getTweetId() == arg1.getTweetId()) return 0;
					else return 1;
				}	
			});
			out.println("lmao, " + "0992-3171-4790");
			for (int i = 0; i < tweet.length; i++) {
				out.println(tweet[i].getTweetId());
			}
		}
		
		else if (action.equals("q3")) {
			String userId = request.getParameter("userid");
			TweetDAO tweetDAO = Model.getTweetDAO();
			TweetBean[] tweets = tweetDAO.getTweetByUid(userId);
			ArrayList<Long> userIds = new ArrayList<Long>();
			for (TweetBean bean : tweets) {
				userIds.addAll(tweetDAO.getUidByRetweetId(bean.getTweetId()));
			}
			Collections.sort(userIds);
			out.println("lmao, " + "0992-3171-4790");
			for (Long uid : userIds) {
				out.println(uid);
			}
		}

		//return Action.perform(action, request);
	}

	private String getActionName(String path){
		int slash = path.lastIndexOf('/');
		return path.substring(slash + 1, slash+3);
	}

}
