package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import databeans.PlaceBean;

public class PlaceDAO {
	
	private List<Connection> connectionPool = new ArrayList<Connection>();

	private String jdbcDriver;
	private String jdbcURL;
	private String tableName;
	
	public PlaceDAO(String jdbcDriver, String jdbcURL, String tableName) throws MyDAOException {
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
	
	public void create(PlaceBean place){
		Connection con = null;
        try {
        	con = getConnection();
        	PreparedStatement pstmt = con.prepareStatement("INSERT INTO " + tableName + " (placeId,coordinates,boundingType,country,countryCode,fullName,name,placeType,url) VALUES (?,?,?,?,?,?,?,?,?)"
        			+ " ON DUPLICATE KEY UPDATE placeId = placeId");
        	pstmt.setString(1, place.getPlaceId());
        	pstmt.setString(2,place.getCoordinates());
        	pstmt.setString(3,place.getBoundingType());
        	pstmt.setString(4,place.getCountry());
        	pstmt.setString(5, place.getCountryCode());
        	pstmt.setString(6,place.getFullName());
        	pstmt.setString(7,place.getName());
        	pstmt.setString(8,place.getPlaceType());
        	pstmt.setString(9,place.getUrl());
        	int count = pstmt.executeUpdate();
        	if (count != 1) throw new SQLException("Insert updated "+count+" rows");
    		
        	pstmt.close();
        	releaseConnection(con);
        	
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	try {
				throw new MyDAOException(e);
			} catch (MyDAOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
	}

	public PlaceBean getPlace(String placeId) throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();

        	PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + tableName + " WHERE placeId=?");
        	pstmt.setString(1,placeId);
        	ResultSet rs = pstmt.executeQuery();
        	
        	PlaceBean placeBean;
        	if (!rs.next()) {
        		placeBean = null;
        	} else {
        		placeBean = new PlaceBean();
        		placeBean.setPlaceId(rs.getString("placeId"));
        		placeBean.setBoundingType(rs.getString("boundingType"));
        		placeBean.setCoordinates(rs.getString("coordinates"));
        		placeBean.setCountry(rs.getString("country"));
        		placeBean.setCountryCode(rs.getString("countryCode"));
        		placeBean.setFullName(rs.getString("fullName"));
        		placeBean.setName(rs.getString("name"));
        		placeBean.setPlaceType(rs.getString("placeType"));
        		placeBean.setUrl(rs.getString("url"));
        	} 
        	rs.close();
        	pstmt.close();
        	releaseConnection(con);
            return placeBean;
            
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
	}
	
	public PlaceBean[] getAllPace() throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            List<PlaceBean> list = new ArrayList<PlaceBean>();
            while (rs.next()) {        		
        		PlaceBean placeBean = new PlaceBean();
        		placeBean.setPlaceId(rs.getString("placeId"));
        		placeBean.setBoundingType(rs.getString("boundingType"));
        		placeBean.setCoordinates(rs.getString("coordinates"));
        		placeBean.setCountry(rs.getString("country"));
        		placeBean.setCountryCode(rs.getString("countryCode"));
        		placeBean.setFullName(rs.getString("fullName"));
        		placeBean.setName(rs.getString("name"));
        		placeBean.setPlaceType(rs.getString("placeType"));
        		placeBean.setUrl(rs.getString("url"));
            	list.add(placeBean);
            }
            stmt.close();
            releaseConnection(con);
            
            return list.toArray(new PlaceBean[list.size()]);
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	
	public void delete(String placeId) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM " + tableName + " WHERE placeId=" + placeId);
            stmt.close();
            releaseConnection(con);
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
    	Connection con = getConnection();
    	try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(
            		"CREATE TABLE " + tableName +
            		"(placeId VARCHAR(255)," +
            		"coordinates VARCHAR(255) NULL," +
            		"boundingType VARCHAR(50) NULL," +
            		"country VARCHAR(50) NULL," +
            		"countryCode VARCHAR(255)," +
            		"fullName VARCHAR(255) NULL," +
            		"name VARCHAR(50) NULL," +
            		"placeType VARCHAR(50) NULL," +
            		"url VARCHAR(255) NULL,"+
            		"PRIMARY KEY(placeId))");    
            
            stmt.close();
            releaseConnection(con);
        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }
}
