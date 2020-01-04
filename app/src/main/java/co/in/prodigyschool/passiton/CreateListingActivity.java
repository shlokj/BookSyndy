package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.User;
import co.in.prodigyschool.passiton.util.BookUtil;

public class CreateListingActivity extends AppCompatActivity {

    private static String TAG = "CREATELISTINGFULL";
    private Spinner typeSpinner,gradeSpinner,boardSpinner;
    private double book_lat,book_lng;
    boolean isTextbook, forCompExam;
    private String curUserId, bookName, bookDescription, phoneNumber, userId, bookAddress, bookImageUrl, selectedImage,book_photo_url;
    int gradeNumber, boardNumber, year;
    private int bookPrice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private TextView boardDegreeLabel;
    private Button postButton;
    private ProgressDialog progressDialog;
    private EditText nameField, descField, priceField, locField, yearField;
    private CheckBox competitiveExam, free;
    private ArrayAdapter<String> gradeAdapter, boardAdapter, degreeAdapter, typeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        typeSpinner = findViewById(R.id.bookTypeSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
        boardDegreeLabel = findViewById(R.id.boardLabel);
        postButton = findViewById(R.id.postButton);
        competitiveExam = findViewById(R.id.forCompetitiveExams);
        nameField = findViewById(R.id.bookNameField);
        descField = findViewById(R.id.bookDescField2);
        locField = findViewById(R.id.locField2);
        priceField = findViewById(R.id.priceField);
        yearField = findViewById(R.id.bookYearField1);
        free = findViewById(R.id.freeOrNot);

        getSupportActionBar().setTitle("Create a listing");

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        gradeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        boardAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSpinner.setAdapter(boardAdapter);

        degreeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.degrees));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        mAuth = FirebaseAuth.getInstance();

        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);

        // to disallow enter
        nameField.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {

                for(int i = s.length(); i > 0; i--) {

                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }
        });

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
        free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!(free.isChecked())) {
                    priceField.setEnabled(true);
                    priceField.setFocusableInTouchMode(true);
                    priceField.setFocusable(true);
                    priceField.setText("");
                    priceField.requestFocus();
                }
                else {
                    priceField.setEnabled(false);
                    priceField.setFocusableInTouchMode(false);
                    priceField.setFocusable(false);
                    priceField.setText("0");
                }
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

        autoFillGradeAndBoard();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookName = nameField.getText().toString();
                bookDescription = descField.getText().toString();
                bookAddress = locField.getText().toString();
                isTextbook = typeSpinner.getSelectedItemPosition()==0;
                gradeNumber = gradeSpinner.getSelectedItemPosition()+1;
                if (gradeNumber>=7) {
                    boardNumber = boardSpinner.getSelectedItemPosition() + 7;
                }
                else {
                    boardNumber = boardSpinner.getSelectedItemPosition() + 1;
                }
                String bps = priceField.getText().toString().trim();
                String bys;
                forCompExam = competitiveExam.isChecked();

                if (bookName.length()<10) {
                    showSnackbar("Please enter at least 10 characters for your book's name");
                }
                else if (bookDescription.length()<10) {
                    showSnackbar("Please enter at least 10 characters for the description");
                }
                else if (gradeNumber>=7 && yearField.getText().toString().length()==0) {
                    showSnackbar("Please enter a year for your book");
                }
                else if (bookAddress.length()==0) {
                    showSnackbar("Couldn't get your location. Please enter it manually.");
                }
                else if (bps.length()==0) {
                    showSnackbar("Please enter a price or give it for free");
                }
                else {
                    boolean validYear = true;
                    bookPrice = Integer.parseInt(bps);
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
//                        Toast.makeText(getApplicationContext(),"Valid, start upload",Toast.LENGTH_SHORT).show();
                        uploadBook();
                    }
                }
            }
        });
    }


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }


    private void autoFillGradeAndBoard() {
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"Internet Required",Toast.LENGTH_LONG).show();
            return;
        }
        mFirestore = FirebaseFirestore.getInstance();
        try {
            curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference =  mFirestore.collection("users").document(curUserId);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    co.in.prodigyschool.passiton.Data.User user = snapshot.toObject(User.class);
                    if(user != null) {
                        gradeNumber=user.getGradeNumber();
                        boardNumber=user.getBoardNumber();

                        gradeSpinner.setAdapter(gradeAdapter);
                        gradeSpinner.setSelection(gradeNumber-1);

                        if (gradeNumber>=7) {
                            boardDegreeLabel.setText("Degree / course");
//
//                            findViewById(R.id.boardLL).setVisibility(View.INVISIBLE);
//                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.VISIBLE);

                            boardDegreeLabel.setText("Degree / Board");
                            boardSpinner.setAdapter(degreeAdapter);
                            boardSpinner.setSelection(boardNumber-7);
                        }
                        else {
                            boardDegreeLabel.setText("Board");
                            boardSpinner.setAdapter(boardAdapter);
                            boardSpinner.setSelection(boardNumber-1);
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ",e );
                }
            });

        }
        catch(Exception e){
            Log.e(TAG, "PopulateUserDetails method failed with  ",e);
        }
    }


    private void uploadBook() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setTitle("Creating your listing");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {

            if(userId == null){
                userId = mAuth.getCurrentUser().getPhoneNumber();
            }
            Book book = BookUtil.addBook(userId,isTextbook,bookName,bookDescription,gradeNumber,boardNumber,bookPrice,bookAddress,book_lat,book_lng);
            book.setBookYear(year);
            if(selectedImage != null && !selectedImage.isEmpty()){
                // book.setBookPhoto();
                book.setBookPhoto(book_photo_url);
            }

            CollectionReference books = mFirestore.collection("books");
            books.add(book).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Log.d("Add Book","onComplete: Book added successfully");
                        progressDialog.dismiss();
                        Intent homeIntent = new Intent(CreateListingActivity.this,HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (isTextbook) {
                            homeIntent.putExtra("SNACKBAR_MSG", "Your book has been listed!");
                        }
                        else {
                            homeIntent.putExtra("SNACKBAR_MSG", "Your material has been listed!");
                        }
                        startActivity(homeIntent);
                    }
                    else{
                        Log.d(TAG, "onComplete: failed with",task.getException());
                        Toast.makeText(getApplicationContext(),"failed to add book!",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User Register Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(CreateListingActivity.this, HomeActivity.class);
        startActivity(homeActivity);
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
}
