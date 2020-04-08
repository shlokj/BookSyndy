package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.booksyndy.academics.android.Data.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SignUpQuestionsActivity extends AppCompatActivity {

    private int currentQuestionNumber = 1;
    private View.OnClickListener changeQ;

    private boolean isValidUsername, isAvailableUsername=true, isParent, validYear, phoneNumberPublic, preferGeneral, temp1 = false, temp2 = false, compExams, btp;
    private String firstName, lastName, username;
    private int gradeNumber, boardNumber, degree, degreeNumber, yearNumber, userType, studOrPar, grade, board;
    Intent registerUser;

    private EditText firstNameField, lastNameField, userIdField, yearField;
    private TextInputLayout fnf,lnf,uif;
    private CheckBox pnpcb;
    private TextWatcher pUsername;

    private FirebaseFirestore mFireStore;
    private List<String> userNamesList;

    private static final String TAG = "SIGNUP_QUESTIONS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeQ = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionNumber = currentQuestionNumber + 1;
            }
        };

        getSupportActionBar().setTitle("Sign up");

        changeQuestionAndGetAnswer();


    }
    private void changeQuestionAndGetAnswer() {

        LayoutInflater inflater=getLayoutInflater();
        View view1;

        switch (currentQuestionNumber) {

            case 1:
                setContentView(R.layout.activity_cust_name);

                userIdField = (EditText) findViewById(R.id.usernameField);
                firstNameField = (EditText) findViewById(R.id.firstName);
                lastNameField = (EditText) findViewById(R.id.lastName);
                uif = findViewById(R.id.usernameTIL);
                pnpcb = findViewById(R.id.publicPhoneCB);

                pnpcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        phoneNumberPublic = isChecked;
                    }
                });
                pUsername = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        userIdField.setText(firstNameField.getText().toString().toLowerCase()+lastNameField.getText().toString().toLowerCase());
                        uif.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                userIdField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        uif.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            lastNameField.requestFocus();
                        }
                        return false;
                    }
                });
                lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            userIdField.requestFocus();
                        }
                        return false;
                    }
                });

                firstNameField.addTextChangedListener(pUsername);
                lastNameField.addTextChangedListener(pUsername);

                userNamesList = new ArrayList<>();
                initFirestore();

                findViewById(R.id.fab4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firstName = firstNameField.getText().toString().trim();
                        lastName = lastNameField.getText().toString().trim();
                        username = userIdField.getText().toString().trim().toLowerCase();
                        phoneNumberPublic = pnpcb.isChecked();

                        // todo: last char can't be a dot.
                        isValidUsername = false;
                        isValidUsername = (username != null) && username.matches("[A-Za-z0-9_.]+");
                        isAvailableUsername = checkUserName(username);
                        if (firstName.length()==0 || lastName.length()==0) {
                            showSnackbar("Please fill in both name fields");
                        }

                        else if (!(isValidUsername && isAvailableUsername)) {

                            if (!isValidUsername) {
                                uif.setError("The username you entered is not valid.");

                            }
                            else if (!isAvailableUsername) {
                                uif.setError("This username is taken. Please try another.");
                            }
                        }

                        else if (username.length()<4) {
                            uif.setError("This username is too short.");
                        }
                        else {
                            currentQuestionNumber = currentQuestionNumber + 1;
                            changeQuestionAndGetAnswer();
                        }
                    }
                });
                break;

            case 2:
                if (btp) {
                    setContentView(R.layout.activity_get_mode);
                }
                else {
                    view1 = inflater.inflate(R.layout.activity_get_mode, null, false);
                    view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                    setContentView(view1);
                }
                btp = false;

//                setContentView(R.layout.activity_get_mode);

                final RadioGroup genOrAcad = findViewById(R.id.modesRadioGroup);

                findViewById(R.id.fab2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int goa = genOrAcad.getCheckedRadioButtonId();
                        if (goa==-1) {
                            temp1 = false;
                            showSnackbar("Please select an option");
                        }
                        else if (goa==R.id.genOption) {
                            preferGeneral = true;
                            temp1 = true;
                        }
                        else if (goa==R.id.acadsOption){
                            preferGeneral = false;
                            temp1 = true;
                        }

                        if (temp1) {
                            currentQuestionNumber = currentQuestionNumber + 1;
                            changeQuestionAndGetAnswer();
                        }
                    }
                });


                break;

            case 3:

                if (btp) {
                    setContentView(R.layout.activity_par_or_stud);
                }
                else {
                    view1 = inflater.inflate(R.layout.activity_par_or_stud, null, false);
                    view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                    setContentView(view1);
                }
                btp = false;

//                setContentView(R.layout.activity_par_or_stud);

                final RadioGroup studOrParButtons = findViewById(R.id.studentOrParentRadioGroup);

                findViewById(R.id.fab3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        studOrPar = studOrParButtons.getCheckedRadioButtonId();
                        if (studOrPar==-1) {
                            showSnackbar("Please select an option");
                        }
                        else if (studOrPar==R.id.studentoption) {
                            isParent=false;
                            userType = 1;
                        }
                        else if (studOrPar==R.id.parentoption){
                            isParent=true;
                            userType = 2;
                        }
                        else if (studOrPar==R.id.teacherOption){
                            userType = 3;
                        }
                        else if (studOrPar==R.id.vendorOption){
                            userType = 4;
                        }

                        if (studOrPar!=-1) {
                            currentQuestionNumber = currentQuestionNumber + 1;
                            changeQuestionAndGetAnswer();
                        }
                    }
                });
                break;

            case 4:
                if (btp) {
                    setContentView(R.layout.activity_get_grade);
                }
                else {
                    view1 = inflater.inflate(R.layout.activity_get_grade, null, false);
                    view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                    setContentView(view1);
                }
                btp = false;

                final RadioGroup grades = findViewById(R.id.gradesButtonList);

                TextView gradeQuestion = findViewById(R.id.gradeQuestionTV);
                if (userType==2) {
                    gradeQuestion.setText(R.string.parent_grade_q);
                }
                else if (userType==3) {
                    gradeQuestion.setText(R.string.which_grade_teacher);
                }

                TextView gradeInstructions = findViewById(R.id.settingGradeInstructions);

                if (userType!=1) {
                    gradeInstructions.setVisibility(View.INVISIBLE);
                }

                findViewById(R.id.fab5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        grade = grades.getCheckedRadioButtonId();
                        if (grade==-1) {
                            showSnackbar("Please select an option");
                        }
                        else if (grade==R.id.grade5OrBelow) {
                            gradeNumber=1;
                        }
                        else if (grade==R.id.grade6to8) {
                            gradeNumber=2;
                        }
                        else if (grade==R.id.grade9) {
                            gradeNumber=3;
                        }
                        else if (grade==R.id.grade10) {
                            gradeNumber=4;
                        }
                        else if (grade==R.id.grade11) {
                            gradeNumber=5;
                        }
                        else if (grade==R.id.grade12) {
                            gradeNumber=6;
                        }
                        else if (grade==R.id.university) {
                            gradeNumber=7;
                        }

                        if (gradeNumber != -1) {
                            currentQuestionNumber = currentQuestionNumber + 1;
                            changeQuestionAndGetAnswer();
                        }

                    }
                });

                break;

            case 5:

                if (gradeNumber<=6) {
                    if (btp) {
                        setContentView(R.layout.activity_get_board);
                    }
                    else {
                        view1 = inflater.inflate(R.layout.activity_get_board, null, false);
                        view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                        setContentView(view1);
                    }
                    btp = false;

                    TextView boardQuestion = findViewById(R.id.boardQuestionTV);
                    if (userType==2) {
                        boardQuestion.setText("Which board is your child studying under?");
                    }
                    else if (userType==3) {
                        boardQuestion.setText("Which board do you teach for?");
                    }

                    final RadioGroup boards = findViewById(R.id.boardsButtonList);

                    final CheckBox competitiveExam =  findViewById(R.id.competitiveExam);

                    competitiveExam.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
                    if (gradeNumber<3 || gradeNumber>6) {
                        competitiveExam.setVisibility(View.GONE);
                    }

                    findViewById(R.id.fab6).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            board = boards.getCheckedRadioButtonId();

                            compExams = competitiveExam.isChecked();

                            if (board==-1) {
                                showSnackbar("Please select an option");
                            }
                            else if (board==R.id.cbseBoard) {
                                boardNumber=1;
                            }
                            else if (board==R.id.icseBoard) {
                                boardNumber=2;
                            }
                            else if (board==R.id.ibBoard) {
                                boardNumber=3;
                            }
                            else if (board==R.id.igcseBoard) {
                                boardNumber=4;
                            }
                            else if (board==R.id.stateBoard) {
                                boardNumber=5;
                            }
                            else if (board==R.id.otherBoard) {
                                boardNumber=6;
                            }

                            if (board!=-1) {
                                currentQuestionNumber = currentQuestionNumber + 1;
                                changeQuestionAndGetAnswer();
                            }
                        }
                    });

                }

                else {
                    if (btp) {
                        setContentView(R.layout.activity_get_college_specifics);
                    }
                    else {
                        view1 = inflater.inflate(R.layout.activity_get_college_specifics, null, false);
                        view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                        setContentView(view1);
                    }
                    btp = false;

                    yearField = (EditText) findViewById(R.id.yearField);
                    yearField.setFocusable(false);

                    TextView degreeQuestion = (TextView) findViewById(R.id.degreeQuestionTV);
                    final RadioGroup degrees = (RadioGroup) findViewById(R.id.degreesButtonList);

                    if (userType==3) {
                        degreeQuestion.setText("Which degree do you teach for?");
                    }
                    degrees.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            yearField.setFocusableInTouchMode(true);
                            yearField.setFocusable(true);
                            yearField.requestFocus();
                        }
                    });

                    findViewById(R.id.fab8).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            validYear=true;
                            degree = degrees.getCheckedRadioButtonId();

                            if (degree==-1) {
                                if (yearField.getText().toString().length()==0) {
                                    showSnackbar("Please enter your degree and year");
                                }
                            }
                            else if (yearField.getText().toString().length()==0) {
                                showSnackbar("Please enter your year");
                            }
                            else if (yearField.getText().toString().length()==1) {
                                yearNumber = Integer.parseInt(yearField.getText().toString());

                                if (degree == R.id.btech) {
                                    degreeNumber = 7;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.bsc) {
                                    degreeNumber = 8;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.bcom) {
                                    degreeNumber = 9;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.ba) {
                                    degreeNumber = 10;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.bba) {
                                    degreeNumber = 11;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.bca) {
                                    degreeNumber = 12;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.bed) {
                                    degreeNumber = 13;
                                    if (yearNumber > 4 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(4);
                                    }
                                } else if (degree == R.id.llb) {
                                    degreeNumber = 14;
                                    if (yearNumber > 5 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(5);
                                    }
                                } else if (degree == R.id.mbbs) {
                                    degreeNumber = 15;
                                    if (yearNumber > 6 || yearNumber==0) {
                                        validYear = false;
                                        displaySnackbarYears(6);
                                    }
                                } else if (degree == R.id.otherDegree) {
                                    degreeNumber = 16;
                                    if (yearNumber==0) {
                                        validYear = false;
                                        showSnackbar("Your year can't be 0");
                                    }
                                }

                                if (degree!=-1 && validYear) {
                                    currentQuestionNumber = currentQuestionNumber + 1;
                                    changeQuestionAndGetAnswer();
                                }
                            }
                            else {
                                showSnackbar("Please enter your year");

                            }
                        }

                    });

                }


                break;

            case 6:
                if (btp) {
                    setContentView(R.layout.activity_get_grade);
                }
                else {
                    view1 = inflater.inflate(R.layout.activity_get_join_purpose, null, false);
                    view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                    setContentView(view1);
                }
                btp = false;



        }
    }

    @Override
    public void onBackPressed() {
        if (currentQuestionNumber>1) {
            btp = true;
            currentQuestionNumber = currentQuestionNumber - 1;
            changeQuestionAndGetAnswer();
        }
        else {
            super.onBackPressed();
        }
    }


    private boolean checkUserName(String username) {
        if(username != null){
            for(String userId:userNamesList){
                if(userId.equalsIgnoreCase(username)){
                    return false;
                }
            }
        }
        return true;
    }

    private void initFirestore() {
        mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: usernames fetch error",e );
                }
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                    for (User user:queryDocumentSnapshots.toObjects(User.class)){
                        userNamesList.add(user.getUserId());
                    }

                }

            }
        });
    }
    private void showSnackbar(String message) {
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

    private void displaySnackbarYears(int year) {
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
