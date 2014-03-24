/*
 * Title: Homework #9
 * Name: Danni Wu
 * Andrew ID: danniw
 * Course Number: 08600
 * Date: 11/28/2013
 */
package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;
import org.genericdao.Transaction;

import databeans.TweetBean;

public class TweetDAO {
	private List<Connection> connectionPool = new ArrayList<Connection>();

	private String jdbcDriver;
	private String jdbcURL;
	private String tableName;
	
	public TweetDAO(String jdbcDriver, String jdbcURL, String tableName) throws MyDAOException {
		this.jdbcDriver = jdbcDriver;
		this.jdbcURL    = jdbcURL;
		this.tableName  = tableName;
		
		if (!tableExists()) createTable();
	}
	
	private synchronized Connection getConnection() throws MyDAOException {
		if (connectionPool.size() > 0) {
			return connectionPool.remove(connectionPool.size()-1);
		}
		
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new MyDAOException(e);
        }

        try {
            return DriverManager.getConnection(jdbcURL);
        } catch (SQLException e) {
            throw new MyDAOException(e);
        }
	}
	
	private synchronized void releaseConnection(Connection con) {
		connectionPool.add(con);
	}

//	public void create(TweetBean tweet){
//		//createAutoIncrement(tweet);
//		create(tweet);
//	}
	
	public void create(TweetBean tweet) throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();
        	PreparedStatement pstmt = con.prepareStatement("INSERT INTO " + tableName + " (tweetId,createdAt,retweeted_status,text,userId,placeId) VALUES (?,?,?,?,?,?)");
        	pstmt.setLong(1, tweet.getTweetId());
        	pstmt.setString(2,tweet.getCreatedAt());
        	pstmt.setLong(3,tweet.getRetweetedStatus());
        	pstmt.setString(4,tweet.getText());
        	pstmt.setLong(5,tweet.getUserId());
        	pstmt.setString(6,tweet.getPlaceId());
        	int count = pstmt.executeUpdate();
        	if (count != 1) throw new SQLException("Insert updated "+count+" rows");
        	
        	pstmt.close();
        	releaseConnection(con);
        	
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
	}

	public TweetBean[] getTweet(long l, String createdAt) throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();

        	PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + tableName + " WHERE userId=? AND createdAt=?");
        	pstmt.setLong(1,l);
        	pstmt.setString(2,createdAt);
        	
        	ResultSet rs = pstmt.executeQuery();
        	
        	List<TweetBean> list = new ArrayList<TweetBean>();
            while (rs.next()) {
            	TweetBean tweetBean = new TweetBean();
            	tweetBean.setTweetId(rs.getLong("tweetId"));
        		tweetBean.setCreatedAt(rs.getString("createdAt"));
        		tweetBean.setPlaceId(rs.getString("placeId"));
        		tweetBean.setRetweetedStatus(rs.getLong("retweeted_status"));
        		tweetBean.setText(rs.getString("text"));
        		tweetBean.setUserId(rs.getLong("userId"));
            	list.add(tweetBean);
            }
        	
        	rs.close();
        	pstmt.close();
        	releaseConnection(con);
            return list.toArray(new TweetBean[list.size()]);
            
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
	}
	
	public TweetBean[] getAllTweet() throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            
            List<TweetBean> list = new ArrayList<TweetBean>();
            while (rs.next()) {
            	TweetBean tweetBean = new TweetBean();
        		tweetBean.setCreatedAt(rs.getString("createdAt"));
        		tweetBean.setPlaceId(rs.getString("placeId"));
        		tweetBean.setRetweetedStatus(rs.getLong("retweeted_status"));
        		tweetBean.setText(rs.getString("text"));
        		tweetBean.setUserId(rs.getInt("userId"));
            	list.add(tweetBean);
            }
            stmt.close();
            releaseConnection(con);
            
            return list.toArray(new TweetBean[list.size()]);
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	
	public void delete(int userId, String createdAt) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM " + tableName + " WHERE userId=" + userId + " AND createdAt=" + createdAt);
            stmt.close();
            releaseConnection(con);
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}

//	public TweetBean[] getAllTweet () throws RollbackException {
//		TweetBean[] items = match();
//		return items;
//	}
//
//	public TweetBean[] getUserTweets(int userID) throws RollbackException{
//		TweetBean[] tweets = match(MatchArg.equals("userId", userID));
//		return tweets;
//	}
//	
//	public TweetBean getTweet(int userID, String createdAt) throws RollbackException {
//		TweetBean tweet = read(userID, createdAt);
//		return tweet;
//	}
//
//	public void delete(int userID, String createdAt) throws RollbackException {
//		try {
//			if (Transaction.isActive()) Transaction.rollback();
//			Transaction.begin();
//			TweetBean p = read(userID, createdAt);
//
//			if (p == null) {
//				throw new RollbackException("User " + userID + "'s Tweet does not exist: createdAt= "+ createdAt);
//			}
//
//			delete(userID, createdAt);
//			Transaction.commit();
//		} finally {
//			if (Transaction.isActive()) Transaction.rollback();
//		}
//	}
	
	private boolean tableExists() throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();
        	DatabaseMetaData metaData = con.getMetaData();
        	ResultSet rs = metaData.getTables(null, null, tableName, null);
        	
        	boolean answer = rs.next();
        	
        	rs.close();
        	releaseConnection(con);
        	
        	return answer;

        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }

	private void createTable() throws MyDAOException {
    	Connection con = getConnection();
    	try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(
            		"CREATE TABLE " + tableName +
            		"(tweetId BIGINT," +
            		"createdAt VARCHAR(50)," +
            		"retweeted_status BIGINT," +
            		"text VARCHAR(255)," +
            		"userId BIGINT," +
            		"placeId VARCHAR(255) NULL," +
            		"PRIMARY KEY(tweetId)," +
            		"FOREIGN KEY(placeId) REFERENCES place(placeId) ON DELETE SET NULL," +
            		"FOREIGN KEY(userId) REFERENCES user(userId) ON DELETE CASCADE)");
            stmt.close();
            releaseConnection(con);
        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }

}
