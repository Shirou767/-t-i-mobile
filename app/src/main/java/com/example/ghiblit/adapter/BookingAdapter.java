package com.example.ghiblit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ghiblit.R;
import com.example.ghiblit.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnCancelClickListener cancelClickListener;

    // Giao diện callback
    public interface OnCancelClickListener {
        void onCancel(Booking booking);
    }

    public BookingAdapter(Context context, List<Booking> bookings, OnCancelClickListener cancelClickListener) {
        this.context = context;
        this.bookingList = bookings;
        this.cancelClickListener = cancelClickListener;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.txtId.setText("Mã: #" + booking.getId());
        holder.txtDate.setText("Ngày: " + booking.getDate());
        holder.txtTheater.setText("Rạp: " + booking.getTheater());
        holder.txtTotal.setText("Tổng: " + booking.getTotal() + " VND");
        holder.txtStatus.setText("Trạng thái: " + booking.getStatus());

        if (booking.getStatus().equalsIgnoreCase("confirmed")) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> {
                if (cancelClickListener != null) {
                    cancelClickListener.onCancel(booking);
                }
            });
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtDate, txtTheater, txtTotal, txtStatus;
        Button btnCancel;

        public BookingViewHolder(View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtBookingId);
            txtDate = itemView.findViewById(R.id.txtBookingDate);
            txtTheater = itemView.findViewById(R.id.txtBookingTheater);
            txtTotal = itemView.findViewById(R.id.txtBookingTotal);
            txtStatus = itemView.findViewById(R.id.txtBookingStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel); // bạn cần thêm vào XML
        }
    }
}
