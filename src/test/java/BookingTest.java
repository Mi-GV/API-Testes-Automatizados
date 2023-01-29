import Entities.Booking;
import Entities.BookingDates;
import Entities.BookingResponse;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class BookingTest {
    public static Faker faker;
    private static RequestSpecification request;
    private static Booking booking;

    @BeforeAll
    public static void Setup(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        faker = new Faker();

        BookingDates bookingDates = new BookingDates("2018-01-02","2018-01-03");
        booking = new Booking(
                faker.name().firstName(),
                faker.name().lastName(),
                (float)faker.number().randomDouble(2,50,100000),
                true,bookingDates,
                "");
        RestAssured.filters(new RequestLoggingFilter(),new RequestLoggingFilter(), new ErrorLoggingFilter());
    }

    @BeforeEach
    void setRequest(){
        request = given().config(RestAssured.config().logConfig(new LogConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .auth().preemptive().basic("admin","password123");

    }

    @Test
    public void getAllBookings_returnOk(){
        Response response = request
                .when()
                .get("/booking")
                .then()
                .extract()
                .response();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200,response.statusCode());
    }

    @Test
    public void getBookingByID_returnOk(){
        Response response = request
                .when()
                .pathParam("id", "1")
                .get("/booking/{id}")
                .then()
                .extract()
                .response();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200,response.statusCode());
    }

    @Test
    public void getAllBookingsByUserFirstName_BookingExists_returnOk(){
        request
                .when()
                .queryParam("firstName","Carol")
                .get("/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("results",hasSize(greaterThan(0)));

    }

    @Test
    public void CreateBooking_WithValidData_returnOk(){
        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
              .contentType(ContentType.JSON)
                 .when()
                 .body(booking)
                 .post("/booking")
                 .then()
                 .assertThat()
                 .statusCode(200)
                 .and()
                 .body(matchesJsonSchemaInClasspath("createBookingRequestSchema.json"))
                 .contentType(ContentType.JSON);

    }

    @Test
    public void UpdateBooking_WithValidData_returnOk(){
        request
                .when()
                .pathParam("id", "1")
                .body(booking)
                .put("/booking/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(matchesJsonSchemaInClasspath("createBookingRequestSchema.json"))
                .contentType(ContentType.JSON);
    }

    @Test
    public void UpdatePartialBooking_WithValidData_returnOk(){
        request
                .when()
                .pathParam("id", "1")
                .body(booking)
                .patch("/booking/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(matchesJsonSchemaInClasspath("createBookingRequestSchema.json"))
                .contentType(ContentType.JSON);
    }

    @Test
    public void DeleteBooking_returnOk() {
        Response response = given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .when()
                .body(booking)
                .post("/booking");

        response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(matchesJsonSchemaInClasspath("createBookingRequestSchema.json"))
                .contentType(ContentType.JSON);

        BookingResponse bookingCreated = response.as(BookingResponse.class);

        request
                .when()
                .pathParam("id", bookingCreated.getBookingid())
                .body(booking)
                .delete("/booking/{id}")
                .then()
                .assertThat()
                .statusCode(201);
    }

}

