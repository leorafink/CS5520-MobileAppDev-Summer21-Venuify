package edu.neu.venuify.reservationPage;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import edu.neu.venuify.DateComparator;
import edu.neu.venuify.R;
import edu.neu.venuify.Reservation;
import edu.neu.venuify.ReservationDetailsPage;

/**
 * This adapter works for the recycler view of the ReservationPageActivities.
 */
public class RecyclerViewAdapterReservationPage extends RecyclerView.Adapter<RecyclerViewHolderReservationPage>{


    private List<Reservation> reservationsList;

    public RecyclerViewAdapterReservationPage(List<Reservation> reservations) {
        this.reservationsList = reservations;
    }


    //method for the ReservationDetailsPage to return the list of current reservations
    public List<Reservation> getReservationList() {
        return reservationsList;
    }


    //creates the venue object layout for the recycler view
    @NonNull
    @Override
    public RecyclerViewHolderReservationPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_object_reservation_page, parent, false);
        return new RecyclerViewHolderReservationPage(view);
    }

    //sets things in the recycler view card based on the reservation objects
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolderReservationPage holder, int position) {
        Reservation currentReservationObject = reservationsList.get(position);
        holder.reservationName.setText(currentReservationObject.getVenue());
        holder.reservationTime.setText(currentReservationObject.getTime());
        holder.reservationDate.setText(currentReservationObject.getDate());
    }

    @Override
    public int getItemCount() {
        return reservationsList.size();
    }



}
