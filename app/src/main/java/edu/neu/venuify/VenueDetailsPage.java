package edu.neu.venuify;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.neu.venuify.Adapters.AvailableTimeslotAdapter;

import edu.neu.venuify.Models.VenueObject;

public class VenueDetailsPage extends AppCompatActivity {

    private Spinner dateSelector;
    private DatabaseReference mDatabase;

    private List<Reservation> fullReservationList = new ArrayList<>();
    private List<Reservation> reservationListToDisplay = new ArrayList<>();

    private ArrayList<String> keys = new ArrayList<>();

    private RecyclerView recyclerView;

    //Keeps list of times that should be displayed based on the date selected in dropdown
    ArrayList<Reservation> availableSlotsByDayList;

    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    AvailableTimeslotAdapter byDayAdapter;
    LinearLayoutManager HorizontalLayout;

    ArrayAdapter<Reservation> adapter;
    VenueObject venueObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue_details_page);
//        VenueObject venueObject = getIntent().getParcelableExtra("venue");
        venueObject = getIntent().getParcelableExtra("venue");
        TextView venueTitleOnDetailsPage = findViewById(R.id.venueTitleOnDetailsPg);
        venueTitleOnDetailsPage.setText(venueObject.getVenueName());
        ImageView venueImgOnDetailsPage = findViewById(R.id.venueImgOnDetailsPage);
        venueImgOnDetailsPage.setImageResource(venueObject.getImageId());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        dateSelector = findViewById(R.id.dateSelector);
//        ArrayAdapter<Reservation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reservationListToDisplay);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reservationListToDisplay);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSelector.setAdapter(adapter);

        mDatabase.child("reservations").addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                        Reservation reservation = dataSnapshot.getValue(Reservation.class);
                        reservation.setReservationId(dataSnapshot.getKey());

                        if (isFutureAvailableReservation(reservation, venueObject)) {

                            fullReservationList.add(reservation);

                            if (!dateAlreadySeen(reservation)) {
                                reservationListToDisplay.add(reservation);
                                keys.add(dataSnapshot.getKey());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        //will use the keys array if we want to handle changes
                        //the key will identify the user object that changed
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext()
                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        recyclerView = findViewById(R.id.recyclerview);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        availableSlotsByDayList = new ArrayList<>();
        byDayAdapter = new AvailableTimeslotAdapter(availableSlotsByDayList);

        HorizontalLayout = new LinearLayoutManager(VenueDetailsPage.this, LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(byDayAdapter);

        dateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                showAvailableTimeSlots();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }
        });
    }

    //Shows available timeslots based on the date selected in the dropdown
    public void showAvailableTimeSlots() {

        availableSlotsByDayList.clear();
        for (Reservation r : fullReservationList) {
            if (r.getDate().equals(dateSelector.getSelectedItem().toString())) {
                availableSlotsByDayList.add(r);
            }
        }
        byDayAdapter.notifyDataSetChanged();
    }

//    private void loadData() {
//
//        mDatabase.child("reservations").addChildEventListener(
//                new ChildEventListener() {
//
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
//
//                        Reservation reservation = dataSnapshot.getValue(Reservation.class);
//                        reservation.setReservationId(dataSnapshot.getKey());
//
//                        if (isFutureAvailableReservation(reservation, venueObject)) {
//
//                            fullReservationList.add(reservation);
//
//                            if (!dateAlreadySeen(reservation)) {
//                                reservationListToDisplay.add(reservation);
//                                keys.add(dataSnapshot.getKey());
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
//                        //will use the keys array if we want to handle changes
//                        //the key will identify the user object that changed
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(getApplicationContext()
//                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//    }

    //Todo also filter out past reservations
    private boolean isFutureAvailableReservation(Reservation reservation, VenueObject venueObject) {
        return reservation.venue.equals(venueObject.getVenueName()) &&
             reservation.isAvailable;
    }

    private boolean dateAlreadySeen(Reservation r) {
        return reservationListToDisplay.contains(r);
    }
}
