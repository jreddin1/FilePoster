package com.xcira.fileposter;

import java.util.HashMap;
import java.util.Map;

import com.ning.http.client.Response;

public class Service {

	private String companyId;
	private String email;
	private String password;
	private String url;
	private String body;
	private String baseUrl;
	private boolean secondAttempt = false;
	
	public Service(String companyId, String email, String password, String url, String baseUrl, String body) {
		
		this.companyId = companyId;
		this.email = email;
		this.password = password;
		this.url = url;
		this.baseUrl = baseUrl;
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
		
		companyUser.put("email", email);
		companyUser.put("password", password);

		handleResponse(RestClient.put(baseUrl + "/services/company/" + companyId + "/user/signIn", JSONUtil.toJson(companyUser)));
	}
}