package rest;
/*
 * author benjaminp
 * version 1.0
 */

import com.google.gson.Gson;
import entities.Movie;
import org.hamcrest.Matchers;
import utils.EMF_Creator;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

import io.restassured.parsing.Parser;

import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;


public class MovieResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Movie r1, r2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        r1 = new Movie("Movie1",1990,new String[]{"Peter","Poul"});
        r2 = new Movie("Movie2",2020,new String[]{"Lars","Lotte"});
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Movie.Truncate").executeUpdate();
            em.persist(r1);
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.persist(r2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        given().when().get("/movie").then().statusCode(200);
    }

    @Test
    public void testCreate() {
        Movie movie = new Movie("Movie3",2131,new String[]{"A","B"});
        given().contentType("application/json")
                .body(new Gson().toJson(movie))
                .post("/movie")
                .then()
                    .statusCode(200)
                    .body("id", equalTo(3));
    }

    @Test
    public void testCount() {
        when()
                .get("/movie/count")
                .then()
                    .statusCode(200)
                    .extract().asString().equals("2");
    }

    @Test
    public void testGetByName_with_no_results() {
        when()
                .get("/movie/name/{name}","This name is not a title")
                .then()
                    .statusCode(200)
                    .body("", Matchers.hasSize(0));
    }

    @Test
    public void testGetByName() {
        when()
                .get("/movie/name/{name}","Movie1")
                .then()
                    .statusCode(200)
                    .body("name[0]", equalTo("Movie1"),
                            "year[0]", equalTo(1990),
                            "actors[0]", hasItems("Peter", "Poul"));
    }

    @Test
    public void testGetAll() {
        when()
                .get("/movie/all")
                .then()
                    .statusCode(200)
                    .body("name", hasItems("Movie1","Movie2"));
    }

    @Test
    public void testGetById() {
        when()
                .get("/movie/{id}",1L)
                .then()
                    .statusCode(200)
                    .body("name", equalTo("Movie1"),
                            "year", equalTo(1990),
                            "actors", hasItems("Peter","Poul"));

    }

    @Test
    public void testGetById_invalid_id() {
        when()
                .get("/movie/{id}",1413L)
                .then()
                    .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
