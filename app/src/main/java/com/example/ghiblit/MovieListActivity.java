package com.example.ghiblit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghiblit.adapter.FilmAdapter;
import com.example.ghiblit.database.Data;
import com.example.ghiblit.model.Film;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;


public class MovieListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FilmAdapter adapter;
    List<Film> filmList;
    Data dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        recyclerView = findViewById(R.id.recyclerViewFilms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new Data(this);
        filmList = loadFilmsFromDatabase();

        adapter = new FilmAdapter(this, filmList, new FilmAdapter.OnFilmClickListener() {
            @Override
            public void onFilmClick(Film film) {
                Intent intent = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
                intent.putExtra("film_id", film.getFid());
                startActivity(intent);
            }
        });


        recyclerView.setAdapter(adapter);
    }


    private List<Film> loadFilmsFromDatabase() {
        List<Film> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT FID, Title, Genre, Description, Duration, Image FROM Film", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String genre = cursor.getString(2);
            String desc = cursor.getString(3);
            int duration = cursor.getInt(4);
            String image = cursor.getString(5);

            list.add(new Film(id, title, genre, desc, duration, image));
        }
        cursor.close();
        return list;
    }
}
