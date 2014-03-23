/*
 * Title: Homework #9
 * Name: Danni Wu
 * Andrew ID: danniw
 * Course Number: 08600
 * Date: 11/28/2013
 */
package databeans;


public class TweetBean {
	private long   tweetId;
	private long	userId;
	private String text;
	private String createdAt; //time and date string??
	private long retweetedStatus;   //store the initial tweet id
	private String placeId;

	public long	  getTweetId()				{ return tweetId; }
	public long    getUserId()               { return userId;     }
	public String getText()              	{ return text;        }
	public String getCreatedAt() 			{ return createdAt; }
	public long getRetweetedStatus()     { return retweetedStatus; }
	public String getPlaceId()					{ return placeId;	}
	
	public void	  setTweetId(long tweetId)  { this.tweetId = tweetId; }
	public void   setUserId(long l)      	{ userId = l;       }
	public void   setText(String text)     	{ this.text = text;         }
	public void   setRetweetedStatus(long retweetedStatus)  { this.retweetedStatus = retweetedStatus;}
	public void   setPlaceId(String placeId)	{ this.placeId = placeId;}
	public void   setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}