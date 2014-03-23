package etl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonParser;

import model.Model;
import model.MyDAOException;
import model.PlaceDAO;
import model.TweetDAO;
import model.UserDAO;
import databeans.PlaceBean;
import databeans.TweetBean;
import databeans.User;

public class mysqlETL {
	JsonParser parser = null;	
	UserDAO userDAO = Model.getUserDAO();
	PlaceDAO placeDAO = Model.getPlaceDAO();
	TweetDAO tweetDAO = Model.getTweetDAO();

	public mysqlETL() throws FileNotFoundException, MyDAOException{
		parser = Json.createParser(new FileReader("/Users/Danny/Desktop/daniel"));
		try {
			parseJson(parser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long parseJson(JsonParser parser) throws IOException, MyDAOException{
		String key = null;
		String createdAt = null;
		long tweetId = -1;
		long retweeted_status = -1;
		String text = null;
		String objType = null;
	
		User user = null;
		PlaceBean place = null;
		TweetBean tweet = null;
		
		while (parser.hasNext()) {
			JsonParser.Event event = parser.next();
			switch(event) {
			case START_ARRAY:
			case END_ARRAY:
			case START_OBJECT:		    	 
			case END_OBJECT:
				if (key != null && key.equals("lang")) {
					//TODO:insert into database
					tweet = new TweetBean();
					tweet.setCreatedAt(createdAt);
					if (place != null) {
					tweet.setPlaceId(place.getPlaceId());
					} else {
						tweet.setPlaceId(null);
					}
					tweet.setRetweetedStatus(retweeted_status);
					tweet.setText(text);
					tweet.setTweetId(tweetId);
					if (user != null) {
						tweet.setUserId(user.getUserID());
					} else {
						tweet.setUserId(-1);
					}

					if (user != null) {
						userDAO.create(user);
					}
					if (place != null) {
						placeDAO.create(place);
					}
					if (tweet != null) {
						//System.out.println(tweet.getTweetId() + "@@@@@@@@@@@@@@@@" + tweet.getUserId());
						tweetDAO.create(tweet);
					}

					key = null;
					createdAt = null;
					tweetId = -1;
					retweeted_status = -1;;
					text = null;

					user = null;
					place = null;
					tweet = null;
					objType = null;
					break;
				}
			case VALUE_FALSE:
			case VALUE_NULL:
			case VALUE_TRUE:
				//System.out.println(event.toString());
				break;
			case KEY_NAME:
//				System.out.print(event.toString() + " " +
//						parser.getString() + " -  from parseJson");
				key = parser.getString();
				if (key.equals("user")) {
					objType = "user";
					user = parseUser(parser);
					//System.out.println(user + "!!!!!!!!!!!!!");
				} else if (key.equals("place")) {
					objType = "place";
					place = parsePlace(parser);
				} else if (key.equals("retweeted_status")) {
					//TODO:may need retweet information
					//retweeted_status = parseJson(parser);
					retweeted_status = parseRetweet(parser);
				}
				break;
			case VALUE_STRING:
				if (key.equals("created_at")) {
					createdAt = parseTime(parser.getString());
				} else if (key.equals("text")) {
					text = parser.getString().replaceAll("&gt;", ">").replaceAll(";&lt", "<");
					if (text.length() > 255) {
						text = text.substring(0, 255);
					}
				}

				break;
			case VALUE_NUMBER:
//				System.out.println(event.toString() + " " +
//						parser.getString());
				if (objType == null && key.equals("id")) {
					tweetId = parser.getLong();
				}
				break;
			}
		}
		return tweetId;
	}
	
	public long parseRetweet(JsonParser parser) {
		String key = null;
		long tweetId = -1;
		String objType = null;
		
		while (parser.hasNext()) {
			JsonParser.Event event = parser.next();
			System.out.println(event.toString() + "From parseRetweet$$$$$$$$$$$$$");
			switch(event) {
			case START_ARRAY:
			case END_ARRAY:
			case START_OBJECT:		    	 
			case END_OBJECT:
				if (key != null && key.equals("lang")) {
					return tweetId;
				}
				break;
			case VALUE_FALSE:
			case VALUE_NULL:
			case VALUE_TRUE:
				System.out.println(event.toString());
				break;
			case KEY_NAME:
//				System.out.print(event.toString() + " " +
//						parser.getString() + " - from parseUser");
				key = parser.getString();
				if (key.equals("user")) {
					objType = "user";
				} else if (key.equals("place")) {
					objType = "place";
				}
				break;
			case VALUE_STRING:
				break;
			case VALUE_NUMBER:
//				System.out.println(event.toString() + " " +
//						parser.getString());
				if (objType == null && key.equals("id")) {
					tweetId = parser.getLong();
				}
				break;
			}
		}
		return tweetId;
	}
	
	
	public String parseTime (String oldForm) {
		String[] createdTime = oldForm.split(" ");
		String month = createdTime[1];
		if (createdTime[1].equalsIgnoreCase("Jan")) {
			return createdTime[5] + "-01-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Feb")) {
			return createdTime[5] + "-02-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Mar")) {
			return createdTime[5] + "-03-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Apr")) {
			return createdTime[5] + "-04-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("May")) {
			return createdTime[5] + "-05-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Jun")) {
			return createdTime[5] + "-06-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Jul")) {
			return createdTime[5] + "-07-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Aug")) {
			return createdTime[5] + "-08-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Sept")) {
			return createdTime[5] + "-09-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Oct")) {
			return createdTime[5] + "-10-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Nov")) {
			return createdTime[5] + "-11-" + createdTime[2] + " " + createdTime[3];
		} else if (month.equalsIgnoreCase("Dec")) {
			return createdTime[5] + "-12-" + createdTime[2] + " " + createdTime[3];
		}
		return null;
		
	}

	public PlaceBean parsePlace(JsonParser parser) throws IOException{
		PlaceBean place = null;
		String key = null;
		String country = null;
		String country_code = null;
		String fullName = null;
		String placeId = null;
		String name = null;
		String placeType = null;
		String url = null;
		String boundingType = null; //TODO:may be deleted

		while (parser.hasNext()) {
			JsonParser.Event event = parser.next();
			switch(event) {
			case START_ARRAY:
				//TODO
				//parseBoundingBox(parser, place);
				break;
			case END_ARRAY:
			case START_OBJECT:		    	 
			case END_OBJECT:
				if (key != null && key.equals("attributes")) {
					place = new PlaceBean();
					place.setCountry(country);
					place.setCountryCode(country_code);
					place.setFullName(fullName);
					place.setPlaceId(placeId);
					place.setName(name);
					place.setPlaceType(placeType);
					place.setUrl(url);
					place.setCoordinates(null); //may be deleted
					return place;
				}
				break;
			case VALUE_FALSE:
			case VALUE_NULL:
				return null;
			case VALUE_TRUE:
//				System.out.println(event.toString());
				break;
			case KEY_NAME:
//				System.out.print(event.toString() + " " +
//						parser.getString() + " - ");
				key = parser.getString();
				break;
			case VALUE_STRING:
				if (key.equals("id")) {
					placeId = parser.getString();
				}else if (key.equals("country")) {
					country = parser.getString();
				} else if (key.equals("country_code")) {
					country_code = parser.getString();
				} else if (key.equals("full_name")) {
					fullName = parser.getString();
				} else if (key.equals("id")) {
					country = parser.getString();
				} else if (key.equals("name")) {
					name = parser.getString();
				} else if (key.equals("place_type")) {
					placeType = parser.getString();
				} else if (key.equals("url")) {
					url = parser.getString();
				} if (key.equals("type")) {  //TODO:may be deleted
					boundingType = parser.getString();
				}

				break;
			case VALUE_NUMBER:
//				System.out.println(event.toString() + " " +
//						parser.getString());
				break;
			}
		}
		return null;
	}

	public void parseBoundingBox(JsonParser parser, PlaceBean place) throws IOException{
		String key = null;
		String boundingType = null;

		ArrayList<double[]> l = new ArrayList<double[]>();

		outerLoop:
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch(event) {
				case START_ARRAY:
					l.add(parse1D(parser)); 
					break;
				case END_ARRAY:
					break outerLoop;
				case KEY_NAME:
					System.out.print(event.toString() + " " +
							parser.getString() + " - ");
					key = parser.getString();
					break;
				case VALUE_STRING:
					if (key.equals("type")) {
						boundingType = parser.getString();
					}
					break;
				default:
					throw new IOException("Not a 2 dimensional array! Seeing: "+event.toString());
				}
			}
		double[][] ret = new double[l.size()][l.get(0).length];
		for (int i = 0; i<l.size(); i++)
			ret[i] = l.get(i);
		place.setCoordinates(String.valueOf(ret)); //TODO?????
		place.setBoundingType(boundingType);
	}

	private static double[] parse1D(JsonParser parser) throws IOException {
		ArrayList<Double> l = new ArrayList<Double>();
		outerLoop:
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch(event) {
				case START_ARRAY: break;
				case END_ARRAY:
					break outerLoop;
				case VALUE_STRING:
					l.add(Double.parseDouble(parser.getString()));
					break;
				default:
					throw new IOException("Not a 1 dimensional array! Seeing: "+event.toString());
				}
			}
		double[] ret = new double[l.size()];
		for (int i = 0; i<l.size(); i++)
			ret[i] = l.get(i);
		return ret;
	}

	//	private static double[][] parse2D(JsonParser parser) throws IOException {
	//	    ArrayList<double[]> l = new ArrayList<double[]>();
	//
	//	    outerLoop:
	//	        while (parser.hasNext()) {
	//	            JsonParser.Event event = parser.next();
	//	            switch(event) {
	//	            case START_ARRAY:
	//	                l.add(parse1D(parser)); 
	//	                break;
	//	            case END_ARRAY:
	//	                break outerLoop;
	//	            default:
	//	                throw new IOException("Not a 2 dimensional array! Seeing: "+event.toString());
	//	            }
	//	        }
	//	    double[][] ret = new double[l.size()][l.get(0).length];
	//	    for (int i = 0; i<l.size(); i++)
	//	        ret[i] = l.get(i);
	//	    return ret;
	//	}

	public User parseUser(JsonParser parser) {
		String key = null;
		long userId = -1;
		String uidStr = null;
		
		while (parser.hasNext()) {
			JsonParser.Event event = parser.next();
			System.out.println(event.toString() + "From parseUser!!!");
			switch(event) {
			case START_ARRAY:
			case END_ARRAY:
			case START_OBJECT:		    	 
			case END_OBJECT:
				if (key != null && key.equals("notifications")) {
					User user = new User();
					user.setUserID(userId);
					user.setUidStr(uidStr);
					return user;
				}
				break;
			case VALUE_FALSE:
			case VALUE_NULL:
			case VALUE_TRUE:
				System.out.println(event.toString());
				break;
			case KEY_NAME:
				System.out.print(event.toString() + " " +
						parser.getString() + " - from parseUser");
				key = parser.getString();
				break;
			case VALUE_STRING:
				if (key.equals("id_str")) {
					uidStr = parser.getString();
				}
				break;
			case VALUE_NUMBER:
				System.out.println(event.toString() + " " +
						parser.getString());
				if (key.equals("id")) {
					userId = parser.getLong();
				}
				break;
			}
		}
		return null;
	}


}
