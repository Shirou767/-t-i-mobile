package com.example.ghiblit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.widget.AdapterView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.ghiblit.database.Data;
import com.example.ghiblit.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class BookingActivity extends AppCompatActivity {

    private Spinner spinnerTheater, spinnerShowTime;
    private EditText edtSeat;
    private TextView txtTotal;
    private Button btnConfirm;

    private Data dbHelper;
    private int userId, filmId;
    private double finalTotal = 0;

    private List<String> theaterNames = new ArrayList<>();
    private Map<String, Double> theaterPrices = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Ánh xạ View
        spinnerTheater = findViewById(R.id.spinnerTheater);
        spinnerShowTime = findViewById(R.id.spinnerShowTime);
        edtSeat = findViewById(R.id.edtSeat);
        txtTotal = findViewById(R.id.txtTotal);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        dbHelper = new Data(this);

        SessionManager session = new SessionManager(this);
        userId = session.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        filmId = getIntent().getIntExtra("film_id", -1);
        if (filmId == -1) {
            Toast.makeText(this, "Không có thông tin phim!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTheaters();

        spinnerTheater.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTheater = theaterNames.get(position);
                int tid = getTIDByName(selectedTheater);
                loadShowtimes(filmId, tid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String selectedTheater = spinnerTheater.getSelectedItem().toString();
            String selectedShowtime = (String) spinnerShowTime.getSelectedItem();
            String seat = edtSeat.getText().toString().trim().toUpperCase();

            if (!seat.matches("^[A-Z]{1}[0-9]{1}$")) {
                Toast.makeText(this, "Định dạng ghế không hợp lệ! Ví dụ: A1", Toast.LENGTH_SHORT).show();
                return;
            }

            double ticketPrice = theaterPrices.get(selectedTheater);
            double seatFee = calculateSeatFee(seat);
            finalTotal = ticketPrice + seatFee;

            txtTotal.setText("Tổng tiền: " + finalTotal + " VND");

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            boolean success = insertBooking(userId, currentDate, selectedTheater, finalTotal, "confirmed", selectedShowtime);

            if (success) {
                Toast.makeText(this, "✅ Đặt vé thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "❌ Đặt vé thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTheaters() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Name, PTicket FROM Theater", null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            double price = Double.parseDouble(cursor.getString(1));
            theaterNames.add(name);
            theaterPrices.put(name, price);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, theaterNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheater.setAdapter(adapter);
    }

    private void loadShowtimes(int fid, int tid) {
        List<String> showtimes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ShowTime FROM Showtime WHERE FID = ? AND TID = ?",
                new String[]{String.valueOf(fid), String.valueOf(tid)});

        while (cursor.moveToNext()) {
            showtimes.add(cursor.getString(0));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, showtimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShowTime.setAdapter(adapter);
    }

    private int getTIDByName(String theaterName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT TID FROM Theater WHERE Name = ?", new String[]{theaterName});
        int tid = -1;
        if (cursor.moveToFirst()) {
            tid = cursor.getInt(0);
        }
        cursor.close();
        return tid;
    }

    private double calculateSeatFee(String seat) {
        if (seat.isEmpty()) return 0;
        char row = seat.charAt(0);
        switch (row) {
            case 'A': return 30000;
            case 'B': return 20000;
            case 'C': return 10000;
            default: return 5000;
        }
    }

    private boolean insertBooking(int uid, String date, String theater, double total, String status, String showtime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UID", uid);
        values.put("BookingDate", date);
        values.put("Theater", theater);
        values.put("TotalPrice", total);
        values.put("Status", status);
        values.put("ShowTime", showtime); // 🆕 nếu bảng Booking có cột này

        long result = db.insert("Booking", null, values);
        return result != -1;
    }
}
