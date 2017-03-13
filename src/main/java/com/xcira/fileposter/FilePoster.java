package com.xcira.fileposter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ning.http.client.Response;

public class FilePoster {

	private static final String PROPERTIES_FILE_NAME = "./FilePoster.properties";
	private static final Integer NUM_THREADS = 2;
	
	private static String inputFolder;
	private static String outputFolder;
	private static String errorFolder;
	private static String companyId;
	private static String email;
	private static String password;
	private static String url;

	public static void main(String[] args) throws Exception {

		ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
		
		initializeProperties();
		
		for (Integer i = 0; i < NUM_THREADS; i++) {
			
			executorService.execute(new Worker());
		}
	}
	
	private static class FileInfo {
		
		public String fileName;
		public String content;
		
		public FileInfo(String fileName, String content) {
			
			this.fileName = fileName;
			this.content = content;
		}
	}
	
	private static class Worker implements Runnable {

		@Override
		public void run() {
			
			while (true) {

				FileInfo nextFile = null;
				
				try {
					
					nextFile = getNextInputFile();
					
					if (nextFile != null) {
						
						Response response = post(nextFile.content);
						
						saveResponse(response);
						
						if (response.getStatusCode() != 200 && response.getStatusCode() != 204) {
							
							copyToErrorFolder(nextFile);
						}
					}
					
					Thread.sleep(1);
					
				} catch (Exception exception) {

					exception.printStackTrace();
				}
			}
		}
	}
	
	private static void initializeProperties() throws Exception {
		
		Properties properties = new Properties();

		properties.load(new FileInputStream(PROPERTIES_FILE_NAME));
		
		inputFolder = properties.getProperty("INPUT_FOLDER");
		outputFolder = properties.getProperty("OUTPUT_FOLDER");
		errorFolder = properties.getProperty("ERROR_FOLDER");
		url = properties.getProperty("URL");
		email = properties.getProperty("EMAIL");
		password = properties.getProperty("PASSWORD");
		companyId = properties.getProperty("COMPANY_ID");
	}
	
	private static Response post(String file) throws Exception {

		return new Service(companyId, email, password, url, JSONUtil.toJson(createParameters(file))).sendRequest();
	}
	
	private static synchronized FileInfo getNextInputFile() throws Exception {
		
		File[] files = getInputFiles();
		
		if (files.length > 0) {

			File file = files[0];
			FileInfo fileInfo = new FileInfo(file.getName(), getContent(file));
			
			file.delete();
			
			return fileInfo;
		
		} else {
			
			return null;
		}
	}
	
	private static File[] getInputFiles() throws Exception {

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
	
	private static void copyToErrorFolder(FileInfo fileInfo) throws Exception {
		
		writeToFile(errorFolder + "/" + fileInfo.fileName, fileInfo.content);
	}
	
	private static Map<String, Object> createParameters(String xml) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("xml", xml);
		
		return parameters;
	}
	
	private static void writeToFile(String filePath, String content) throws Exception {

		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
		
		bufferedWriter.write(content);
		bufferedWriter.close();
	}
	
	private static String getContent(File file) throws Exception {
		
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), Charset.defaultCharset());
	}
}
