package entities;
/*
 * author benjaminp
 * version 1.0
 */

import java.io.Serializable;
import javax.persistence.*;


@Entity
@Table(name = "Movie")
@NamedNativeQuery(name = "Movie.Truncate", query = "TRUNCATE TABLE Movie")
@NamedQueries({
    @NamedQuery(name = "Movie.deleteAllRows", query = "DELETE FROM Movie"),
    @NamedQuery(name = "Movie.getAll", query = "SELECT m FROM Movie m"),
    @NamedQuery(name = "Movie.getByName", query = "SELECT m FROM Movie m WHERE m.name LIKE :name")
})
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int year;
    private String[] actors;

    public Movie() {
    }

    public Movie(String name, int year, String[] actors) {
        this.name = name;
        this.year = year;
        this.actors = actors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String[] getActors() {
        return actors;
    }

    public void setActors(String[] actors) {
        this.actors = actors;
    }
}
