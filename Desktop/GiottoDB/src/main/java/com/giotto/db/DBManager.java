package com.giotto.db;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.giotto.things.*;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;
public class DBManager {

//	private final static String DB_ADDRESS = "52.0.136.240";
	private final static String DB_ADDRESS = "0.0.0.0";
	private final static int PORT = 27017;
	
	/** 
	 * 
	 * DB Method for insertion
	 * 
	 * @param new thing data
	 * */	
	@SuppressWarnings("resource")
	public static Object insert(String dbTitle, Thing thing) throws Exception{
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		MongoClient m = new MongoClient(DB_ADDRESS,PORT); //AWS
		MongoDatabase db = m.getDatabase("Giotto");		
		MongoCollection<Document> people = db.getCollection(dbTitle);
		Document queryDoc = makeDoc(thing);
		Document result = db.getCollection(dbTitle).find(queryDoc).first();
		if (result != null) return ((ObjectId) result.get("_id")).toHexString();

		people.insertOne(queryDoc);

		m.close();
		return insert(dbTitle, thing);
	}
	

	/** 
	 * 
	 * Counts the number of elements in the given database
	 * 
	 * @param the name of the database
	 * */
	public static long count(String name){
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		MongoClient m = new MongoClient(DB_ADDRESS,PORT);
		MongoDatabase db = m.getDatabase("Giotto");
		long count = db.getCollection(name).count();
		m.close();
		return count;
	}
	
	public static String[] search(String DBTitle, HashMap<String, Object> map) throws Exception{
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		MongoClient m = new MongoClient(DB_ADDRESS,PORT);
		MongoDatabase db = m.getDatabase("Giotto");
		
		Document queryDoc = new Document();
		for (String key : map.keySet()) {
			if (key.equals("location")) {
				Object s = new Location(query("Location", new Location(JSON.serialize(map.get(key)))));
				queryDoc.append(key, s.toString());
			}
			else queryDoc.append(key, map.get(key));
		}
		FindIterable<Document> d = db.getCollection(DBTitle).find(queryDoc);
		
		int count = (int) db.getCollection(DBTitle).count(queryDoc);
		
		String[] result = new String[count];
		for (int i = 0; i < count ; i++){
			result[i] = (d.skip(i).iterator().next()).toJson();
		}
		m.close();
		return result;
	}
	
	public static Object authenticate(String DBTitle, String uID) throws Exception{
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		MongoClient m = new MongoClient(DB_ADDRESS,PORT);
		MongoDatabase db = m.getDatabase("Giotto");
		
		BasicDBObject queryDoc = new BasicDBObject();
		queryDoc.put("_id", new ObjectId(uID));
		
		FindIterable<Document> d = db.getCollection(DBTitle).find(queryDoc);
		
		if (d.first() != null) return d.first().get("authentication_level");
		return null;
	}
	
	
	
	/** 
	 * 
	 * DB method for querying thing based on exact data
	 * 
	 * @param new location data
	 * @throws UnknownHostException 
	 * */
	public static String query(String name, Thing thing) throws UnknownHostException{
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		MongoClient m = new MongoClient(DB_ADDRESS,PORT); //AWS
		MongoDatabase db = m.getDatabase("Giotto");
		Document queryDoc = makeDoc(thing);
		Document result = db.getCollection(name).find(queryDoc).first();
		if (result == null) {
			m.close();
			return null;
		}
		String output = JSON.serialize(result); 
		m.close();
		return output;
	}
	
	
	public static void update(String dbTitle, String id, String key, String value) throws UnknownHostException{
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		
		MongoClient m = new MongoClient(DB_ADDRESS,PORT); //AWS
		System.out.println("Updating " + dbTitle + "...");
		MongoDatabase db = m.getDatabase("Giotto");
		Document queryDoc = new Document();
		queryDoc.append("_id", new ObjectId(id));
		Document target = db.getCollection(dbTitle).find(queryDoc).first();
		target.put(key, value);
		db.getCollection(dbTitle).findOneAndReplace(queryDoc, target);
		m.close();
	}

	public static boolean delete(String dbTitle, Thing thing) throws UnknownHostException{
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		
		MongoClient m = new MongoClient(DB_ADDRESS,PORT); //AWS
		System.out.println("Deleting " + thing.getName() + "...");
		MongoDatabase db = m.getDatabase("Giotto");
		Document queryDoc = makeDoc(thing);
		
		MongoCollection<Document> dbCollection = db.getCollection(dbTitle);
		DeleteResult result = dbCollection.deleteMany(queryDoc);
		m.close();
		return (result.getDeletedCount() > 0);
	}
	
	private static Document makeDoc(Thing thing){
		Document queryDoc = new Document();
		for (String key : thing.getRequiredKeys()){
			if (key.equals("location"))	queryDoc.append(key, thing.getValue(key).toString());
			else queryDoc.append(key, thing.getValue(key));
		}
		return queryDoc;
	}
	
}
