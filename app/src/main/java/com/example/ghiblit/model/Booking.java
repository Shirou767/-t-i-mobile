package com.example.ghiblit.model;

public class Booking {
    private int id;
    private String date;
    private String theater;
    private double total;
    private String status;

    public Booking(int id, String date, String theater, double total, String status) {
        this.id = id;
        this.date = date;
        this.theater = theater;
        this.total = total;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTheater() { return theater; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }

    // Setter cho status để hỗ trợ cập nhật trạng thái hủy
    public void setStatus(String status) {
        this.status = status;
    }
}
