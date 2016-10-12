package com.xcira.fileposter;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ning.http.client.Response;

public class FilePoster {

	private static final String PROPERTIES_FILE_NAME = "./FilePoster.properties";
	
	private static String inputFolder;
	private static String outputFolder;
	private static String companyId;
	private static String username;
	private static String password;
	private static String url;

	public static void main(String[] args) throws Exception {

		initializeProperties();

		while (true) {

			try {
				
				File nextFile = getNextInputFile();
				
				if (nextFile != null) {
					
					Response response = post(toString(nextFile));
					
					saveResponse(response);
					
					if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
						
						nextFile.delete();
						
					} else {
						
						Thread.sleep(1000);
					}
				}
				
				Thread.sleep(1);
				
			} catch (Exception exception) {

				exception.printStackTrace();
			}
		}
	}
	
	private static void initializeProperties() throws Exception {
		
		Properties properties = new Properties();
		
		InputStream inputStream = FilePoster.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
		
		if (inputStream == null) {
			
			throw new Exception("Properties File Not Found");
		}
		
		properties.load(inputStream);
		
		inputFolder = properties.getProperty("INPUT_FOLDER");
		outputFolder = properties.getProperty("OUTPUT_FOLDER");
		url = properties.getProperty("URL");
		username = properties.getProperty("USERNAME");
		password = properties.getProperty("PASSWORD");
	}
	
	private static Response post(String file) throws Exception {
		
		return new Service(companyId, username, password, url, JSONUtil.toJson(createParameters(file))).sendRequest();
	}
	
	private static File getNextInputFile() throws Exception {
		
		File[] files = getInputFiles();
		
		if (files.length > 0) {

			return files[0];
		
		} else {
			
			return null;
		}
	}
	
	private static File[] getInputFiles() {

		File[] files = new File(inputFolder).listFiles();
		
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File f1, File f2) {

				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
		
		return files;
	}
	
	private static void saveResponse(Response response) throws Exception {
		
		PrintWriter printWriter = new PrintWriter(outputFolder + "/" + System.currentTimeMillis() + "-" + response.getStatusCode() + ".txt");
		
		printWriter.print(response.getResponseBody());
		
		printWriter.close();
	}
	
	private static Map<String, Object> createParameters(String xml) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("xml", xml);
		
		return parameters;
	}
	
	private static String toString(File file) throws Exception {
		
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), Charset.defaultCharset());
	}
}
