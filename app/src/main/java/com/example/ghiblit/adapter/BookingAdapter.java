package com.example.ghiblit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghiblit.R;
import com.example.ghiblit.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnCancelClickListener listener;

    public interface OnCancelClickListener {
        void onCancelClick(Booking booking);
    }

    public BookingAdapter(Context context, List<Booking> bookingList, OnCancelClickListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.txtId.setText("Mã vé: #" + booking.getId());
        holder.txtDate.setText("Ngày đặt: " + booking.getDate());
        holder.txtTheater.setText("Rạp: " + booking.getTheater());
        holder.txtMovie.setText("Phim: " + booking.getMovieName());
        holder.txtSeats.setText("Ghế: " + booking.getSeatInfo());
        holder.txtTotal.setText("Tổng tiền: " + booking.getTotal() + " đ");
        holder.txtStatus.setText("Trạng thái: " + booking.getStatus());

        if (booking.getStatus().equalsIgnoreCase("confirmed")) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(booking));
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtDate, txtTheater, txtMovie, txtSeats, txtTotal, txtStatus;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtBookingId);
            txtDate = itemView.findViewById(R.id.txtBookingDate);
            txtTheater = itemView.findViewById(R.id.txtTheater);
            txtMovie = itemView.findViewById(R.id.txtMovie);
            txtSeats = itemView.findViewById(R.id.txtSeats);
            txtTotal = itemView.findViewById(R.id.txtTotalPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
