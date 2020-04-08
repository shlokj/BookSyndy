package com.booksyndy.academics.android;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.booksyndy.academics.android.Data.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "USERPROFILEACTIVITY";

    private ImageView profilePic;
    private EditText fName, lName, year, phoneNo, uName;
    private Spinner gradeSpinner, boardSpinner, degreeSpinner;
    private FloatingActionButton saveChanges;
    private TextWatcher checkChange;
    private Menu menu;
    private int gradeNumber, boardNumber, yearNumber, userType;
    private CheckBox compExams, preferGuidedMode, pnpcb;
    private TextView boardLabel;
    private LinearLayout gradeLL,boardLL,yearLL;
    private boolean detailsChanged = false, tempCE, editing, phoneNumberPublic;
    private FirebaseFirestore mFirestore;
    private ArrayAdapter<String> boardAdapter, degreeAdapter, gradeAdapter;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private String firstName, lastName, phoneNumber, userId, iUsername;
    private String imageFilePath;
    private Uri selectedImageUri;
    private StorageReference bookPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private List<String> userNamesList;
    private String book_photo_url = "https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/default_photos%2Fdefault_user_dp.png?alt=media&token=23b43df7-8143-4ad7-bb87-51e49da095c6";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("Profile");

        userNamesList = new ArrayList<>();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mFirebaseStorage = FirebaseStorage.getInstance();
        bookPhotosStorageReference = mFirebaseStorage.getReference().child("user_photos");

        profilePic = findViewById(R.id.profilePic);
        preferGuidedMode = findViewById(R.id.preferGuidedMode);
        pnpcb = findViewById(R.id.phoneNumberPublic_p);
        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);

        fName = findViewById(R.id.firstNameProfile);
        lName = findViewById(R.id.lastNameProfile);
        uName = findViewById(R.id.usernameField);
        year = findViewById(R.id.profileYearField);
        phoneNo = findViewById(R.id.profilePhoneNumberField);

        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.profileBoardSpinner);
        degreeSpinner = findViewById(R.id.degreeSpinner);

        boardLabel = findViewById(R.id.boardLabel);

        gradeLL = findViewById(R.id.gradeLL);
        boardLL = findViewById(R.id.boardLL);
        yearLL = findViewById(R.id.profileYearLL);

        gradeAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        boardAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        degreeAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.degrees));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        compExams = findViewById(R.id.profileCompetitiveExams);

        checkChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                detailsChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mFirestore = FirebaseFirestore.getInstance();
        fetchUserNameList();
        getUserPreference();
        populateUserDetails();
        iUsername = uName.getText().toString().toLowerCase().trim();
        fName.addTextChangedListener(checkChange);
        lName.addTextChangedListener(checkChange);
        uName.addTextChangedListener(checkChange);
        year.addTextChangedListener(checkChange);

        //profilePic.setImageDrawable(getDrawable(R.drawable.ic_account_circle_24px));

        fName.setEnabled(false);
        lName.setEnabled(false);
        uName.setEnabled(false);
        year.setEnabled(false);
        compExams.setEnabled(false);
        gradeSpinner.setEnabled(false);
        boardSpinner.setEnabled(false);
        degreeSpinner.setEnabled(false);
        preferGuidedMode.setEnabled(false);
        profilePic.setEnabled(false);
        pnpcb.setEnabled(false);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
//                builder.setTitle("Select Pic Using...");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            openCameraIntent();
                        } else if (options[item].equals("Choose from Gallery")) {

                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 1);

                        }
                    }
                });

                builder.show();
            }
        });


        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position < 6) { //school grade selected
                    boardLabel.setText("Board");
                    yearLL.setVisibility(View.GONE);
                    if (boardSpinner.getAdapter() != boardAdapter) {
                        boardSpinner.setAdapter(boardAdapter);
                    }
                    if (position == 4 || position == 5) {
                        compExams.setVisibility(View.VISIBLE);
                        compExams.setChecked(tempCE);
                    } else {
                        compExams.setVisibility(View.GONE);
                        compExams.setChecked(false);
                    }
                } else { //undergrad selected
                    boardLabel.setText("Degree / course");
                    yearLL.setVisibility(View.VISIBLE);
                    if (boardSpinner.getAdapter() != degreeAdapter) {
                        boardSpinner.setAdapter(degreeAdapter);
                    }
                    compExams.setVisibility(View.GONE);
                    compExams.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }

    private void getUserPreference() {

        preferGuidedMode.setChecked(userPref.getBoolean(getString(R.string.preferGuidedMode), false));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                saveChangesAndExit();
                break;
            case android.R.id.home:
                showSaveDialog();
                break;
        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        showSaveDialog();
//        super.onBackPressed();
    }

    public void finishActivity1() {
        this.finish();
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

    private void populateUserDetails() {

        try {
            phoneNumber = userPref.getString(getString(R.string.p_userphone), "");

            gradeNumber = userPref.getInt(getString(R.string.p_grade), 2);
            boardNumber = userPref.getInt(getString(R.string.p_board), 2);
            yearNumber = userPref.getInt(getString(R.string.p_year), 0);
            book_photo_url = userPref.getString(getString(R.string.p_imageurl), book_photo_url);
            firstName = userPref.getString(getString(R.string.p_firstname), "");
            lastName = userPref.getString(getString(R.string.p_lastname), "");
            userId = userPref.getString(getString(R.string.p_userid), "");
            userType = userPref.getInt(getString(R.string.p_usertype),1);
            phoneNumberPublic = userPref.getBoolean(getString(R.string.phoneNumberPublic),false);

            pnpcb.setChecked(phoneNumberPublic);
            gradeSpinner.setAdapter(gradeAdapter);
            gradeSpinner.setSelection(gradeNumber - 1);

//                        Toast.makeText(getApplicationContext(),"Grade number: "+gradeNumber,Toast.LENGTH_SHORT).show();

            if (gradeNumber >= 1 && gradeNumber <= 6) {

//                            Toast.makeText(getApplicationContext(),"School",Toast.LENGTH_LONG).show();

                boardLabel.setText("Board");
                findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.GONE);

                if (gradeNumber == 5 || gradeNumber == 6) {
                    compExams.setVisibility(View.VISIBLE);
                    tempCE = userPref.getBoolean(getString(R.string.p_competitive), false);
//                                Toast.makeText(getApplicationContext(),"Competitive exam: "+tempCE,Toast.LENGTH_SHORT).show();
                    compExams.setChecked(tempCE);
                } else {
                    compExams.setVisibility(View.GONE);
                }

                boardSpinner.setAdapter(boardAdapter);
                boardSpinner.setSelection(boardNumber - 1);
            } else {

                boardLabel.setText("Degree / course");

                boardSpinner.setAdapter(degreeAdapter);
                boardSpinner.setSelection(boardNumber - 7);

                year.setText(yearNumber + "");
            }

            if (userType==4) {
                boardLL.setVisibility(View.GONE);
                gradeLL.setVisibility(View.GONE);
                compExams.setVisibility(View.GONE);
                year.setVisibility(View.GONE);
                getSupportActionBar().setTitle("Profile (vendor)");
            }
            fName.setText(firstName);
            lName.setText(lastName);
            uName.setText(userId);
            phoneNo.setText(phoneNumber);
            Glide.with(profilePic.getContext())
                    .load(book_photo_url)
                    .into(profilePic);

        } catch (Exception e) {
            Log.e(TAG, "PopulateUserDetails method failed with  ", e);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:// camera intent
                if (resultCode == RESULT_OK) {
                    File f = new File(imageFilePath);
                    selectedImageUri = FileProvider.getUriForFile(UserProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);
                    CropImage(selectedImageUri);

                }
                break;

            case 1:// gallery intent
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    selectedImageUri = imageReturnedIntent.getData();
                    CropImage(selectedImageUri);
                }
                break;
            //new crop image
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                        profilePic.setImageBitmap(bitmap);
                        Glide.with(profilePic.getContext())
                                .load(resultUri)
                                .into(profilePic);

                        storeBookImage(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, "onActivityResult: CROP ERROR:", e);
                        Toast.makeText(this, "CROP ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "CROP ERROR:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private File createImageFile() throws IOException {

        String imageFileName = "DP" + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.booksyndy.academics.android.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent, 0);
            }
        }
    }

    private void storeBookImage(Bitmap bmp) {

        byte[] compressedImage = CompressResizeImage(bmp);

        //show progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        try {
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HH",
                            Locale.getDefault()).format(new Date());
            // Get a reference to store file at book_photos/<FILENAME>
            final StorageReference photoRef = bookPhotosStorageReference.child(timeStamp + "_" + selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            UploadTask uploadTask = photoRef.putBytes(compressedImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "then: failure download url", task.getException());
                        progressDialog.dismiss();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        book_photo_url = task.getResult().toString();
//                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.d(TAG, "onComplete: success url: " + book_photo_url);
                    } else {
                        // Handle failures
                        progressDialog.dismiss();
                        Log.e(TAG, "onComplete: failure", task.getException());
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    protected void CropImage(Uri picUri) {
        try {
            CropImage.activity(picUri)
                    .setAspectRatio(1, 1)
//                    .setMaxCropResultSize(512,512)
                    .setRequestedSize(512, 512)

                    .start(this);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();

        }
    }

    public byte[] CompressResizeImage(Bitmap bm) {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int ivWidth = profilePic.getWidth();
        int ivHeight = profilePic.getHeight();

        int new_height = (int) Math.floor((double) bmHeight * ((double) ivWidth / (double) bmWidth));
        Bitmap newbitMap = Bitmap.createScaledBitmap(bm, ivWidth, new_height, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newbitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return b;
    }

    private void fetchUserNameList() {
        mFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: usernames fetch error", e);
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (User user : queryDocumentSnapshots.toObjects(User.class)) {
                        userNamesList.add(user.getUserId());
                        //Log.d(TAG, "onEvent: username:"+user.getUserId()+"...");
                    }

                }

            }
        });
    }

    private boolean checkUserName(String username) {
        // Log.d(TAG, "checkUserName: "+username);
        if (!username.equalsIgnoreCase(iUsername)) {
            for (String userId : userNamesList) {
                if (userId.equalsIgnoreCase(username)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_blue_light))
                .show();
    }

    public void saveChangesAndExit() {

        if (!editing) {
            fName.setEnabled(true);
            lName.setEnabled(true);
            uName.setEnabled(true);
            year.setEnabled(true);
            compExams.setEnabled(true);
            gradeSpinner.setEnabled(true);
            boardSpinner.setEnabled(true);
            degreeSpinner.setEnabled(true);
            preferGuidedMode.setEnabled(true);
            profilePic.setEnabled(true);
            pnpcb.setEnabled(true);
            // phoneNumber.setEnabled(true);
//            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_24px)).setTitle("Save changes");
            menu.findItem(R.id.edit_profile).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_24px))
                    .setTitle("Save changes");
            editing = true;

        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            // phoneNumber.setEnabled(false);

//                    clickCount = clickCount + 1;

            if (!checkConnection(this)) {
                showSnackbar("Internet is required");
                return;
            }

            String nUsername = uName.getText().toString().toLowerCase().trim();
            if (!(fName.getText().toString().length() == 0 || lName.getText().toString().length() == 0 || nUsername.length() == 0 /*|| year.getText().toString().length()==0*/)) {
                boolean isAvailableUsername = checkUserName(nUsername);

                if (nUsername.length()<4) {
                    showSnackbar("This username is too short.");
//                    return;
                }
                else if (!isAvailableUsername) {
                    showSnackbar("This username is not available. Please try another");
                    return;
                } else {
                    progressDialog.setTitle("Saving your changes");
                    progressDialog.setMessage("Updating your profile");
                    progressDialog.setCancelable(false);

                    int board;
                    if (gradeSpinner.getSelectedItemPosition() >= 6) {
                        board = boardSpinner.getSelectedItemPosition() + 7;
                        String ys = year.getText().toString();
                        if (!(ys == null || ys.length() == 0)) {
                            yearNumber = Integer.parseInt(year.getText().toString());
                            progressDialog.show();
                        } else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Please enter a year", Snackbar.LENGTH_SHORT)
                                    .setAction("OKAY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                            return;
                        }
                    } else {
                        board = boardSpinner.getSelectedItemPosition() + 1;
                    }
//                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_24px))
//                            .setTitle("Edit profile");
                    menu.findItem(R.id.edit_profile).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_24px))
                            .setTitle("Edit profile");
                    fName.setEnabled(false);
                    lName.setEnabled(false);
                    uName.setEnabled(false);
                    year.setEnabled(false);
                    compExams.setEnabled(false);
                    gradeSpinner.setEnabled(false);
                    boardSpinner.setEnabled(false);
                    degreeSpinner.setEnabled(false);
                    preferGuidedMode.setEnabled(false);
                    profilePic.setEnabled(true);
                    progressDialog.show();


                    editor = userPref.edit();
                    editor.putBoolean(getString(R.string.preferGuidedMode), preferGuidedMode.isChecked());
                    editor.putString(getString(R.string.p_firstname), fName.getText().toString().trim());
                    editor.putString(getString(R.string.p_lastname), lName.getText().toString().trim());
                    editor.putString(getString(R.string.p_userid), nUsername);
                    editor.putString(getString(R.string.p_imageurl), book_photo_url);
                    editor.putInt(getString(R.string.p_grade), gradeSpinner.getSelectedItemPosition() + 1);
                    editor.putInt(getString(R.string.p_board), board);
                    editor.putInt(getString(R.string.p_year), yearNumber);
                    editor.putBoolean(getString(R.string.p_competitive), compExams.isChecked());
                    editor.putBoolean(getString(R.string.phoneNumberPublic),pnpcb.isChecked());
                    editor.apply();
                    DocumentReference userReference = mFirestore.collection("users").document(phoneNumber);
                    userReference.update("competitiveExam", compExams.isChecked(),
                            "year", yearNumber,
                            "firstName", fName.getText().toString().trim(),
                            "lastName", lName.getText().toString().trim(),
                            "gradeNumber", gradeSpinner.getSelectedItemPosition() + 1,
                            "boardNumber", board,
                            "userId", uName.getText().toString().toLowerCase().trim(),
                            "imageUrl", book_photo_url,
                            "phoneNumberPublic",pnpcb.isChecked())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
                            startActivity(homeIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.d(TAG, "onFailure: update user", e);
                            Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Please fill in all fields", Snackbar.LENGTH_SHORT)
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

    public void showSaveDialog() {

        if (detailsChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
            builder.setTitle("Save your changes?");
            builder.setMessage("Would you like to save the changes you made to your profile?");
            builder.setPositiveButton("Save and exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
//                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
//                            startActivity(homeIntent);
                    saveChangesAndExit();
                }
            });
            builder.setNegativeButton("Exit without saving", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishActivity1();
                }
            });
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        } else {
            finishActivity1();
        }
    }

}
