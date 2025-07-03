package com.example.ghiblit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ghiblit.R;
import com.example.ghiblit.model.Film;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private Context context;
    private List<Film> filmList;
    private OnFilmClickListener listener;

    // Interface xử lý sự kiện click
    public interface OnFilmClickListener {
        void onFilmClick(Film film);
    }

    // Constructor mới
    public FilmAdapter(Context context, List<Film> filmList, OnFilmClickListener listener) {
        this.context = context;
        this.filmList = filmList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film film = filmList.get(position);
        holder.txtTitle.setText(film.getTitle());
        holder.txtGenre.setText("Thể loại: " + film.getGenre());
        holder.txtDuration.setText("Thời lượng: " + film.getDuration() + " phút");

        int resId = context.getResources().getIdentifier(
                film.getImage().replace(".png", ""), "drawable", context.getPackageName());

        Glide.with(context)
                .load(resId != 0 ? resId : R.drawable.kiki)
                .into(holder.imgFilm);

        // Gán sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilmClick(film);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtGenre, txtDuration;
        ImageView imgFilm;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtGenre = itemView.findViewById(R.id.txtGenre);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            imgFilm = itemView.findViewById(R.id.imgFilm);
        }
    }
}
