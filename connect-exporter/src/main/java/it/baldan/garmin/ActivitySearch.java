package it.baldan.garmin;

import it.baldan.garmin.dto.Activity;
import it.baldan.garmin.dto.ActivitySearchServiceResultsWrapper;
import it.baldan.garmin.dto.ActivityWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ActivitySearch {
	private static final String ACTIVITIES_SEARCH = "http://connect.garmin.com/proxy/activity-search-service-1.2/json";
	private Client connectClient;

	public ActivitySearch(Client connectClient) {
		this.connectClient = connectClient;
	}

	private Collection<Activity> getActivities(String activityType) {
		WebResource service = this.connectClient.resource(ACTIVITIES_SEARCH);
		WebResource path = service.path("activities");
		if (StringUtils.isNotEmpty(activityType)) {
			path = path.queryParam("activityType", activityType);
		}
		ArrayList<Activity> result = new ArrayList<Activity>();
		int currentPage = 1;
		ActivitySearchServiceResultsWrapper response;
		do {
			response = (ActivitySearchServiceResultsWrapper) path
					.queryParam("currentPage", String.valueOf(currentPage++))
					.accept(new MediaType[] { MediaType.APPLICATION_JSON_TYPE })
					.get(ActivitySearchServiceResultsWrapper.class);

			Collection<ActivityWrapper> activities = response.getResults()
					.getActivities();

			for (ActivityWrapper activityWrapper : activities)
				result.add(activityWrapper.getActivity());
		} while (response.getResults().getTotalPages().intValue() > currentPage);

		return result;
	}

	public static void main(String[] args) throws IOException {
		ConnectLogin cl = new ConnectLogin();
		Client login = cl.login(args[0], args[1]);
		ActivitySearch search = new ActivitySearch(login);
		Collection<Activity> activities = search
				.getActivities(args[2]);

		for (Activity activity : activities) {
			String activityName = activity.getActivityName();
			System.out.println(activityName);

			Long activityId = activity.getActivityId();
			WebResource resource = login
					.resource(String
							.format("http://connect.garmin.com/proxy/activity-service-1.1/gpx/activity/%d?full=true",
									new Object[] { activityId }));
			File file = (File) resource.accept(
					new MediaType[] { MediaType.APPLICATION_JSON_TYPE,
							MediaType.APPLICATION_XML_TYPE,
							MediaType.TEXT_HTML_TYPE }).get(File.class);
			FileUtils.copyFile(file, new File(args[3] + "/" + activityName
					+ ".gpx"));
		}
	}
}