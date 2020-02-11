package rest;
/*
 * author benjaminp
 * version 1.0
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Movie;
import facades.MovieFacade;
import utils.EMF_Creator;

import javax.net.ssl.SSLEngineResult;
import javax.persistence.EntityManagerFactory;
import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/movie")
public class MovieResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/Week3",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final MovieFacade FACADE =  MovieFacade.getMovieFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response get() {
        return Response
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response
                .ok(FACADE.getAll())
                .build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCount() {
        return Response
                .ok(FACADE.getCount())
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") long id) {
        Movie movie = FACADE.getById(id);
        if(movie != null) {
            return Response
                    .ok(movie)
                    .build();
        } else {
            return Response
                    .noContent()
                    .build();
        }
    }

    @GET
    @Path("/populate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response populate() {
        FACADE.populate();
        return Response
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Path("name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@PathParam("name") String name) {
        return Response
                .ok(FACADE.getByName(name))
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMovie(Movie movie) {
        return Response
                .ok(FACADE.create(movie))
                .build();
    }
}
