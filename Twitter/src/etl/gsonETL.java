package etl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import databeans.testTweet;

public class gsonETL {

	public void parse(){
		BufferedReader reader;
		try {
			List<testTweet> tweets = new ArrayList<testTweet>();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/Danny/Desktop/smJson")));
			JsonReader jsReader = new JsonReader(reader);
			Gson gson = new GsonBuilder().create();
			jsReader.setLenient(true);
			//jsReader.beginArray();
			String line = reader.readLine();

			while (line != null) {
				System.out.println(line);
				testTweet p = gson.fromJson(line, testTweet.class);
				tweets.add(p);
				line = reader.readLine();
			}
			//jsReader.endArray();
			for (testTweet t : tweets) {
				System.out.println(t);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
