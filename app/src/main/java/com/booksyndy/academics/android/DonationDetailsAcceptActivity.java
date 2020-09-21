package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.Donation;
import com.booksyndy.academics.android.Data.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DonationDetailsAcceptActivity extends AppCompatActivity {

    private static final String TAG = "DONATION_DETAILS_ACCEPT";

    private String don_id, donTitle, donDesc, donPic, curUserName, curUserPhone, listDate, donorName, donorPhone, donorAddress;
    private int donWeight, donStatus;
    private double don_lat, don_lng, user_lat, user_lng;
    private Donation selectedDonation;
    private User donationOwner;
    private SharedPreferences userPref;
    private ListenerRegistration mDonationRegistration;
    private FirebaseFirestore mFirestore;
    private DocumentReference donationRef;
    private Button acceptBtn, completeBtn;
    private LinearLayout donorNameLL, donorPhoneLL, donorAddressLL;

    private Menu menu;
    private final int MENU_CANCEL = 799;
    // view related vars
    private TextView titleView, descView, weightView, statusView, donDateView, distanceView, weightLabel, donorNameView, phoneView, addressView;
    private ImageView donPicView, copyAction, callAction, contactsAction;
    private boolean newEntry = true, resetCounter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details_accept);

        getSupportActionBar().setTitle("Donation details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        don_id = getIntent().getStringExtra("DON_DOC_NAME");
        donTitle = getIntent().getStringExtra("DON_TITLE");
        donDesc = getIntent().getStringExtra("DON_DESC");
        donPic = getIntent().getStringExtra("DON_PIC");
        donWeight = getIntent().getIntExtra("DON_WEIGHT", 0);
        donStatus = getIntent().getIntExtra("DON_STATUS", 0);
        listDate = getIntent().getStringExtra("DON_LISTDATE");
        don_lat = getIntent().getDoubleExtra("DON_LAT", 0.0);
        don_lng = getIntent().getDoubleExtra("DON_LNG", 0.0);

        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);


        initFireStore();


        titleView = findViewById(R.id.donTitle_v);
        descView = findViewById(R.id.donDesc_v);
        weightView = findViewById(R.id.donWeight_v);
        donPicView = findViewById(R.id.don_image_v);
        donDateView = findViewById(R.id.donDate_v);
        distanceView = findViewById(R.id.donDist);
        weightLabel = findViewById(R.id.detailsWeightLabel);
        donorNameView = findViewById(R.id.donorName);
        phoneView = findViewById(R.id.donorPhone);
        addressView = findViewById(R.id.donAddress);
        donorNameLL = findViewById(R.id.donorNameLL);
        donorPhoneLL = findViewById(R.id.donorPhoneLL);
        donorAddressLL = findViewById(R.id.donAddrLL);

        copyAction = findViewById(R.id.copyDPhone);
        callAction = findViewById(R.id.callDonor);
        contactsAction = findViewById(R.id.addDContacts);


        Glide.with(donPicView.getContext())
                .load(donPic)
                .into(donPicView);

        titleView.setText(donTitle);
        descView.setText(donDesc);
        donDateView.setText(listDate);
        if (donWeight > 0) {
            weightView.setText(donWeight + " kgs");
        } else {
            weightView.setVisibility(View.GONE);
            weightLabel.setVisibility(View.GONE);
        }

        acceptBtn = findViewById(R.id.acceptDonReqBtn);
        completeBtn = findViewById(R.id.logDonBtn);


        if (donStatus == 1) {
            acceptBtn.setVisibility(View.VISIBLE);
//            menu.add(0, MENU_CANCEL, Menu.FIRST, "cancel").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        } else if (donStatus == 2) {
            completeBtn.setVisibility(View.VISIBLE);
            donorName = getIntent().getStringExtra("DON_DONORNAME");
            donorPhone = getIntent().getStringExtra("DON_PHONE");
            donorAddress = getIntent().getStringExtra("DON_ADDRESS");

            donorNameLL.setVisibility(View.VISIBLE);
            donorPhoneLL.setVisibility(View.VISIBLE);
            donorAddressLL.setVisibility(View.VISIBLE);

            donorNameView.setText(donorName);
            phoneView.setText(donorPhone);
            addressView.setText(donorAddress);

            copyAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("book donor phone", donorPhone);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DonationDetailsAcceptActivity.this, "Phone number copied", Toast.LENGTH_SHORT).show();
                }
            });

            callAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dial = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", donorPhone, null));
                    startActivity(dial);
                }
            });

            contactsAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addContact = new Intent(ContactsContract.Intents.Insert.ACTION);
                    addContact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    addContact.putExtra(ContactsContract.Intents.Insert.PHONE, donorPhone).putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).putExtra(ContactsContract.Intents.Insert.NAME, donorName);
                    startActivity(addContact);
                }
            });

        }


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 accept the donation here
                // Toast.makeText(getApplicationContext(),"Donation Accepted",Toast.LENGTH_LONG).show();

                final ProgressDialog sProgressDialog = new ProgressDialog(DonationDetailsAcceptActivity.this);
                sProgressDialog.setMessage("Just a moment...");
                sProgressDialog.setTitle("Processing");
                sProgressDialog.setCancelable(false);
                sProgressDialog.show();

                DocumentReference cancelRef = mFirestore.collection("bannedUsers").document(curUserPhone);
                cancelRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (sProgressDialog.isShowing())
                                    sProgressDialog.dismiss();

                                try {
                                    boolean flag = false; // to check if last donation reject of the month
                                    if (documentSnapshot != null && documentSnapshot.exists()) {

                                        Map<String, Object> result = documentSnapshot.getData();

                                        SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMM_yyyy", Locale.getDefault());
                                        String currentMonth = currentMonthFormat.format(new Date());
                                        if (result.get("volMonth") != null && currentMonth.equalsIgnoreCase(result.get("volMonth").toString()) && result.get("volCount") != null && Integer.parseInt(result.get("volCount").toString()) > 3) {
                                            flag = true;
                                        }
                                    }

                                    if (flag) {
                                        final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                                        cBuilder.setTitle("Cannot Accept Donation!");
                                        cBuilder.setMessage("You have exceeded the donation abandon limit for this Month. Try again next Month.");
                                        cBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        cBuilder.show();

                                    } else {

                                        final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                                        cBuilder.setTitle("Confirm Accept");
                                        cBuilder.setMessage("By clicking Confirm, you agree that you will be assigned this donation and will coordinate with the donor to pick the material up.");
                                        cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                donationRef.update("acceptedByName", curUserName, "acceptedByPhone", curUserPhone, "status", 2,"acceptedAt",new Date().getTime())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        if (sProgressDialog.isShowing())
                                                            sProgressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(DonationDetailsAcceptActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }
                                        });
                                        cBuilder.show();
                                    }

                                } catch (Exception e) {
                                    if (sProgressDialog.isShowing())
                                        sProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (sProgressDialog.isShowing())
                                    sProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 accept the donation here
                // Toast.makeText(getApplicationContext(),"Donation Accepted",Toast.LENGTH_LONG).show();

                final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                cBuilder.setTitle("Confirm Complete");
                cBuilder.setMessage("You will now provide details regarding the books you have received and will send them for approval.");
                cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        Intent logBookCollection = new Intent(DonationDetailsAcceptActivity.this,LogBookCollectionActivity.class);
                        logBookCollection.putExtra("DON_DOC_NAME",don_id);
                        logBookCollection.putExtra("CUR_USER_NAME",curUserName);
                        logBookCollection.putExtra("CUR_USER_PHONE",curUserPhone);
//                        logBookCollection.putExtra("DON_TITLE",donTitle);
//                        logBookCollection.putExtra("DON_DESC",donDesc);
//                        logBookCollection.putExtra("DON_PIC",donPic);
//                        logBookCollection.putExtra("DON_STATUS",donStatus);
//                        logBookCollection.putExtra("DON_DONORNAME",donorName);
//                        logBookCollection.putExtra("DON_PHONE",donorPhone);
//                        logBookCollection.putExtra("DON_ADDRESS",donorAddress);
//                        logBookCollection.putExtra("DON_LISTDATE",listDate);

                        startActivity(logBookCollection);
                    }
                });
                cBuilder.show();
            }
        });

    }


    //    calculate and show distance from volunteer to donation
    private void addDistance() {
        float res;
        if (distanceView==null) {
            distanceView = findViewById(R.id.donDist);
        }
        if (user_lat != 0.0 && user_lng != 0.0 && don_lat != 0.0 && don_lng != 0.0) {
            Location locationA = new Location("point A");
            Location locationB = new Location("point B");

            locationA.setLatitude(user_lat);
            locationA.setLongitude(user_lng);
            locationB.setLatitude(don_lat);
            locationB.setLongitude(don_lng);
            res = locationA.distanceTo(locationB);
            if (res > 0.0f && res < 1000f) {
                res = Math.round(res);
                if (res > 0.0f)
                    distanceView.append((int) res + " m");
            } else if (res > 1000f) {
                res = Math.round(res / 100);
                res = res / 10;
                if (res > 0.0f)
                    distanceView.append(res + " km");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (donStatus == 2) {
            getMenuInflater().inflate(R.menu.menu_donation_request_details, menu);
            this.menu = menu;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.abandonDonation) {
            final ProgressDialog sProgressDialog = new ProgressDialog(DonationDetailsAcceptActivity.this);
            sProgressDialog.setMessage("Just a moment...");
            sProgressDialog.setTitle("Processing");
            sProgressDialog.setCancelable(false);
            sProgressDialog.show();
            DocumentReference cancelRef = mFirestore.collection("bannedUsers").document(curUserPhone);
            cancelRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                if (sProgressDialog.isShowing())
                                    sProgressDialog.dismiss();

                                boolean flag = false; // to check if last donation reject of the month

                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    newEntry = false;
                                    Map<String, Object> result = documentSnapshot.getData();

                                    SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMM_yyyy", Locale.getDefault());
                                    String currentMonth = currentMonthFormat.format(new Date());
                                    //check for cancel count for current month
                                    if (result.get("volMonth") != null && currentMonth.equalsIgnoreCase(result.get("volMonth").toString()) && result.get("volCount") != null && Integer.parseInt(result.get("volCount").toString()) == 3) {
                                        flag = true;
                                    }

                                    //check for new/current month
                                    if (result.get("volMonth") != null && !currentMonth.equalsIgnoreCase(result.get("volMonth").toString())) {
                                        resetCounter = true;
                                    }


                                }


                                if (flag) {
                                    // last time reject alert dialog
                                    final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                                    cBuilder.setTitle("Confirm Reject");
                                    cBuilder.setMessage("Are you sure? You wont be able to accept donations for this month.");
                                    cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            rejectDonation();
                                        }
                                    });
                                    cBuilder.show();

                                } else {
                                    final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                                    cBuilder.setTitle("Confirm Reject");
                                    cBuilder.setMessage("By clicking Confirm, you will be unassigned from this donation request and it will be available for acceptance by other volunteers. Do keep in mind that you can abandon requests a limited number of times every month.");
                                    cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            rejectDonation();
                                        }
                                    });
                                    cBuilder.show();
                                }
                            } catch (Exception e) {
                                if (sProgressDialog.isShowing())
                                    sProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to reject. Please try again after some time", Toast.LENGTH_SHORT).show();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (sProgressDialog.isShowing())
                                sProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to reject. Please try again after some time", Toast.LENGTH_SHORT).show();

                        }
                    });

        }
        return true;
    }

    private void rejectDonation() {
        final ProgressDialog sProgressDialog = new ProgressDialog(DonationDetailsAcceptActivity.this);
        sProgressDialog.setMessage("Just a moment...");
        sProgressDialog.setTitle("Processing");
        sProgressDialog.setCancelable(false);
        sProgressDialog.show();
        try {
            donationRef.update("acceptedByName", null, "acceptedByPhone", null, "status", 1)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            final DocumentReference cancelRef = mFirestore.collection("bannedUsers").document(curUserPhone);

                            SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMM_yyyy", Locale.getDefault());
                            String currentMonth = currentMonthFormat.format(new Date());
                            if (newEntry) {
                                // record the cancellation
                                HashMap<String, Object> o = new HashMap<>();
                                o.put("volMonth", currentMonth);
                                o.put("volCount", 1);
                                cancelRef.set(o)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure:cancelAcceptedDonation ", e);
                                        if (sProgressDialog.isShowing())
                                            sProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                // record the cancellation
                                if (resetCounter) {
                                    cancelRef.update("volMonth", currentMonth, "volCount", 1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.d(TAG, "onFailure:cancelAcceptedDonation ",e);
                                            if (sProgressDialog.isShowing())
                                                sProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    cancelRef.update("volMonth", currentMonth, "volCount", FieldValue.increment(1))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.d(TAG, "onFailure:cancelAcceptedDonation ",e);
                                            if (sProgressDialog.isShowing())
                                                sProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (sProgressDialog.isShowing())
                        sProgressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(DonationDetailsAcceptActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            if (sProgressDialog.isShowing())
                sProgressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
            Toast.makeText(DonationDetailsAcceptActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        try {
            donationRef = mFirestore.collection("donations").document(don_id);
            curUserPhone = userPref.getString(getString(R.string.p_userphone), "");
            curUserName = userPref.getString(getString(R.string.p_firstname), "");
            user_lat = userPref.getFloat(getString(R.string.p_lat), 0.0f);
            user_lng = userPref.getFloat(getString(R.string.p_lng), 0.0f);
            addDistance();
            String lastName = userPref.getString(getString(R.string.p_lastname), null);
            if (lastName != null) {
                curUserName += " " + lastName;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
