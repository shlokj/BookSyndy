package com.booksyndy.academics.android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "HOMEACTIVITY" ;

    /*location variables */
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    private LocationManager locationManager;
    private NavigationView navigationView;

    private TextView navUsername,navUserphone;
    private ImageView navUserPic;
    private FirebaseFirestore mFirestore;
    private String curUserId, snackbarMessage;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private boolean  sbLong;
    private static boolean showGPS = false;
    private String dynamicBookId;
    private NavController navController;
    private Toolbar toolbar;

    //TODO: use showcase view to showcase fab and button to open navigation drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_requests,R.id.nav_booklist, R.id.nav_chats,
                R.id.nav_starred, R.id.nav_help, R.id.nav_signout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView = navigationView.getHeaderView(0);
        navUsername =  headerView.findViewById(R.id.user_title);
        navUserphone = headerView.findViewById(R.id.user_phone);
        navUserPic = headerView.findViewById(R.id.nav_profilePic);
        navUsername.setOnClickListener(this);
        navUserphone.setOnClickListener(this);
        dynamicBookId = getIntent().getStringExtra("dynamicBookId");
        snackbarMessage = getIntent().getStringExtra("SNACKBAR_MSG");
        sbLong = getIntent().getBooleanExtra("SB_LONG",false);
        if (snackbarMessage!=null) {
            if (snackbarMessage.length() > 0) {
                View parentLayout = findViewById(android.R.id.content);
                if (sbLong) {
                    Snackbar sb = Snackbar.make(parentLayout, snackbarMessage, Snackbar.LENGTH_LONG);
                    if (snackbarMessage.contains("Your listings")) {
                        sb.setAction("GO THERE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO: open the your listings fragment

                            }
                        });
                    }
                    sb.setActionTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    sb.show();
                }
                else {
                    Snackbar sb = Snackbar.make(parentLayout, snackbarMessage, Snackbar.LENGTH_SHORT);
                    if (snackbarMessage.contains("Your listings")) {
                        sb.setAction("GO THERE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                    }
                    sb.setActionTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    sb.show();
                }
            }
        }

        populateUserDetails();
        handleOtherIntent();

    }

    private void handleOtherIntent() {
        if (dynamicBookId != null) {
            Intent bookDetails = new Intent(HomeActivity.this, BookDetailsActivity.class);
            bookDetails.putExtra("bookid", dynamicBookId);
            bookDetails.putExtra("isHome", true);
            startActivity(bookDetails);
        }
//        else if (getIntent().getBooleanExtra("openChat", false)) {
//            navigationView.setCheckedItem(R.id.nav_chats);
//            navController.navigate(R.id.nav_chats);
//        }

    }

    private void populateUserDetails() {

        String userPhone = userPref.getString(getString(R.string.p_userphone),"");
        String userId = userPref.getString(getString(R.string.p_userid),"");
        String userPic = userPref.getString(getString(R.string.p_imageurl), "");
        if (!TextUtils.isEmpty(userPic)) {
            Glide.with(navUserPic.getContext())
                    .load(userPic)
                    .into(navUserPic);
        }
        navUsername.setText(userId);
        navUserphone.setText(userPhone);

    }

    @Override
    protected void onStart() {
        super.onStart();
        /* location service */
        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };

        if(!showGPS) {
            startLocationUpdates();
            showGPS = true;
        }
            //showGpsSettingDialog();


    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();

        } else {
            getSupportFragmentManager().popBackStack();
        }

    }



    /* location classes */

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: entered");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
                showGpsSettingDialog();
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            currentLocation = location;
                            getAddress();
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("add_location", "Error trying to get last GPS location");
                                Toast.makeText(HomeActivity.this,
                                        "Error trying to get last GPS location",
                                        Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });


        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
//            Toast.makeText(HomeActivity.this,
//                    "Can't find current address, ",
//                    Toast.LENGTH_SHORT).show();
//
            return;
        }


        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);


        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }
                Log.d(TAG, "getAddress: "+strAddress);
                editor = userPref.edit();
                editor.putFloat(getString(R.string.p_lat),(float)currentLocation.getLatitude());
                editor.putFloat(getString(R.string.p_lng),(float)currentLocation.getLongitude());
                editor.putString(getString(R.string.p_area),fetchedAddress.getSubLocality());
                editor.putString(getString(R.string.p_city),fetchedAddress.getLocality());
                editor.apply();

            } else {
//                Toast.makeText(HomeActivity.this,
//                        "Address not found" ,
//                        Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(HomeActivity.this,
//                    "Address not found" ,
//                    Toast.LENGTH_SHORT).show();
            return;
        }


//        Intent intent = new Intent(this, GetAddressIntentService.class);
//        intent.putExtra("add_receiver", addressResultReceiver);
//        intent.putExtra("add_location", currentLocation);
//        startService(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates();
                } else {
                    Toast.makeText(this, "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    private void showGpsSettingDialog() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> task=LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    startLocationUpdates();

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        HomeActivity.this,
                                        101);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        //Toast.makeText(HomeActivity.this,states.isLocationPresent()+"",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(HomeActivity.this,"GPS SERVICE DISABLED",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_home) {
            if (toolbar != null && toolbar.getChildCount() > 1) {
                final View view = toolbar.getChildAt(1);
                /*
                new MaterialShowcaseView.Builder(this)
                        .setTarget(view)
                        .setDismissText("GOT IT")
                        .setContentText("This is some amazing feature you should know about")
                        .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                        .singleUse("100") // provide a unique ID used to ensure it is only shown once
                        .show();

*/
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(200); // half second between each showcase view

                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "Home showcase");

                sequence.setConfig(config);

                sequence.addSequenceItem(view,
                        "Click here to open the menu", "GOT IT");

                sequence.addSequenceItem(findViewById(R.id.fab_home),
                        "Click here to post a listing", "GOT IT");

                sequence.start();
            }
        }

            return super.onPrepareOptionsMenu(menu);
        }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_signout:
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        Intent profileIntent = new Intent(HomeActivity.this,UserProfileActivity.class);
        profileIntent.putExtra("userid",curUserId);
        startActivity(profileIntent);

    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                //Last Location can be null for various reasons
                //for example the api is called first time
                //so retry till location is set
                //since intent service runs on background thread, it doesn't block main thread
                Log.d("Address", "Location null retrying");
                getAddress();
                return;

            }

            if (resultCode == 1) {
                Toast.makeText(HomeActivity.this,
                        "Address not found" ,
                        Toast.LENGTH_SHORT).show();
            return;
            }

            //String currentAdd = resultData.getString("address_result");

            Map<String, Object> address = new HashMap<>();
            address.put("addr1", resultData.getString("addr1"));
            address.put("addr2", resultData.getString("addr2"));
            address.put("locality", resultData.getString("locality"));
            address.put("county", resultData.getString("county"));
            address.put("state", resultData.getString("state"));
            address.put("country", resultData.getString("country"));
            address.put("post", resultData.getString("post"));
            address.put("lat",resultData.getDouble("lat"));
            address.put("lng",resultData.getDouble("lng"));
            editor = userPref.edit();
            editor.putFloat(getString(R.string.p_lat),(float)resultData.getDouble("lat"));
            editor.putFloat(getString(R.string.p_lng),(float)resultData.getDouble("lng"));
            editor.putString(getString(R.string.p_area),resultData.getString("addr2"));
            editor.putString(getString(R.string.p_city),resultData.getString("locality"));
            editor.apply();
           // showResults(address);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);
//        Toast.makeText(HomeActivity.this, "onResume", Toast.LENGTH_SHORT).show();
//        populateUserDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
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

}
