package com.giotto.web;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import com.giotto.db.DBManager;
import com.giotto.things.Person;
import com.giotto.things.UserPreference;
import com.mongodb.util.JSON;
@Path("/people")
public class Resource {
	
  @GET
  @Path("/count")
  @Produces(MediaType.TEXT_PLAIN)
  public String count() throws UnknownHostException {
	  HashMap<String, Object> ret = new HashMap<String, Object>();
	  ret.put("count", DBManager.count("People"));
	  return JSON.serialize(ret);	  
  }
  
  
  @POST
  @Path("/preference/post")
  @Produces(MediaType.TEXT_PLAIN)
  public String postPreference(String jsonString) throws UnknownHostException {
	  try {
		  UserPreference preferenceInstance = new UserPreference(jsonString);
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("query", jsonString);
		  ret.put("result", DBManager.insert("UserPreference", preferenceInstance));
		  System.out.println(ret);
	      return JSON.serialize(ret);
	  } catch (Exception e) {
		  System.out.println(e);
	  }
	  return ""; 
  }
	
  @POST
  @Path("/preference/find")
  @Produces(MediaType.TEXT_PLAIN)
  public String getPreference(String jsonString) throws UnknownHostException {
	  try {
		  HashMap<String, Object> map = new ObjectMapper().readValue(jsonString, HashMap.class);
		  if (map.containsKey("_id")){
			  map.put("_id", new ObjectId((String)map.get("_id")));
		  }
		  String target = (String)map.get("target");
		  map.remove("target");
		  HashMap<String, Object> result = new ObjectMapper().readValue(DBManager.search("UserPreference", map)[0], HashMap.class);
		  HashMap<String, HashMap<String, ArrayList<String>>> preference = (HashMap<String, HashMap<String, ArrayList<String>>>) result.get("preference");
		  
		  for (String tier : preference.keySet()){
			  ArrayList<String> users = preference.get(tier).get("users");
			  for (int i = 0; i < users.size(); i++){
				  if (users.get(i).equals(target)){

					  HashMap<String, Object> ret = new HashMap<String, Object>();
					  ret.put("query", map);
					  ret.put("result", preference.get(tier).get("api_level"));
				      return JSON.serialize(ret);				  
				  }
			  }
		  }
		  
	  } catch (Exception e) {
		  System.out.println(e);
	  }
	  return ""; 
  }
  

  @POST
  @Path("/preference/update")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public void updatePreference(String updateInfo){
	  try {
		  HashMap<String, Object> map;
		  map = new ObjectMapper().readValue(updateInfo, HashMap.class);
		
		  DBManager.update("UserPreferene", (String) map.get("uid"), (String)map.get("key"), (String)map.get("value"));
	  } catch (Exception e) {
	      System.out.println(e);
	  }
	  
  }
  
	
  @POST
  @Path("/authenticate")
  @Produces(MediaType.TEXT_PLAIN)
  public String getAuthenticationLevel(String jsonString) throws Exception {
	  HashMap<String, Object> map;
	  map = new ObjectMapper().readValue(jsonString, HashMap.class);
	
	  HashMap<String, Object> ret = new HashMap<String, Object>();
	  ret.put("result", DBManager.authenticate("People", (String) map.get("uid")));
	  ret.put("query", map);
      return JSON.serialize(ret);
      
  }
  
  
  @SuppressWarnings("unchecked")
  @POST
  @Path("/find")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public String getPerson(String jsonString){
	  try {
		  HashMap<String, Object> map = new ObjectMapper().readValue(jsonString, HashMap.class);
		  if (map.containsKey("_id")){
			  map.put("_id", new ObjectId((String)map.get("_id")));
		  }
		  
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("query", map);
		  ret.put("result", DBManager.search("People", map));
	      return JSON.serialize(ret);
	  } catch (Exception e) {
		  System.out.println(e);
	  }
	  return "";
  }
  
  @POST
  @Path("/post")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public String addPerson(String person){
	  try {
		  Person p = new Person(person);
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("method", "POST");
		  ret.put("result", DBManager.insert("People", p));
		  return JSON.serialize(ret);
	  } catch (Exception e) {
	      System.out.println(e);
	  }
	  return "";
  }
  
  @POST
  @Path("/update")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public void update(String updateInfo){
	  try {
		  HashMap<String, Object> map;
		  map = new ObjectMapper().readValue(updateInfo, HashMap.class);
		
		  DBManager.update("People", (String) map.get("uid"), (String)map.get("key"), (String)map.get("value"));
	  } catch (Exception e) {
	      System.out.println(e);
	  }
  }
  
  @DELETE
  @Path("/delete")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public String removeLocation(String jsonString){
	  try{
		  Person l = new Person(jsonString);
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("method", "DELETE");
		  ret.put("result", DBManager.delete("People", l));
		  return JSON.serialize(ret);
	  } catch (Exception e){
		  System.out.println(e);
	  }
	  return "";
  }
}
