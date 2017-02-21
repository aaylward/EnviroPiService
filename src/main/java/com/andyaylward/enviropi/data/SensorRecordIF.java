package com.andyaylward.enviropi.data;

import com.muchq.immutables.MoonStyle;
import org.immutables.value.Value.Immutable;

@Immutable
@MoonStyle
public interface SensorRecordIF {
  long getTime();
  long getDeviceId();
  double getTemperature();
  double getPressure();
  double getLight();
}
