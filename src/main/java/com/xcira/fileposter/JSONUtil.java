package com.xcira.fileposter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public static String toJson(Object object) throws Exception {
		
		return objectMapper.writeValueAsString(object);
	}
}
