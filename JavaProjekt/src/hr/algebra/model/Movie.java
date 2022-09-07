/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Patrik
 */

@XmlRootElement(name = "movie")
@XmlAccessorType (XmlAccessType.FIELD)
public class Movie {    
    private int id;
    private String title;
    private String originalTitle;
    private String link;
    private String genre;
    private String picturePath;
    private int length;
    private String publishedDate;
    private List<Actor> actors;
    private List<Director> directors;

    public Movie() {
    }
    
     public Movie(String title, String originalTitle, String link, String publishedDate, List<Actor> actors, List<Director> directors, int length, String genre, String picturePath) {
        this.title = title;
        this.link = link;
        this.originalTitle = originalTitle;
        this.publishedDate = publishedDate;
        this.actors = actors;
        this.directors = directors;
        this.length = length;
        this.genre = genre;
        this.picturePath = picturePath;
    }

    public Movie(String title, String originalTitle, String link, String genre, String picturePath, int length, String publishedDate) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.link = link;
        this.genre = genre;
        this.picturePath = picturePath;
        this.length = length;
        this.publishedDate = publishedDate;
        this.actors = new ArrayList<>();
        this.directors = new ArrayList<>();
    }
     
     
    
    public Movie(int id, String title, String originalTitle, String link, String genre, String picturePath, int length, String publishedDate, List<Actor> actors, List<Director> directors) {
        this(title, originalTitle, link, publishedDate, actors, directors, length, genre, picturePath);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<Actor> getActors() {
        if (actors == null) {
            return new ArrayList<>();
        }
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Director> getDirectors() {
        if (directors == null) {
            return new ArrayList<>();
        }
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    @Override
    public String toString() {
        return  id + " - " + title;
    }
}
