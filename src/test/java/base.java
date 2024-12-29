
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import groovy.lang.Newify;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class base {

	public static void main(String[] args) {
		io.restassured.response.Response response;
		String header = "Content-Type:multipart/form-data";
		String requestBody = "chatType:singlechat" 
				+ ",messageType:text" 
				+ ",toUser:917305466010" 
				+ ",message:hello";

		get_request("post", "https://api-uikit-qa.contus.us/api/v1/chat/sendmessage", header, requestBody);
	}

	public static Map<String, Object> multipart(String requestBody) {
		Map<String, Object> multipart = new HashMap<>();
		String[] multipart_pairs = requestBody.split(",");
		for (String pair : multipart_pairs) {
			String[] keyValue = pair.split(":");
			if (keyValue.length == 2) {
				if (keyValue[0].toLowerCase().contains("file")) {
					multipart.put(keyValue[0], new File(keyValue[1]));
				} else {
					multipart.put(keyValue[0], keyValue[1]);
				}
			}
		}
		return multipart;
	}

	public static Object get_request(String requestType, String endpoint, String header, String requestBody) {

		Map<String, Object> Headers = multipart(header);

		switch (requestType.toLowerCase()) {
		case "get":
			return given().headers(Headers).log().all().when().get(endpoint).then().log().all();
		case "post": {
			if (header.toLowerCase().contains("multipart")) {
				Map<String, Object> multipart = multipart(requestBody);
				return given().headers(Headers).contentType("multipart/form-data")
						.multiPart("file", "example.txt", "File content goes here".getBytes()).formParams(multipart)
						.log().all().post(endpoint).then().log().all();

			} else if (header.toLowerCase().contains("json")) {

				return given().headers(Headers).body(requestBody).log().all().when().post(endpoint).then().log().all();
			} else {
				System.out.println("unsupported");
			}

		}
		case "delete":
			return given().headers(Headers).body(requestBody).log().all().when().delete(endpoint).then().log().all();
		default:
			return null;
		}

	}
}
