package facades;
/*
 * author benjaminp
 * version 1.0
 */

import entities.Movie;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MovieFacadeTest {

    private static EntityManagerFactory emf;
    private static MovieFacade moviefacade;

    public MovieFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                Strategy.CREATE);
        moviefacade = MovieFacade.getMovieFacade(emf);
    }

    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        moviefacade = MovieFacade.getMovieFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
        // Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Movie.Truncate").executeUpdate();
            em.persist(new Movie("Movie1",1990,new String[]{"Peter","Poul"}));
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.persist(new Movie("Movie2",2020,new String[]{"Lars","Lotte"}));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCreate() {
        Movie movie = new Movie("Movie3", 1213, new String[]{"Bo", "Lis"});
        moviefacade.create(movie);
        assertEquals(new Long(3), movie.getId());
    }

    @Test
    public void testGetAll() {
        List<Movie> movies = moviefacade.getAll();
        assertEquals(2, movies.size());
        assertEquals("Movie1",movies.get(0).getName());
        assertEquals("Movie2",movies.get(1).getName());
    }

    @Test
    public void testGetByName_invalid_name() {
        List<Movie> movies = moviefacade.getByName("This is not a movie title");
        assertEquals(0, movies.size());
    }

    @Test
    public void testGetByName() {
        List<Movie> movies = moviefacade.getByName("Movie1");
        assertEquals(1, movies.size());
        Movie movie = movies.get(0);
        assertEquals("Movie1",movie.getName());
        assertEquals(1990,movie.getYear());
        assertArrayEquals(new String[]{"Peter","Poul"}, movie.getActors());
    }

    @Test
    public void testGetById_invalid_id() {
        Movie movie = moviefacade.getById(999999L);
        assertNull(movie);
    }

    @Test
    public void testGetById() {
        Movie movie = moviefacade.getById(1L);
        assertEquals("Movie1",movie.getName());
        assertEquals(1990, movie.getYear());
        assertArrayEquals(new String[]{"Peter","Poul"}, movie.getActors());
    }

    @Test
    public void testAFacadeMethod() {
        assertEquals(2, moviefacade.getCount());
    }
}
