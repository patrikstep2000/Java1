/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.parsers.rss;

import hr.algebra.factory.ParserFactory;
import hr.algebra.factory.UrlConnectionFactory;
import hr.algebra.model.Actor;
import hr.algebra.model.Director;
import hr.algebra.model.Movie;
import hr.algebra.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Patrik
 */
public class MovieParser {
    private static final String RSS_URL = "src/assets/movies.xml";
    private static final String EXT = ".jpg";
    private static final String DIR = "assets";

    private MovieParser() {
    }

    public static List<Movie> parse() throws IOException, XMLStreamException {
        List<Movie> movies = new ArrayList<>();
        List<Actor> actors = new ArrayList<>();
        List<Director> directors = new ArrayList<>();
        HttpURLConnection con = UrlConnectionFactory.getHttpUrlConnection(RSS_URL);
        try (InputStream is = con.getInputStream()) {
            XMLEventReader reader = ParserFactory.createStaxParser(is);

            Optional<TagType> tagType = Optional.empty();
            Movie movie = new Movie();
            StartElement startElement = null;
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();
                        tagType = TagType.from(qName);
                        if (tagType.isPresent() && tagType.get().equals(TagType.ITEM)) {
                            movie = null;
                            movies.add(movie);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (tagType.isPresent() && movie != null) {
                            Characters characters = event.asCharacters();
                            String data = characters.getData().trim();
                            switch (tagType.get()) {
                                case TITLE:
                                    if (!data.isEmpty()) {
                                        movie.setTitle(data);
                                    }
                                    break;
                                case ORIGINAL_TITLE:
                                    if (!data.isEmpty()) {
                                        movie.setOriginalTitle(data);
                                    }
                                    break;
                                case LINK:
                                    if (!data.isEmpty()) {
                                        movie.setLink(data);
                                    }
                                    break;
                                case GENRE:
                                    if (!data.isEmpty()) {
                                        movie.setGenre(data);
                                    }
                                    break;
                                case LENGTH:
                                    if (!data.isEmpty()) {
                                        movie.setLength(Integer.parseInt(data));
                                    }
                                    break;
                                case PICTURE_PATH:
                                    if (!data.isEmpty() && movie.getPicturePath() == null) {
                                        handlePicture(movie, data);
                                    }
                                    break;
                                case PUBLISHED_DATE:
                                    if (!data.isEmpty()) {
                                        movie.setPublishedDate(data);
                                    }
                                    break;
                                case ACTORS:
                                    if (!data.isEmpty()) {
                                        List<String> temp = Arrays.asList(data.split(","));
                                        temp.forEach(t -> actors.add(new Actor(t)));
                                    }
                                    movie.setActors(actors);
                                    break;
                                case DIRECTOR:
                                    if (!data.isEmpty()) {
                                        List<String> temp = Arrays.asList(data.split(","));
                                        temp.forEach(t -> directors.add(new Director(t)));
                                    }
                                    movie.setDirectors(directors);
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        catch (Exception ex){
            Logger.getLogger(MovieParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movies;
    }

    private static void handlePicture(Movie movie, String pictureUrl) {

        try {
            String ext = pictureUrl.substring(pictureUrl.lastIndexOf("."));
            if (ext.length() > 4) {
                ext = EXT;
            }
            String pictureName = UUID.randomUUID() + ext;
            String localPicturePath = DIR + File.separator + pictureName;

            FileUtils.copyFromUrl(pictureUrl, localPicturePath);
            movie.setPicturePath(localPicturePath);
        } catch (IOException ex) {
            Logger.getLogger(MovieParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private enum TagType {

        ITEM("item"),
        TITLE("title"),
        LINK("link"),
        ORIGINAL_TITLE("orignaziv"),
        GENRE("zanr"),
        PUBLISHED_DATE("pubdate"),
        PICTURE_PATH("plakat"),
        LENGTH("trajanje"),
        ACTORS("glumci"),
        DIRECTOR("redatelj");

        private final String name;

        private TagType(String name) {
            this.name = name;
        }

        private static Optional<TagType> from(String name) {
            for (TagType value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }
}
