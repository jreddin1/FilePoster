package com.xcira.fileposter;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;

public class RestClient {

	private static final String JSON = "application/json";
	private static final Integer MAX_CONNECTIONS = Integer.MAX_VALUE;
	private static final AsyncHttpClient asyncHttpClient = createAsyncHttpClient();
	
	private static List<Cookie> cookies = new ArrayList<Cookie>();
	
	public static Response post(String url, String body) throws Exception {

		Response response = createHeaders(asyncHttpClient.preparePost(url).setBody(body)).execute().get();

		setCookies(response.getCookies());
		
		return response;
	}
	
	public static Response put(String url, String body) throws Exception {
		
		Response response = createHeaders(asyncHttpClient.preparePut(url).setBody(body)).execute().get();

		setCookies(response.getCookies());
		
		return response;
	}
	
	private static void setCookies(List<Cookie> cookies) {
		
		if ( ! cookies.isEmpty()) {
			
			RestClient.cookies = cookies;
		}
	}
	
	private static BoundRequestBuilder createHeaders(BoundRequestBuilder boundRequestBuilder) {
		
		boundRequestBuilder.addHeader("accept", JSON);
		boundRequestBuilder.addHeader("content-type", JSON);
	
		for (Cookie cookie : cookies) {
			
			boundRequestBuilder.addCookie(cookie);
		}
		
		return boundRequestBuilder;
	}
	
	private static AsyncHttpClient createAsyncHttpClient() {
		
		Builder builder = new Builder();

		builder.setMaxConnections(MAX_CONNECTIONS);
		builder.setMaxConnectionsPerHost(MAX_CONNECTIONS);
		builder.setFollowRedirect(true);
		builder.setAcceptAnyCertificate(true);
		
		setSSLContext(builder);
		
		return new AsyncHttpClient(builder.build());
	}
	
	private static void setSSLContext(Builder builder) {
		
		SSLContext context;
		
		try {
			
			context = SSLContext.getInstance("SSL");
			context.init(null, createTrustManager(), null);
			
			builder.setHostnameVerifier(new HostNameVerifier());
			builder.setSSLContext(context);
		
		} catch (Exception exception) {

			exception.printStackTrace();
		}
	}
	
	private static TrustManager[] createTrustManager() {
		
		return new TrustManager[] {
				
				new X509TrustManager() {
			    	
					public X509Certificate[] getAcceptedIssuers(){ return new java.security.cert.X509Certificate[0]; }
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}
		};
	}
	
	public static class HostNameVerifier implements HostnameVerifier {
		
		public boolean verify(String urlHostname, String certHostname) {
			
			return true;
		}
		
		public boolean verify(String urlHost, SSLSession sslSession) {
			
			return true;
		}
	}
}
