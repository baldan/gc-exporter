package it.baldan.garmin;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.representation.Form;

public class ConnectLogin {
	private static final String CONNECT_SIGNIN = "https://connect.garmin.com/signin";
	private Client connectClient;

	private Client getConnectClient() {
		if (this.connectClient == null) {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(
					"com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
			this.connectClient = Client.create(clientConfig);
			this.connectClient.addFilter(new ClientFilter() {
				private ArrayList<Object> cookies;

				public ClientResponse handle(ClientRequest request)
						throws ClientHandlerException {
					if (this.cookies != null) {
						request.getHeaders().put("Cookie", this.cookies);
					}
					ClientResponse response = getNext().handle(request);

					if (response.getCookies() != null) {
						if (this.cookies == null) {
							this.cookies = new ArrayList();
						}

						this.cookies.addAll(response.getCookies());
					}
					return response;
				}
			});
		}
		return this.connectClient;
	}

	public Client login(String username, String password) {
		getConnectClient().resource("http://connect.garmin.com").get(
				ClientResponse.class);
		getConnectClient().resource(CONNECT_SIGNIN).get(ClientResponse.class);

		Form formData = new Form();
		formData.add("login", "login");
		formData.add("login:loginUsernameField", username);
		formData.add("login:password", password);
		formData.add("login:signInButton", "Accedi");
		formData.add("javax.faces.ViewState", "j_id2");

		ClientResponse response = (ClientResponse) getConnectClient()
				.resource(CONNECT_SIGNIN)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.post(ClientResponse.class, formData);

		if (response.getClientResponseStatus() != ClientResponse.Status.FOUND) {
			throw new RuntimeException("Login fallita!");
		}

		return getConnectClient();
	}

	public static void main(String[] args) {
		ConnectLogin cl = new ConnectLogin();
		Client login = cl.login(args[0], args[1]);
	}
}
