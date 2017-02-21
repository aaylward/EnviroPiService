package com.andyaylward.enviropi.resources;

import com.andyaylward.enviropi.data.SensorDataManager;
import com.andyaylward.enviropi.data.SensorRecord;
import com.google.common.base.Preconditions;
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
  private static final long ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;

  private final SensorDataManager manager;

  @Inject
  public SensorResource(SensorDataManager manager) {
    this.manager = manager;
  }

  @GET
  public List<SensorRecord> getRecords(@QueryParam("from") long from, @QueryParam("to") long to) {
    validateRange(from, to);
    return manager.getEvents(from, to);
  }

  @POST
  public void addRecord(SensorRecord record) {
    manager.insertEvent(record);
  }

  private void validateRange(long from, long to) {
    Preconditions.checkArgument(to >= from, "from must be before to");
    Preconditions.checkArgument(to - from <= ONE_DAY_MILLIS, "maximum range is one day");
  }
}