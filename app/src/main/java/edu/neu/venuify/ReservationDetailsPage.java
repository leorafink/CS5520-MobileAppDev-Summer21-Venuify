package edu.neu.venuify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.neu.venuify.reservationPage.ReservationPageActivity;


public class ReservationDetailsPage extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public String venue;
    public String date;
    public String time;
    public Integer numGuests;
    public String price;
    public Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_details_page);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent.hasExtra("hideCancel")) {
            cancelButton.setVisibility(View.GONE);
        }

        //when an item in the reservations is clicked, we create a new ReservationDetailsPage
        //from that activity, send the info from the object clicked through a ParcelableExtra.
        //Here, we make a new Reservation object, to be able to set each field when the reservation appears.
        Reservation reservationObject = intent.getParcelableExtra("itemClickedInResList");

        //set the text of the reservation to match the item clicked
        TextView venueTitleOnReservation = findViewById(R.id.venueInfo);
        venueTitleOnReservation.setText(reservationObject.getVenue());

        TextView venueDateOnReservation = findViewById(R.id.dateInfo);
        venueDateOnReservation.setText(reservationObject.getDate());

        TextView venueGuestsOnReservation = findViewById(R.id.numGuestInfo);
        venueGuestsOnReservation.setText(String.valueOf(reservationObject.getNumGuests()));

        TextView venuePriceOnReservation = findViewById(R.id.priceInfo);
        venuePriceOnReservation.setText(reservationObject.getPrice());

        TextView venueTimeOnReservation = findViewById(R.id.timeInfo);
        venueTimeOnReservation.setText(reservationObject.getTime());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        createDatabaseListener();

        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = 0;

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Are you sure you would like to cancel the reservation?");

                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("/reservations/" + reservationObject.getReservationId() + "/isAvailable/", true);
                        map.put("/reservations/" + reservationObject.getReservationId() + "/user/", "");
                        map.put("/reservations/" + reservationObject.getReservationId() + "/numGuests", 0);

                        mDatabase.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), "Reservation Canceled", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(v.getContext(), ReservationPageActivity.class);
                                v.getContext().startActivity(intent);
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

    }


    private void createDatabaseListener() {
        mDatabase.child("reservations").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                        Reservation reservation = Objects.requireNonNull(snapshot.getValue(Reservation.class));

//                            venue = reservation.venue;
//                            date = reservation.date;
//                            time = reservation.time;
//                            numGuests = reservation.numGuests;
//                            price = reservation.price;
//
//                            TextView venueInfo = (TextView) findViewById(R.id.venueInfo);
//                            TextView dateInfo = (TextView) findViewById(R.id.dateInfo);
//                            TextView timeInfo = (TextView) findViewById(R.id.timeInfo);
//                            TextView numGuestsInfo = (TextView) findViewById(R.id.numGuestInfo);
//                            TextView priceInfo = (TextView) findViewById(R.id.priceInfo);
//
//                            venueInfo.setText(venue);
//                            dateInfo.setText(date);
//                            timeInfo.setText(time);
//                            numGuestsInfo.setText(String.valueOf(numGuests));
//                            priceInfo.setText(price);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                        Reservation reservation = Objects.requireNonNull(snapshot.getValue(Reservation.class));
                        String venue = reservation.getVenue();
                        String date = reservation.getDate();
                        String time = reservation.getTime();
                        Integer numGuests = reservation.getNumGuests();
                        String price = reservation.getPrice();

                        TextView venueInfo = findViewById(R.id.venueInfo);
                        TextView dateInfo = findViewById(R.id.dateInfo);
                        TextView timeInfo =  findViewById(R.id.timeInfo);
                        TextView numGuestsInfo = findViewById(R.id.numGuestInfo);
                        TextView priceInfo = findViewById(R.id.priceInfo);

                        venueInfo.setText(venue);
                        dateInfo.setText(date);
                        timeInfo.setText(time);
                        numGuestsInfo.setText(String.valueOf(numGuests));
                        priceInfo.setText(price);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );
    }


}



