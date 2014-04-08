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
import java.util.List;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;
import org.genericdao.Transaction;

import databeans.TweetBean;
import databeans.User;


public class UserDAO {
	
	private List<Connection> connectionPool = new ArrayList<Connection>();

	private String jdbcDriver;
	private String jdbcURL;
	private String tableName;
	
	public UserDAO(String jdbcDriver, String jdbcURL, String tableName) throws MyDAOException {
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


	public void create(User user) throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();
        	PreparedStatement pstmt = con.prepareStatement("INSERT INTO " + tableName + " (userId, uidStr) VALUES (?,?)"
        			 + " ON DUPLICATE KEY UPDATE userId = userId");
        			//+ "where not exists(select * from " + tableName + " where userId=" + user.getUserID() + ")limit 1");
//        	WHERE NOT EXISTS (
//        		    SELECT name FROM table_listnames WHERE name = 'Rupert'
//        		) LIMIT 1;
        	pstmt.setLong(1,user.getUserID());
        	pstmt.setString(2,user.getUidStr());
        	int count = pstmt.executeUpdate();
        	if (count != 1) throw new SQLException("Insert updated "+count+" rows");
        	
        	pstmt.close();
        	releaseConnection(con);
        	
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
	}

	public User read(int userID) throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();

        	PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + tableName + " WHERE userId=?");
        	pstmt.setInt(1,userID);
        	ResultSet rs = pstmt.executeQuery();
        	
        	User user;
        	if (!rs.next()) {
        		user = null;
        	} else {
        		user = new User();
        		user.setUserID(rs.getInt("userId"));
        		user.setUidStr(rs.getString("uidStr"));
        	}
        	
        	rs.close();
        	pstmt.close();
        	releaseConnection(con);
            return user;
            
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
	}


	public User[] getAllUsers() throws RollbackException, MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE =?");
            
            List<User> list = new ArrayList<User>();
            while (rs.next()) {
            	User user = new User();
        		user.setUserID(rs.getInt("userId"));
        		user.setUidStr(rs.getString("uidStr"));
            	list.add(user);
            }
            stmt.close();
            releaseConnection(con);
            
            return list.toArray(new User[list.size()]);
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	
	
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
		Connection con = null;
        try {
        	con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE TABLE " + tableName + " (userId BIGINT NOT NULL, uidStr VARCHAR(255), PRIMARY KEY(userId))");
            stmt.close();
        	
        	releaseConnection(con);

        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }
	
}
