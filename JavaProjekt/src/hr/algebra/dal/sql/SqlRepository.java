/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal.sql;

import hr.algebra.dal.Repository;
import hr.algebra.model.Actor;
import hr.algebra.model.Director;
import hr.algebra.model.Movie;
import hr.algebra.model.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Patrik
 */
public class SqlRepository implements Repository {
    
    private static final String ID = "ID";
    private static final String TITLE = "Title";
    private static final String ORIGINAL_TITLE = "OriginalTitle";
    private static final String LINK = "Link";
    private static final String PICTURE_PATH = "PicturePath";
    private static final String PUBLISHED_DATE = "PublishedDate";
    private static final String GENRE = "Genre";
    private static final String LENGTH = "Length";
    private static final String NAME = "Name";
    private static final String MOVIE_ID = "MovieID";
    private static final String ACTOR_ID = "ActorID";
    private static final String DIRECTOR_ID = "DirectorID";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String ROLE = "Role";
    
    private static final String CREATE_MOVIE = "{ CALL createMovie (?,?,?,?,?,?,?,?) }";
    private static final String UPDATE_MOVIE = "{ CALL updateMovie (?,?,?,?,?,?,?,?) }";
    private static final String DELETE_MOVIE = "{ CALL deleteMovie (?) }";
    private static final String SELECT_MOVIE = "{ CALL selectMovie (?) }";
    private static final String SELECT_MOVIES  = "{ CALL selectMovies }";
    private static final String DELETE_MOVIES  = "{ CALL deleteMovies }";
    
    private static final String CREATE_ACTOR = "{ CALL createActor (?,?) }";
    private static final String UPDATE_ACTOR = "{ CALL updateActor (?,?) }";
    private static final String DELETE_ACTOR = "{ CALL deleteActor (?) }";
    private static final String SELECT_ACTOR = "{ CALL selectActor (?) }";
    private static final String SELECT_ACTORS  = "{ CALL selectActors }";
    private static final String SELECT_ACTORS_BY_MOVIE_ID  = "{ CALL selectActorsByMovieId (?) }";
    private static final String DELETE_MOVIE_ACTOR = "{ CALL deleteMovieActor (?,?) }";
    private static final String CREATE_MOVIE_ACTOR = "{ CALL createMovieActor (?,?) }";
    
    private static final String CREATE_DIRECTOR = "{ CALL createDirector (?,?) }";
    private static final String UPDATE_DIRECTOR = "{ CALL updateDirector (?,?) }";
    private static final String DELETE_DIRECTOR = "{ CALL deleteDirector (?) }";
    private static final String SELECT_DIRECTOR = "{ CALL selectDirector (?) }";
    private static final String SELECT_DIRECTORS  = "{ CALL selectDirectors }";
    private static final String SELECT_DIRECTORS_BY_MOVIE_ID  = "{ CALL selectDirectorsByMovieId (?) }";
    private static final String DELETE_MOVIE_DIRECTOR = "{ CALL deleteMovieDirector (?,?) }";
    private static final String CREATE_MOVIE_DIRECTOR = "{ CALL createMovieDirector (?,?) }";

    private static final String AUTH_USER = "{ CALL authUser (?,?,?) }";
    
    //MOVIE
    @Override
    public int createMovie(Movie movie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE)) {

            stmt.setString("@" + TITLE, movie.getTitle());
            stmt.setString("@" + ORIGINAL_TITLE, movie.getOriginalTitle());
            stmt.setString("@" + LINK, movie.getLink());
            stmt.setString("@" + PICTURE_PATH, movie.getPicturePath());
            stmt.setString("@" + PUBLISHED_DATE, movie.getPublishedDate());
            stmt.setString("@" + GENRE, movie.getGenre());
            stmt.setInt("@" + LENGTH, movie.getLength());
            stmt.registerOutParameter("@" + ID, Types.INTEGER);

            stmt.executeUpdate();
            int movieId = stmt.getInt("@" + ID);
            
            if (!movie.getActors().isEmpty()) {
                movie.getActors().forEach((actor) -> {
                    try {
                        int actorId = createActor(actor);
                        createMovieActor(movieId, actorId);
                    } catch (Exception ex) {
                        Logger.getLogger(SqlRepository.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
           
            if (!movie.getDirectors().isEmpty()) {
                movie.getDirectors().forEach((director) -> {
                    try {
                        int directorId = createDirector(director);
                        createMovieDirector(movieId, directorId);
                    } catch (Exception ex) {
                        Logger.getLogger(SqlRepository.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
            
            
            return movieId;
        }
    }

    @Override
    public void createMovies(List<Movie> movies) throws Exception {
        movies.forEach((movie) -> {
            try {
                createMovie(movie);
            } catch (Exception ex) {
                Logger.getLogger(SqlRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void updateMovie(int id, Movie movie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_MOVIE)) {

            stmt.setString("@" + TITLE, movie.getTitle());
            stmt.setString("@" + ORIGINAL_TITLE, movie.getOriginalTitle());
            stmt.setString("@" + LINK, movie.getLink());
            stmt.setString("@" + PICTURE_PATH, movie.getPicturePath());
            stmt.setString("@" + PUBLISHED_DATE, movie.getPublishedDate());
            stmt.setString("@" + GENRE, movie.getGenre());
            stmt.setInt("@" + LENGTH, movie.getLength());
            stmt.setInt("@" + ID, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteMovie(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_MOVIE)) {

            stmt.setInt("@" + ID, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Movie> selectMovie(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_MOVIE)) {

            stmt.setInt("@" + ID, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Movie(
                            rs.getInt(ID),
                            rs.getString(TITLE),
                            rs.getString(ORIGINAL_TITLE),
                            rs.getString(LINK),
                            rs.getString(GENRE),
                            rs.getString(PICTURE_PATH),
                            rs.getInt(LENGTH),
                            rs.getString(PUBLISHED_DATE),
                            selectActorsByMovieId(id),
                            selectDirectorsByMovieId(id)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Movie> selectMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_MOVIES);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movies.add(new Movie(
                            rs.getInt(ID),
                            rs.getString(TITLE),
                            rs.getString(ORIGINAL_TITLE),
                            rs.getString(LINK),
                            rs.getString(GENRE),
                            rs.getString(PICTURE_PATH),
                            rs.getInt(LENGTH),
                            rs.getString(PUBLISHED_DATE),
                            selectActorsByMovieId(rs.getInt(ID)),
                            selectDirectorsByMovieId(rs.getInt(ID))));
            }
        }
        return movies;
    }
    
    @Override
    public void deleteMovies() throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_MOVIES)) {
            stmt.executeUpdate();
        }
    }
    
    
    //ACTOR
    @Override
    public int createActor(Actor actor) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_ACTOR)) {

            stmt.setString("@" + NAME, actor.getName());
            stmt.registerOutParameter("@" + ID, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt("@" + ID);
        }
    }

    @Override
    public void updateActor(int id, Actor actor) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_ACTOR)) {

            stmt.setString("@" + NAME, actor.getName());
            stmt.setInt("@" + ID, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteActor(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_ACTOR)) {

            stmt.setInt("@" + ID, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Actor> selectActor(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ACTOR)) {

            stmt.setInt("@" + ID, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Actor(rs.getInt(ID), rs.getString(NAME)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Actor> selectActors() throws Exception {
        List<Actor> actors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ACTORS);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                actors.add(new Actor(rs.getInt(ID), rs.getString(NAME)));
            }
        }
        return actors;
    }

    @Override
    public List<Actor> selectActorsByMovieId(int id) throws Exception {
        List<Actor> actors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ACTORS_BY_MOVIE_ID)) {

            stmt.setInt("@" + MOVIE_ID, id);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    actors.add(new Actor(rs.getInt(ID), rs.getString(NAME)));
                }
            }
        }
        return actors;
    }

    @Override
    public void deleteMovieActor(int movieId, int actorId) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_MOVIE_ACTOR)) {

            stmt.setInt("@" + MOVIE_ID, movieId);
            stmt.setInt("@" + ACTOR_ID, actorId);

            stmt.executeUpdate();
        }
    }
    
    @Override
    public void createMovieActor(int movieId, int actorId) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE_ACTOR)) {

            stmt.setInt("@" + MOVIE_ID, movieId);
            stmt.setInt("@" + ACTOR_ID, actorId);

            stmt.executeUpdate();
        }
    }
    
    
    //DIRECTOR
    @Override
    public int createDirector(Director director) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_DIRECTOR)) {

            stmt.setString("@" + NAME, director.getName());
            stmt.registerOutParameter("@" + ID, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt("@" + ID);
        }
    }

    @Override
    public void updateDirector(int id, Director director) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_DIRECTOR)) {

            stmt.setString("@" + NAME, director.getName());
            stmt.setInt("@" + ID, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteDirector(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_DIRECTOR)) {

            stmt.setInt("@" + ID, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Director> selectDirector(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_DIRECTOR)) {

            stmt.setInt("@" + ID, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Director(rs.getInt(ID), rs.getString(NAME)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Director> selectDirectors() throws Exception {
        List<Director> directors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_DIRECTORS);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                directors.add(new Director(rs.getInt(ID), rs.getString(NAME)));
            }
        }
        return directors;
    }

    @Override
    public List<Director> selectDirectorsByMovieId(int id) throws Exception {
        List<Director> directors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_DIRECTORS_BY_MOVIE_ID)) {

            stmt.setInt("@" + MOVIE_ID, id);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    directors.add(new Director(rs.getInt(ID), rs.getString(NAME)));
                }
            }
        }
        return directors;
    }

    @Override
    public void deleteMovieDirector(int movieId, int directorId) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_MOVIE_DIRECTOR)) {

            stmt.setInt("@" + MOVIE_ID, movieId);
            stmt.setInt("@" + DIRECTOR_ID, directorId);

            stmt.executeUpdate();
        }
    }

    @Override
    public void createMovieDirector(int movieId, int directorId) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE_DIRECTOR)) {

            stmt.setInt("@" + MOVIE_ID, movieId);
            stmt.setInt("@" + DIRECTOR_ID, directorId);

            stmt.executeUpdate();
        }
    }    

    @Override
    public User authUser(String username, String password) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(AUTH_USER)) {

            stmt.setString("@" + USERNAME, username);
            stmt.setString("@" + PASSWORD, password);
            stmt.registerOutParameter("@" + ROLE, Types.NVARCHAR);

            stmt.executeUpdate();
            
            String role = stmt.getString("@" + ROLE);
            
            if (role != null) {
                return new User(username, password, role);
            }
            return null;
        }
    }
}
