package com.xcira.fileposter;

import java.util.HashMap;
import java.util.Map;

import com.ning.http.client.Response;

public class Service {

	private String companyId;
	private String username;
	private String password;
	private String url;
	private String body;
	private boolean secondAttempt = false;
	
	public Service(String companyId, String username, String password, String url, String body) {
		
		this.companyId = companyId;
		this.username = username;
		this.password = password;
		this.url = url;
		this.body = body;
	}
	
	public Response sendRequest() throws Exception {
		
		return handleResponse(RestClient.post(url, body));
	}
	
	protected Response handleResponse(Response response) throws Exception {
		
		if (response.getStatusCode() == 401 && ! secondAttempt) {
			
			signIn();
				
			secondAttempt = true;
			Response secondAttemptResponse = sendRequest();
			secondAttempt = false;
			
			return secondAttemptResponse;
			
		}

		return response;
	}
	
	protected void signIn() throws Exception {

		Map<String, String> companyUser = new HashMap<String, String>();
		
		companyUser.put("userName", username);
		companyUser.put("password", password);

		handleResponse(RestClient.put("company/" + companyId + "/user/signIn", JSONUtil.toJson(companyUser)));
	}
}