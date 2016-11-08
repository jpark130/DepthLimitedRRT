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
import com.giotto.things.GeneralType;
import com.mongodb.util.JSON;

@Path("/thing")
public class ThingResource {
	
  @GET
  @Path("/count")
  @Produces({"application/json"})
  public String count() throws UnknownHostException {
	  HashMap<String, Object> ret = new HashMap<String, Object>();
	  ret.put("count", DBManager.count("Thing"));
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
		
		  DBManager.update("Thing", (String) map.get("uid"), (String)map.get("key"), (String)map.get("value"));
	  } catch (Exception e) {
	      System.out.println(e);
	  }
  }
  
  
  @SuppressWarnings("unchecked")
  @POST
  @Path("/find")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public String getThing(String jsonString){
	  try {
		  HashMap<String, Object> map = new ObjectMapper().readValue(jsonString, HashMap.class);
		  
		  if (map.containsKey("_id")){
			  map.put("_id", new ObjectId((String)map.get("_id")));
		  }
		  
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("query", jsonString);
		  ret.put("result", DBManager.search("Thing", map));
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
		  GeneralType thing = new GeneralType(jsonString);
		
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("method", "POST");
		  ret.put("result", DBManager.insert("Thing", thing));
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
		  GeneralType thing = new GeneralType(jsonString);
		  
		  HashMap<String, Object> ret = new HashMap<String, Object>();
		  ret.put("method", "DELETE");
		  ret.put("result", DBManager.delete("Thing", thing));
		  return JSON.serialize(ret);
	  } catch (Exception e){
		  System.out.println(e);
	  }
	  return "";
  }
}
