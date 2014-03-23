/*
 * Title: Homework #9
 * Name: Danni Wu
 * Andrew ID: danniw
 * Course Number: 08600
 * Date: 11/28/2013
 */
package model;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.RollbackException;

import databeans.TweetBean;
import databeans.User;
import etl.mysqlETL;


public class Model {
	private static TweetDAO tweetDAO;
	private static UserDAO userDAO;
	private static PlaceDAO placeDAO;
	private User[] userBean;
	private TweetBean[] tweetBean;

	public Model(ServletConfig config) throws ServletException, MyDAOException{
		userBean = new User[3];
		tweetBean = new TweetBean[4];
		for (int i = 0; i < userBean.length; i ++){
			userBean[i] = new User();
		}
		for (int i = 0; i < tweetBean.length; i ++){
			tweetBean[i] = new TweetBean();
		}
		try{
			String jdbcDriver = config.getInitParameter("jdbcDriverName");
			String jdbcURL = config.getInitParameter("jdbcURL");

			ConnectionPool pool = new ConnectionPool(jdbcDriver, jdbcURL);
			userDAO = new UserDAO(jdbcDriver, jdbcURL, "user");// new UserDAO(pool, "user");
			placeDAO = new PlaceDAO(jdbcDriver, jdbcURL, "place");
			tweetDAO = new TweetDAO(jdbcDriver, jdbcURL, "tweet_createdByIn"); //new TweetDAO(pool, "tweet_createdByIn");
			

			if (userDAO.getAllUsers().length == 0){
				userBean[0].setUserID(111);
				userBean[0].setUidStr("111");
				userBean[1].setUserID(666);
				userBean[1].setUidStr("666");
				userBean[2].setUserID(888);
				userBean[2].setUidStr("888");
				userDAO.create(userBean[0]);
				userDAO.create(userBean[1]);
				userDAO.create(userBean[2]);
			}
			
			if (tweetDAO.getAllTweet().length == 0){
				tweetBean[0].setTweetId(114749583439036416L);
				tweetBean[0].setCreatedAt("2013-10-02 00:00:00");
				tweetBean[0].setPlaceId(null);
				tweetBean[0].setRetweetedStatus(-1);
				tweetBean[0].setText("Hello world!");
				tweetBean[0].setUserId(111);
				tweetDAO.create(tweetBean[0]);
			} else {
				tweetDAO.delete(111, "2013-10-02");
			}
			try {
				mysqlETL test = new mysqlETL();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static TweetDAO getTweetDAO() { return tweetDAO; }
	public static UserDAO getUserDAO() { return userDAO; }
	public static PlaceDAO getPlaceDAO() { return placeDAO; }
}
