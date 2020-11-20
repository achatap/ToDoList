package application;

import static io.restassured.RestAssured.given;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class todotest {

	Set<String> taskId= new HashSet<String>();
	JSONObject jo;

	@Test(priority = 1)
	public void listingTasks() {

		Response res= given().
				when().
				get("http://127.0.0.1:5000/tasks");

		String body= res.body().asString();

		if(body.contains("completed") && body.contains("task") && body.contains("id")) {
			System.out.println("Proper reponse is received :");
			res.prettyPrint();

			res.then()
			.contentType(ContentType.JSON)
			.statusCode(200)
			.header("Server", "Werkzeug/1.0.1 Python/2.7.12");

			List<String> idList = res.jsonPath().getList("id");

			taskId.addAll(idList);
		}

		else System.out.println("Response body is having some issue");

	}

	@Parameters("taskID")
	@Test(priority = 2, dependsOnMethods = "listingTasks", enabled = true)
	public void getaSingleTask(String taskID) {

			Response res= given().
					when().
					get("http://127.0.0.1:5000/tasks/"+ taskID);

			String body= res.body().asString();

			if(body.contains("completed") && body.contains("task"))
			{
				res.then()
				.contentType(ContentType.JSON)
				.statusCode(200)
				.header("Server", "Werkzeug/1.0.1 Python/2.7.12");

				res.body().prettyPrint();
			}

			else System.out.println("Response body is having some issue");
		}

	@Parameters({"taskName", "status"})
	@Test(priority = 3, enabled = true)
	public void createTask(String taskName, String status) {
		
		jo =new JSONObject();
		jo.put("task", taskName);
		jo.put("completed", status);

		Response res= 
				given()
				.header("Content-Type", "application/json")
				.contentType(ContentType.JSON)
				.body(jo.toJSONString())
				.when()
				.post("http://127.0.0.1:5000/tasks");

		String taskID = res.jsonPath().getString("task_id");

		if(taskID!=null) {

			taskId.add(taskID);
			res.getBody().prettyPrint();
			res.then().statusCode(200);
			Assert.assertTrue(res.getBody().asString().contains("task_id"));

		}
		else {		 
			System.out.println("task ID is not generated due to some error");
			Assert.assertTrue(false);
		}		
	}

	@Parameters({"taskID", "taskName", "status"})
	@Test(priority = 4, dependsOnMethods = "listingTasks", enabled = true)
	public void modifyTask(String taskID, String taskName, String status) {

		jo =new JSONObject();	
		jo.put("task", taskName);
		jo.put("completed", status);

		Response res= 
				given()
				.header("Content-Type", "application/json")
				.contentType(ContentType.JSON)
				.body(jo.toJSONString())
				.when()
				.put("http://127.0.0.1:5000/tasks/"+ taskID);

		if(res.jsonPath().getString("task_id")!=null) {

			res.getBody().prettyPrint();
			res.then().statusCode(200);

		}
		else {		 
			System.out.println("task ID is not generated due to some error");
			Assert.assertTrue(false);
		}
	}		

	@Parameters("taskID")
	@Test(priority = 5, enabled = true)
	public void markTaskCompleted(String taskID) {

		for (String tid : taskId) {

			given()
			.header("Content-Type", "application/json")
			.contentType(ContentType.JSON)
			.when()
			.post("http://127.0.0.1:5000/tasks/"+ taskID+ "/completed")
			.then()
			.contentType(ContentType.JSON)
			.statusCode(200)
			.header("Server", "Werkzeug/1.0.1 Python/2.7.12");			
		}
	}

	@Parameters("taskID")
	@Test(priority = 6, enabled = true)
	public void markTaskInComplete(String taskID) {

			given()
				.header("Content-Type", "application/json")
				.contentType(ContentType.JSON)
			.when()
				.post("http://127.0.0.1:5000/tasks/"+ taskID+ "/incomplete")
			.then()
				.contentType(ContentType.JSON)
				.statusCode(200)
				.header("Server", "Werkzeug/1.0.1 Python/2.7.12");					
		}

	@Parameters("taskID")
	@Test(priority = 7, enabled = true)
	public void deleteExistingTask(String taskID) {

			given()
				.header("Content-Type", "application/json")
				.contentType(ContentType.JSON)
			.when()
				.delete("http://127.0.0.1:5000/tasks/"+ taskID)
			.then()
				.contentType(ContentType.JSON)
				.statusCode(200)
				.header("Server", "Werkzeug/1.0.1 Python/2.7.12");				
			}
	}
