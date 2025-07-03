package com.example.ghiblit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghiblit.adapter.BookingAdapter;
import com.example.ghiblit.database.Data;
import com.example.ghiblit.model.Booking;
import com.example.ghiblit.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    private Data dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        recyclerView = findViewById(R.id.recyclerBooking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new Data(this);
        SessionManager session = new SessionManager(this);
        userId = session.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadBookings();
    }

    private void loadBookings() {
        bookingList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Booking WHERE UID = ? ORDER BY BookingDate DESC",
                new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int bid = cursor.getInt(cursor.getColumnIndexOrThrow("BID"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("BookingDate"));
            String theater = cursor.getString(cursor.getColumnIndexOrThrow("Theater"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalPrice"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));

            bookingList.add(new Booking(bid, date, theater, total, status));
        }

        cursor.close();

        adapter = new BookingAdapter(this, bookingList, booking -> {
            cancelBooking(booking);
        });

        recyclerView.setAdapter(adapter);
    }

    private void cancelBooking(Booking booking) {
        if (!booking.getStatus().equalsIgnoreCase("confirmed")) {
            Toast.makeText(this, "Chỉ huỷ được các vé đã xác nhận!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE Booking SET Status = 'cancelled' WHERE BID = ?",
                new String[]{String.valueOf(booking.getId())});
        db.close();

        booking.setStatus("cancelled");
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "✅ Đã huỷ vé #" + booking.getId(), Toast.LENGTH_SHORT).show();
    }
}
