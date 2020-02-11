package facades;
/*
 * author benjaminp
 * version 1.0
 */

import entities.Movie;
import utils.EMF_Creator;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;


public class MovieFacade {

    private static MovieFacade instance;
    private static EntityManagerFactory emf;

    private MovieFacade() {
    }

    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static MovieFacade getMovieFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MovieFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public long getCount() {
        EntityManager entityManager = getEntityManager();
        int count = -1;
        try {
            count = Integer.parseInt(entityManager.createQuery("SELECT count(m) FROM Movie m", Long.class).getSingleResult().toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return count;
    }

    public List<Movie> getAll() {
        EntityManager entityManager = getEntityManager();
        List<Movie> movies = null;
        try {
            movies = entityManager.createNamedQuery("Movie.getAll",Movie.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return movies;
    }

    public List<Movie> getByName(String name) {
        EntityManager entityManager = getEntityManager();
        List<Movie> movies = null;
        try {
            TypedQuery<Movie> query = entityManager.createNamedQuery("Movie.getByName", Movie.class);
            query.setParameter("name", name);
            movies = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return movies;
    }

    public Movie getById(long id) {
        EntityManager em = getEntityManager();
        Movie result = null;
        try {
            result = em.find(Movie.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            em.close();
        }
        return result;
    }

    public Movie create(Movie movie) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return movie;
    }

    public void populate() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Movie.deleteAllRows").executeUpdate();
            em.persist(new Movie("Movie 1", 1990, new String[]{"Peter", "Lise"}));
            em.persist(new Movie("Movie 2", 2020, new String[]{"Lars", "Lotte"}));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    public static void main(String[] args) {
        EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/Week3",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        MovieFacade.getMovieFacade(EMF).populate();

    }
}
