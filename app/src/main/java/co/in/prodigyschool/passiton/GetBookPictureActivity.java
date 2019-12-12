package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookPictureActivity extends AppCompatActivity {

    LinearLayout takePic, choosePic;
    ImageView takenPic, chosenPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_picture);
        getSupportActionBar().setTitle("List a book");

        takenPic = findViewById(R.id.picChosenIV);
        chosenPic = findViewById(R.id.picTakenIV);

        takePic = findViewById(R.id.takePicLL);
        choosePic = findViewById(R.id.chooseFromGalleryLL);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab19);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getBookType = new Intent(GetBookPictureActivity.this, GetBookMaterialTypeActivity.class);
                // To find an efficient way to pass on the image of the book; mostly the uri
                startActivity(getBookType);

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    takenPic.setVisibility(View.VISIBLE);
                    Uri selectedImage = imageReturnedIntent.getData();
                    takenPic.setImageURI(selectedImage);
                }
                break;

            case 1:
                if(resultCode == RESULT_OK){
                    chosenPic.setVisibility(View.VISIBLE);
                    Uri selectedImage = imageReturnedIntent.getData();
                    chosenPic.setImageURI(selectedImage);
                }
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(GetBookPictureActivity.this,HomeActivity.class);
        startActivity(homeActivity);
    }
}
