# FilePoster

FilePoster is used to post files from a specified input folder to a specified http endpoint sequentially. File list to post is sorted by last modified time. Responses from the endpoint are saved in files in the specified output folder. File Poster is designed to work with Cubed platform and requires authentication credentials to be configured in properties file.

##### FilePoster.properties should be located in the same folder as the application and configured with the following properties:

**INPUT_FOLDER** - Full file path of the input folder to post files from  
**OUTPUT_FOLDER** - Full file path of the output folder to save responses to  
**URL** - Full url of the http endpoint to post files to  
**COMPANY_ID** - Provided company id for credential purposes  
**EMAIL** - Provided email for credential purposes  
**PASSWORD** - Provided password for credential purposes  

**Example properties file:**

```
INPUT_FOLDER=C:/Users/User/Desktop/FilePoster/Input
OUTPUT_FOLDER=C:/Users/User/Desktop/FilePoster/Output
URL=http://exampleUrl.com/services/scriptConfiguration/exampleScript/run
COMPANY_ID=8
EMAIL=example@example.com
PASSWORD=example
```

##### To start file poster, navigate to directory and run:
```
java -jar FilePoster.jar
```

**Jar downloads are found under releases**
