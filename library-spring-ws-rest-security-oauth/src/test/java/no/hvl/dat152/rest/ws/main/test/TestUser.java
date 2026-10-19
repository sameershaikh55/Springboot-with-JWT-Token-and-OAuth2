package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestUser {

	private static final String API_ROOT = "http://localhost:8090/elibrary/api/v1";

	@Value("${admin.token.test}")		// user2
	private String ADMIN_TOKEN;			

	@Value("${user.token.test}")		// user1
	private String USER_TOKEN;

	@Value("${user3.token.test}")		// user3
	private String USER3_TOKEN;
	
	@Value("${user4.token.test}")		// user4
	private String USER4_TOKEN;

	@DisplayName("JUnit test for @GetMapping(/users) endpoint")
	@Test
	public void getAllUsers_thenOK() {
		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/users");
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("userid").size() > 0);
	}

	@DisplayName("JUnit test for @GetMapping(/users) endpoint")
	@Test
	public void getAllUsers_USER_ROLE_thenForbidden() {
		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users");
		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode());
	}

	@DisplayName("JUnit test for @GetMapping(/users/{id}) endpoint")
	@Test
	public void getUserById_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER3_TOKEN)
				.get(API_ROOT + "/users/3");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("3", response.jsonPath().get("userid").toString());
	}

	@DisplayName("JUnit test for @GetMapping(/users/{id}/orders) endpoint")
	@Test
	public void getOrdersOfUser_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users/1/orders");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}

	@DisplayName("JUnit test for @PostMapping(/users/{id}/orders) endpoint")
	@Test
	public void createOrderForUser_thenOK() throws UserNotFoundException {

		String order = orderData();

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(order)
				.post(API_ROOT + "/users/{id}/orders", 2);

		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	}

	@DisplayName("JUnit test for @DeleteMapping(/users/{id}) endpoint")
	@Test
	public void deleteUserById_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER4_TOKEN)
				.get(API_ROOT + "/users/4");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("4", response.jsonPath().get("userid").toString());
		
		Response resp = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.delete(API_ROOT + "/users/4");

		assertTrue(resp.getStatusCode() == HttpStatus.OK.value()
				|| resp.getStatusCode() == HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("JUnit test for @GetMapping(/users/{uid}/orders/{oid}) endpoint")
	@Test
	public void getUserOrder_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users/1/orders/1");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("1", response.jsonPath().get("id").toString());
	}

	@DisplayName("JUnit test for @DeleteMapping(/users/{uid}/orders/{oid}) endpoint")
	@Test
	public void deleteUserOrder_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.delete(API_ROOT + "/users/2/orders/3");

		assertTrue(response.getStatusCode() == HttpStatus.OK.value()
				|| response.getStatusCode() == HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("JUnit test for @GetMapping(/users/{uid}) endpoint")
	@Test
	public void getUserWrongAccess_thenForbidden() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER3_TOKEN)
				.get(API_ROOT + "/users/1");

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode());
	}

	private String orderData() {

		String json = "{\n"
				+ "    \"isbn\":\"rstuv1540\",\n"
				+ "    \"expiry\":\"2026-12-30\"\n"
				+ "}";

		return json;
	}
}