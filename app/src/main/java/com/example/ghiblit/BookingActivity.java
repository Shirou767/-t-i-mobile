package com.example.ghiblit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ghiblit.database.Data;
import com.example.ghiblit.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class BookingActivity extends AppCompatActivity {

    private Spinner spinnerTheater, spinnerShowTime;
    private TextView txtTotal;
    private Button btnConfirm;
    private GridLayout seatLayout;

    private Data dbHelper;
    private int userId, filmId;
    private double finalTotal = 0;
    private String selectedSeat = "";

    private List<String> theaterNames = new ArrayList<>();
    private Map<String, Double> theaterPrices = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        spinnerTheater = findViewById(R.id.spinnerTheater);
        spinnerShowTime = findViewById(R.id.spinnerShowTime);
        txtTotal = findViewById(R.id.txtTotal);
        btnConfirm = findViewById(R.id.btnConfirmBooking);
        seatLayout = findViewById(R.id.seatLayout);

        dbHelper = new Data(this);
        SessionManager session = new SessionManager(this);
        userId = session.getUserId();

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

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerShowTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedTheater = spinnerTheater.getSelectedItem().toString();
                String selectedShowtime = spinnerShowTime.getSelectedItem().toString();
                int tid = getTIDByName(selectedTheater);
                generateSeatMap(filmId, tid, selectedShowtime);
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String selectedTheater = spinnerTheater.getSelectedItem().toString();
            String selectedShowtime = spinnerShowTime.getSelectedItem().toString();

            if (selectedSeat.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ghế!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isSeatBooked(filmId, getTIDByName(selectedTheater), selectedShowtime, selectedSeat)) {
                Toast.makeText(this, "Ghế đã có người đặt!", Toast.LENGTH_SHORT).show();
                return;
            }

            double ticketPrice = theaterPrices.get(selectedTheater);
            double seatFee = calculateSeatFee(selectedSeat);
            finalTotal = ticketPrice + seatFee;
            txtTotal.setText("Tổng tiền: " + finalTotal + " VND");

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            boolean success = insertBooking(userId, currentDate, selectedTheater, finalTotal, "confirmed", selectedShowtime, selectedSeat);
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
        theaterNames.clear();
        theaterPrices.clear();
        Cursor cursor = db.rawQuery("SELECT DISTINCT t.Name, t.PTicket FROM Theater t JOIN Showtime s ON t.TID = s.TID WHERE s.FID = ?",
                new String[]{String.valueOf(filmId)});
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            double price = Double.parseDouble(cursor.getString(1));
            theaterNames.add(name);
            theaterPrices.put(name, price);
        }
        cursor.close();
        spinnerTheater.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theaterNames));
    }

    private void loadShowtimes(int fid, int tid) {
        List<String> showtimes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ShowTime FROM Showtime WHERE FID = ? AND TID = ?",
                new String[]{String.valueOf(fid), String.valueOf(tid)});
        while (cursor.moveToNext()) showtimes.add(cursor.getString(0));
        cursor.close();
        spinnerShowTime.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, showtimes));
    }

    private int getTIDByName(String theaterName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT TID FROM Theater WHERE Name = ?", new String[]{theaterName});
        int tid = -1;
        if (cursor.moveToFirst()) tid = cursor.getInt(0);
        cursor.close();
        return tid;
    }

    private double calculateSeatFee(String seat) {
        char row = seat.charAt(0);
        switch (row) {
            case 'A': return 30000;
            case 'B': return 20000;
            case 'C': return 10000;
            default: return 5000;
        }
    }

    private boolean insertBooking(int uid, String date, String theater, double total, String status, String showtime, String seat) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ghi vào bảng Booking
        ContentValues values = new ContentValues();
        values.put("UID", uid);
        values.put("BookingDate", date);
        values.put("Theater", theater);
        values.put("TotalPrice", total);
        values.put("Status", status);
        values.put("ShowTime", showtime);
        long bookingId = db.insert("Booking", null, values);
        if (bookingId == -1) return false;

        // 🆕 Lấy Theater ID từ tên rạp
        int tid = getTIDByName(theater);

        // Ghi vào bảng BookDetail
        ContentValues detail = new ContentValues();
        detail.put("BID", bookingId);
        detail.put("FID", filmId);
        detail.put("TID", tid); // ✅ Fix lỗi thiếu TID
        detail.put("seat", seat);
        detail.put("Price", total);
        long result = db.insert("BookDetail", null, detail);

        return result != -1;
    }

    private boolean isSeatBooked(int fid, int tid, String showtime, String seat) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT bd.seat FROM BookDetail bd " +
                        "JOIN Booking b ON bd.BID = b.BID " +
                        "WHERE b.Theater = (SELECT Name FROM Theater WHERE TID = ?) AND b.ShowTime = ?",
                new String[]{String.valueOf(tid), showtime});
        while (cursor.moveToNext()) {
            if (seat.equalsIgnoreCase(cursor.getString(0))) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }


    private void generateSeatMap(int fid, int tid, String showtime) {
        seatLayout.removeAllViews();
        List<String> booked = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT bd.seat FROM BookDetail bd " +
                        "JOIN Booking b ON bd.BID = b.BID " +
                        "WHERE b.Theater = (SELECT Name FROM Theater WHERE TID = ?) AND b.ShowTime = ?",
                new String[]{String.valueOf(tid), showtime});
        while (cursor.moveToNext()) booked.add(cursor.getString(0));
        cursor.close();

        char[] rows = {'A', 'B', 'C'};
        int cols = 4;

        for (char row : rows) {
            for (int col = 1; col <= cols; col++) {
                String seatCode = row + String.valueOf(col);
                Button btn = new Button(this);
                btn.setText(seatCode);
                btn.setLayoutParams(new GridLayout.LayoutParams());
                btn.setWidth(150);
                btn.setHeight(150);
                if (booked.contains(seatCode)) {
                    btn.setEnabled(false);
                    btn.setBackgroundColor(Color.GRAY);
                } else {
                    btn.setOnClickListener(v -> {
                        selectedSeat = seatCode;
                        Toast.makeText(this, "Đã chọn ghế: " + seatCode, Toast.LENGTH_SHORT).show();
                    });
                }
                seatLayout.addView(btn);
            }
        }
    }
}

