package it.baldan.garmin.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Activity
{
  Long activityId;
  String activityName;

  public String getActivityName()
  {
    return this.activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public Long getActivityId() {
    return this.activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }
}