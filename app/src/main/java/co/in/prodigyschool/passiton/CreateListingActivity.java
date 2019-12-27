package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class CreateListingActivity extends AppCompatActivity {

    private static String TAG = "CREATELISTINGFULL";
    private Spinner typeSpinner,gradeSpinner,boardSpinner;

    boolean isTextbook;
    String bookName, bookDescription, phoneNumber, userId, bookAddress, bookImageUrl, selectedImage,book_photo_url;
    int gradeNumber, boardNumber;
    private int bookPrice;
    private FirebaseFirestore mFirestore;
    private CheckBox compExams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        typeSpinner = findViewById(R.id.bookTypeSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
    }

/*
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
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"Internet Required",Toast.LENGTH_LONG).show();
            return;
        }
        mFirestore = FirebaseFirestore.getInstance();
        try{
            phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference =  mFirestore.collection("users").document(phoneNumber);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    User user = snapshot.toObject(User.class);

                    if(user != null) {
                        gradeNumber = user.getGradeNumber();
                        boardNumber = user.getBoardNumber();
                        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
                        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        gradeSpinner.setAdapter(gradeAdapter);
                        gradeSpinner.setSelection(gradeNumber-1);

                        if (gradeNumber>=1 && gradeNumber<=6) {

                            findViewById(R.id.boardLL).setVisibility(View.VISIBLE);
                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.GONE);

                            ArrayAdapter<String> boardAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
                            boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            boardSpinner.setAdapter(boardAdapter);
                            boardSpinner.setSelection(boardNumber-1);
                        }

                        else {

                            findViewById(R.id.boardLL).setVisibility(View.GONE);
                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.VISIBLE);

                            ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
                            degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            degreeSpinner.setAdapter(degreeAdapter);
                            degreeSpinner.setSelection(boardNumber-7);
                        }

                        phoneNo.setText(user.getPhone().substring(3));
                        compExams.setChecked(user.isCompetitiveExam());
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
    }*/
}
