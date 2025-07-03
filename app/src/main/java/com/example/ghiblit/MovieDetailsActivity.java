package com.example.ghiblit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ghiblit.database.Data;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView txtTitle, txtGenre, txtDescription, txtDuration;
    private ImageView imgPoster, imgBackground;
    private Button btnBook;
    private Data dbHelper;
    private int filmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Ánh xạ view
        txtTitle = findViewById(R.id.txtTitle);
        txtGenre = findViewById(R.id.txtGenre);
        txtDescription = findViewById(R.id.txtDescription);
        txtDuration = findViewById(R.id.txtDuration);
        imgPoster = findViewById(R.id.imgPoster);
        imgBackground = findViewById(R.id.imgBackground);
        btnBook = findViewById(R.id.btnBookTicket);

        dbHelper = new Data(this);

        // Nhận film_id từ intent
        filmId = getIntent().getIntExtra("film_id", -1);

        if (filmId == -1) {
            Toast.makeText(this, "Không có thông tin phim!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý sự kiện nút "Đặt vé"
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailsActivity.this, BookingActivity.class);
            intent.putExtra("film_id", filmId); // Truyền filmId qua BookingActivity
            startActivity(intent);
        });

        // Lấy dữ liệu từ database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Title, Genre, Description, Duration, Image FROM Film WHERE FID = ?", new String[]{String.valueOf(filmId)});

        if (cursor.moveToFirst()) {
            String title = cursor.getString(0);
            String genre = cursor.getString(1);
            String description = cursor.getString(2);
            int duration = cursor.getInt(3);
            String imageName = cursor.getString(4); // ví dụ: totoro.png

            txtTitle.setText(title);
            txtGenre.setText("Thể loại: " + genre);
            txtDescription.setText(description);
            txtDuration.setText("Thời lượng: " + duration + " phút");

            // Load ảnh poster & background từ drawable
            int resId = getResources().getIdentifier(imageName.replace(".png", ""), "drawable", getPackageName());
            if (resId != 0) {
                Glide.with(this).load(resId).into(imgPoster);
                Glide.with(this).load(resId).into(imgBackground); // dùng lại ảnh làm nền
            }
        } else {
            Toast.makeText(this, "Không tìm thấy phim!", Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
        db.close();
    }
}
