/*
 * Title: Homework #9
 * Name: Danni Wu
 * Andrew ID: danniw
 * Course Number: 08600
 * Date: 11/28/2013
 */
package databeans;

public class User{
	private long userID;
	private String uidStr;
	
	public long	getUserID()	{return userID;}
	public String getUidStr() {return uidStr;}


	public void	setUserID(long id)	{ userID = id;}
	public void setUidStr(String uidStr) { this.uidStr = uidStr;}
	
	public String toString() {
		return "User("+getUserID() + getUidStr() +")";
	}

}
