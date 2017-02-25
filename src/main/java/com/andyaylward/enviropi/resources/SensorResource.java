package com.andyaylward.enviropi.resources;

import com.andyaylward.enviropi.auth.ApiKey;
import com.andyaylward.enviropi.data.SensorDataManager;
import com.andyaylward.enviropi.data.SensorRecord;
import com.google.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v1/sensors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {
  private final SensorDataManager manager;

  @Inject
  public SensorResource(SensorDataManager manager) {
    this.manager = manager;
  }

  @GET
  public List<SensorRecord> getRecords(@QueryParam("deviceId") long deviceId,
                                       @QueryParam("from") long from,
                                       @QueryParam("to") long to) {
    return manager.getEvents(deviceId, from, to);
  }

  @POST
  @ApiKey
  public void addRecord(SensorRecord record) {
    manager.insertEvent(record);
  }

  @POST
  @ApiKey
  @Path("expire")
  public void expire(@QueryParam("deviceId") long deviceId) {
    manager.expire(deviceId);
  }
}
