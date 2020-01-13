package co.in.prodigyschool.passiton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.in.prodigyschool.passiton.Data.BookRequest;

public class RequestBookActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 108;
    private static String TAG = "REQUEST_BOOK";

    private FirebaseFirestore mFireStore;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private String userPhone,userId,bookTitle,bookDesc,bookAddress;
    private int gradeNumber,boardNumber,year;
    private boolean isTextbook,isCompetitive;
    private double bookLat,bookLng;

    private ArrayAdapter<String> gradeAdapter, boardAdapter, degreeAdapter, typeAdapter;
    private Spinner typeSpinner,gradeSpinner,boardSpinner;
    private EditText titleField, descField,yearField;
    private CheckBox competitiveExam;
    private TextView locField,boardDegreeLabel;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book);
        getSupportActionBar().setTitle("Request a book");
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);
        typeSpinner = findViewById(R.id.bookTypeSpinner_r);
        gradeSpinner = findViewById(R.id.gradeSpinner_r);
        boardSpinner = findViewById(R.id.boardSpinner_r);
        boardDegreeLabel = findViewById(R.id.boardLabel_r);
        competitiveExam = findViewById(R.id.forCompetitiveExams_r);
        titleField = findViewById(R.id.bookNameField_r);
        descField = findViewById(R.id.bookDescField_r);
        locField = findViewById(R.id.locField_r);

        yearField = findViewById(R.id.bookYearField_r);
         initFirebase();

        gradeAdapter = new ArrayAdapter<>(RequestBookActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        boardAdapter = new ArrayAdapter<>(RequestBookActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSpinner.setAdapter(boardAdapter);

        degreeAdapter = new ArrayAdapter<>(RequestBookActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.degrees));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeAdapter = new ArrayAdapter<>(RequestBookActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        competitiveExam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gradeSpinner.setEnabled(!isChecked);
                gradeSpinner.setFocusable(!isChecked);
                gradeSpinner.setFocusableInTouchMode(!isChecked);
                boardSpinner.setEnabled(!isChecked);
                boardSpinner.setFocusable(!isChecked);
                boardSpinner.setFocusableInTouchMode(!isChecked);
            }
        });

        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position<6) {
                    boardDegreeLabel.setText("Board");
                    boardSpinner.setAdapter(boardAdapter);
                    yearField.setVisibility(View.GONE);
                    if (position==4 || position==5) {
                        competitiveExam.setVisibility(View.VISIBLE);
                    }
                    else {
                        competitiveExam.setVisibility(View.GONE);
                    }
                }
                else {
                    boardDegreeLabel.setText("Degree / course");
                    boardSpinner.setAdapter(degreeAdapter);
                    yearField.setVisibility(View.VISIBLE);
                    competitiveExam.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        // to disallow enter
        titleField.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {

                for(int i = s.length(); i > 0; i--) {

                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }
        });


        gradeNumber = userPref.getInt(getString(R.string.p_grade),4);
        gradeSpinner.setSelection(gradeNumber-1);
        boardNumber = userPref.getInt(getString(R.string.p_board), 6);
        if (gradeNumber>=7) {
            boardDegreeLabel.setText("Degree / course");
            boardSpinner.setSelection(boardNumber-7);
        }
        else {
            boardDegreeLabel.setText("Board");
            boardSpinner.setSelection(boardNumber-1);
        }

        findViewById(R.id.btn_search_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchCalled();
            }
        });

        findViewById(R.id.requestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add validation and populate the fields here

                bookTitle = titleField.getText().toString();
                bookDesc = descField.getText().toString();
                bookAddress = locField.getText().toString();
                isTextbook = typeSpinner.getSelectedItemPosition()==0;
                gradeNumber = gradeSpinner.getSelectedItemPosition()+1;
                if (gradeNumber>=7) {
                    boardNumber = boardSpinner.getSelectedItemPosition() + 7;
                }
                else {
                    boardNumber = boardSpinner.getSelectedItemPosition() + 1;
                }

                String bys;
                isCompetitive = competitiveExam.isChecked();

                if (bookTitle.length()<10) {
                    showSnackbar("Please enter at least 10 characters for your book's name");
                }
                else if (bookDesc.length()<10) {
                    showSnackbar("Please enter at least 10 characters for the description");
                }
                else if (gradeNumber>=7 && yearField.getText().toString().length()==0) {
                    showSnackbar("Please enter a year for your book");
                }
                else if (bookAddress.length()==0) {
                    showSnackbar("Couldn't get your location. Please enter it manually.");
                }
                else {
                    boolean validYear = true;
                    if (gradeNumber>=7) {
//                        Toast.makeText(getApplicationContext(),"Undergrad",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"Board number: "+boardNumber,Toast.LENGTH_SHORT).show();

                        bys = yearField.getText().toString().trim();
                        if (bys.length()==0) {
                            showSnackbar("Please enter a year for your book");
                        }
                        else {
                            year = Integer.parseInt(bys);

                            if (boardNumber == 7) {

                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 8) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 9) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 10) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 11) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 12) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 13) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 14) {
                                if (year > 5 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(5);
                                }
                            } else if (boardNumber == 15) {
                                boardNumber = 15;
//                                Toast.makeText(getApplicationContext(),"MBBS",Toast.LENGTH_LONG).show();
                                if (year > 6 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(6);
                                }
                            } else if (boardNumber == 16) {
                                if (year == 0) {
                                    validYear = false;
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "Your year can't be 0", Snackbar.LENGTH_SHORT)
                                            .setAction("OKAY", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                            .show();
                                }
                            }
                        }
                        if (bys.length()==0) {
                            showSnackbar("Please enter a year for your book");
                        }
                        else {
                            year=Integer.parseInt(bys);
                        }
                    }
                    else {
                        year=0;
                    }
                    if (validYear) {
                        BookRequest bookRequest = new BookRequest(bookTitle,bookDesc,bookAddress,userPhone
                                ,userId,gradeNumber,boardNumber,year,isCompetitive,false,isTextbook);
                        postBookRequest(bookRequest);
                    }
                }

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initFirebase() {
        mFireStore = FirebaseFirestore.getInstance();
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        userId = userPref.getString(getString(R.string.p_userid),"");
        String address;
        address = userPref.getString(getString(R.string.p_area),"");
        if(address != null && !TextUtils.isEmpty(address))
            address = address + ", ";
        address  = address + userPref.getString(getString(R.string.p_city),"");
        locField.setText(address);
        String apiKey = getString(R.string.places_api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);
    }



    private void postBookRequest(BookRequest bookRequest) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setTitle("Creating your request");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String bookRequestTime = new SimpleDateFormat("dd MM yyyy HH", Locale.getDefault()).format(new Date());
        bookRequest.setTime(bookRequestTime);
        mFireStore.collection("bookRequest").add(bookRequest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(),"Book Request Posted",Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Firebase Error: Try Again",Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                finish();
            }
        });
    }



    public void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public void displaySnackbarYears(int year) {
        String yearNum = Integer.valueOf(year).toString();
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Please enter a valid year " + yearNum + " or below, and not 0", Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }


    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //NIGERIA
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(RequestBookActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                // do query with address
                locField.setText(address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(RequestBookActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }




}
