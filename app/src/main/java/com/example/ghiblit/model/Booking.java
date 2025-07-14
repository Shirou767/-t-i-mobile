package com.example.ghiblit.model;

public class Booking {
    private int id;
    private String date;
    private String theater;
    private String movieName;
    private String seatInfo;
    private double total;
    private String status;

    public Booking(int id, String date, String theater, String movieName, String seatInfo, double total, String status) {
        this.id = id;
        this.date = date;
        this.theater = theater;
        this.movieName = movieName;
        this.seatInfo = seatInfo;
        this.total = total;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTheater() {
        return theater;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getSeatInfo() {
        return seatInfo;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

