package com.example.ghiblit.model;

public class Film {
    private int fid;
    private String title;
    private String genre;
    private String description;
    private int duration;
    private String image;

    public Film(int fid, String title, String genre, String description, int duration, String image) {
        this.fid = fid;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.duration = duration;
        this.image = image;
    }

    public int getFid() {
        return fid;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }
}
