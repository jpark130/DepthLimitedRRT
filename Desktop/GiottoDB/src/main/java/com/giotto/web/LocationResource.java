package com.giotto.web;

import java.net.UnknownHostException;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.DELETE;


import com.giotto.db.DBManager;
import com.giotto.things.Location;
import com.mongodb.util.JSON;

@Path("/location")
public class LocationResource {
	
  @GET
  @Path("/count")
  @Produces({"application/json"})
  public String count() throws UnknownHostException {
	  HashMap<String, Object> ret = new HashMap<String, Object>();
	  ret.put("count", DBManager.count("Location"));
	  return JSON.serialize(ret);	  
	  }
  
  
  
  @POST
  @Path("/update")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public void update(String updateInfo){
	  try {
		  HashMap<String, Object> map;
		  map = new ObjectMapper().readValue(updateInfo, HashMap.class);
		
		  DBManager.update("Location", (String) map.get("uid"), (String)map.get("key"), (String)map.get("value"));
	  } catch (Exception e) {
	      System.out.println(e);
	  }
  }
  
  
  @SuppressWarnings("unchecked")
  @POST
  @Path("/find")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public String getLocation(String jsonString){
	  try {
		  HashMap<String, Object> map = new ObjectMapper().readValue(jsonString, HashMap.class);
		  if (map.containsKey("_id")){
			  map.put("_id", new ObjectId((String)map.get("_id")));
		  }
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("query", jsonString);
		  ret.put("result", DBManager.search("Location_" + map.get("tag"), map));
	      return JSON.serialize(ret);	      
	  } catch (Exception e) {
		  System.out.println(e);
	  }
	  return "";
  }
  
  @POST
  @Path("/post")
  @Consumes({"application/json"})
  public String postLocation(String jsonString){
	  try {
		  Location l = new Location(jsonString);
		  System.out.println(l.getTag());

		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("method", "POST");
		  System.out.println(l.getTag());
		  ret.put("result", DBManager.insert("Location_"+ l.getTag(), l));
		  return JSON.serialize(ret);
	  } catch (Exception e) {
	      System.out.println(e);
	  }
	  return "";
  }
  
  @DELETE
  @Path("/delete")
  @Consumes({"application/json"})
  public String removeLocation(String jsonString){
	  try{
		  Location l = new Location(jsonString);
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("method", "DELETE");
		  ret.put("result", DBManager.delete("Location", l));
		  return JSON.serialize(ret);
	  } catch (Exception e){
		  System.out.println(e);
	  }
	  return "";
  }
}
