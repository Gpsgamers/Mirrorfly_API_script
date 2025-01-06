
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import groovy.lang.Newify;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class base {

	public static void main(String[] args) {
		io.restassured.response.Response response;
		String header = "Content-Type:application/json";
		String requestBody = "{ \"password\": \"lEHcFXIAp47mIEv\", \"username\": \"917358337102\", \"type\": \"\" }";
		//(issue type , header , endpoints ,Queryparam , requestbody )
		get_request("post",
				header,
				"https://api-uikit-qa.contus.us/api/v1/login",
				"",
				requestBody);

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

	public static Object get_request(String requestType, String header, String endpoint, String Queryparam,
			String requestBody) {

		Map<String, Object> Headers = multipart(header);

		switch (requestType.toLowerCase()) {
		case "get": {
			return given().headers(Headers).log().all().when().get(endpoint+Queryparam).then().log().all();
		}
		case "post": {
			if (header.toLowerCase().contains("multipart")) {
				Map<String, Object> multipart = multipart(requestBody);
				RequestSpecification request = given().headers(Headers);

				multipart.forEach((key, value) -> {
					if (value instanceof File) {
						request.multiPart(key, (File) value);
					} else {
						request.multiPart(key, (String) value);
					}
				});

				return request.log().all().post(endpoint+Queryparam).then().log().all();

			} else if (header.toLowerCase().contains("json")) {

				return given().headers(Headers).body(requestBody).log().all().when().post(endpoint+Queryparam).then().log().all();
			} else {
				System.out.println("unsupported");
				return nullValue();
			}

		}
		case "delete": {
			return given().headers(Headers).body(requestBody).log().all().when().delete(endpoint+Queryparam).then().log().all();
		}
		default:
			return null;
		}

	}
}
