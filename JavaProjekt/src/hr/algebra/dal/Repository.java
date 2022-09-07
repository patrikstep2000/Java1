/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal;

import hr.algebra.model.Actor;
import hr.algebra.model.Director;
import hr.algebra.model.Movie;
import hr.algebra.model.User;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Patrik
 */
public interface Repository {
    User authUser(String username, String password) throws Exception;
    
    int createMovie(Movie movie) throws Exception;
    void createMovies(List<Movie> movies) throws Exception;
    void updateMovie(int id, Movie movie) throws Exception;
    void deleteMovie(int id) throws Exception;
    Optional<Movie> selectMovie(int id) throws Exception;
    List<Movie> selectMovies() throws Exception;
    void deleteMovies() throws Exception;
    
    int createActor(Actor actor) throws Exception;
    void updateActor(int id, Actor actor) throws Exception;
    void deleteActor(int id) throws Exception;
    Optional<Actor> selectActor(int id) throws Exception;
    List<Actor> selectActors() throws Exception;
    List<Actor> selectActorsByMovieId(int id) throws Exception;
    void deleteMovieActor(int movieId, int actorId) throws Exception;
    void createMovieActor(int movieId, int actorId) throws Exception;
    
    int createDirector(Director director) throws Exception;
    void updateDirector(int id, Director director) throws Exception;
    void deleteDirector(int id) throws Exception;
    Optional<Director> selectDirector(int id) throws Exception;
    List<Director> selectDirectors() throws Exception;
    List<Director> selectDirectorsByMovieId(int id) throws Exception;
    void deleteMovieDirector(int movieId, int directorId) throws Exception;
    void createMovieDirector(int movieId, int directorId) throws Exception;
}
