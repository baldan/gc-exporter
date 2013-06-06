package it.baldan.garmin.dto;

import java.util.Collection;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivitySearchServiceResults
{
  Collection<ActivityWrapper> activities;
  private Integer totalPages;

  public Integer getTotalPages()
  {
    return this.totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Collection<ActivityWrapper> getActivities() {
    return this.activities;
  }

  public void setActivities(Collection<ActivityWrapper> activities) {
    this.activities = activities;
  }
}
