package com.booksyndy.academics.android;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.booksyndy.academics.android.util.ForceUpdateChecker;
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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ForceUpdateChecker.OnUpdateNeededListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = HomeActivity.class.getSimpleName();
    /*location variables */
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    private NavigationView navigationView;

    private TextView navUsername, navUserphone;
    private ImageView navUserPic;
    private String snackbarMessage, version;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private boolean sbLong;
    private static boolean showGPS = false;
    private String dynamicBookId;
    private NavController navController;
    private Toolbar toolbar;
    private ImageButton nav_btn_share;
    private int dismissCount=0;
    //    public static boolean showDefaultFilters = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_requests, R.id.nav_booklist, R.id.nav_chats,
                R.id.nav_starred, R.id.nav_switchmode, R.id.nav_help, R.id.nav_share, R.id.nav_signout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.user_title);
        navUserphone = headerView.findViewById(R.id.user_phone);
        navUserPic = headerView.findViewById(R.id.nav_profilePic);
        nav_btn_share = headerView.findViewById(R.id.nav_btn_share);
        navUsername.setOnClickListener(this);
        navUserphone.setOnClickListener(this);
        nav_btn_share.setOnClickListener(this);
        dynamicBookId = getIntent().getStringExtra("dynamicBookId");
        snackbarMessage = getIntent().getStringExtra("SNACKBAR_MSG");
        sbLong = getIntent().getBooleanExtra("SB_LONG", false);
        if (snackbarMessage != null) {
            if (snackbarMessage.length() > 0) {
                View parentLayout = findViewById(android.R.id.content);
                int l = 0;
                if (sbLong) {
                    l = Snackbar.LENGTH_LONG;
                }
                else {
                    l = Snackbar.LENGTH_SHORT;
                }
                    Snackbar sb = Snackbar.make(parentLayout, snackbarMessage, l);
                if (snackbarMessage.contains("Your listings")) {
                    sb.setAction("GO THERE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            navigationView.setCheckedItem(R.id.nav_booklist);
                            navController.navigate(R.id.nav_booklist);

                        }
                    });
                }
                else if (snackbarMessage.contains("Your edits")) {
                    navigationView.setCheckedItem(R.id.nav_booklist);
                    navController.navigate(R.id.nav_booklist);
                }

                sb.setActionTextColor(getResources().getColor(android.R.color.holo_blue_light));
                sb.show();

            }
        }

        populateUserDetails();
        handleOtherIntent();

        NavigationView navigationView = findViewById(R.id.nav_view);

        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        if (userPref.getBoolean(getString(R.string.preferGeneral),false)) {
            menu.findItem(R.id.nav_switchmode).setTitle("Switch to academics mode");
        }
        else {
            menu.findItem(R.id.nav_switchmode).setTitle("Switch to general mode");
        }

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, version);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.booksyndy.academics.android");

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minute
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

    }

    private void handleOtherIntent() {
        if (dynamicBookId != null && !dynamicBookId.equalsIgnoreCase("/details?id=com.booksyndy.academics.android")) {
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

        String userPhone = userPref.getString(getString(R.string.p_userphone), "");
        String userId = userPref.getString(getString(R.string.p_userid), "");
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

        if (!showGPS) {
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
    public void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
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
//                            Toast.makeText(HomeActivity.this,
//                                    "Error trying to get last GPS location",
//                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });


        }
    }

    @SuppressWarnings("MissingPermission")
    public void getAddress() {

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

                editor = userPref.edit();
                editor.putFloat(getString(R.string.p_lat), (float) currentLocation.getLatitude());
                editor.putFloat(getString(R.string.p_lng), (float) currentLocation.getLongitude());
                editor.putString(getString(R.string.p_area), fetchedAddress.getSubLocality());
                editor.putString(getString(R.string.p_city), fetchedAddress.getLocality());
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

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

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
        super.onActivityResult(requestCode, resultCode, data);
/*        try {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        }
        catch (Exception e) {
            Snackbar.make(parentLayout, "Ran into an error", Snackbar.LENGTH_SHORT);
        }*/
        if (requestCode == 101) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    //Toast.makeText(HomeActivity.this,states.isLocationPresent()+"",Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(HomeActivity.this, "GPS SERVICE DISABLED", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_home) {
            if (toolbar != null && toolbar.getChildCount() > 1) {
                final View view = toolbar.getChildAt(1);

                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(200); // half second between each showcase view

                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "Home showcase");

                sequence.setConfig(config);

                sequence.addSequenceItem(view,
                        "Here's the menu", "GOT IT"); // TODO: change this string after gen mode is complete

                sequence.addSequenceItem(toolbar.getChildAt(2),"Search for material you need and chat with other users here.","GOT IT");

                sequence.start();

                final Activity temp = this;

                sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                    @Override
                    public void onDismiss(MaterialShowcaseView itemView, int position) {
                        dismissCount = dismissCount + 1;
                        if (dismissCount>=2) {
                            new MaterialShowcaseView.Builder(temp)
                                    .setTarget(findViewById(R.id.fab_home))
                                    .setDismissText("DO LATER")
                                    .setContentText("Get started by posting a listing!")
                                    .setDismissOnTargetTouch(true)
                                    .setDelay(200) // optional but starting animations immediately in onCreate can make them choppy
                                    .singleUse("100011") // provide a unique ID used to ensure it is only shown once
                                    .show();
                        }
                    }
                });
            }
        }
        else if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_requests) {
//            if (toolbar != null && toolbar.getChildCount() > 1) {

            if (findViewById(R.id.fab_request)!=null) {
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.fab_request))
                        .setDismissText("GOT IT")
                        .setContentText("Welcome to the requests section.\n\nUse this section to post a request if you aren't able to find what you need. Hit the plus button to post.")
                        .setDismissOnTargetTouch(true)
                        .setDelay(200)
                        .singleUse("100611")
                        .show();
            }
//            }
        }

        else if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_starred) {

            new MaterialShowcaseView.Builder(this)
                    .setTarget(findViewById(R.id.tempviewbm))
                    .setDismissText("GOT IT")
                    .setContentText("This is the bookmarks section. Listings you save will appear here. To remove a listing, simply swipe right on it.")
//                        .setDismissOnTargetTouch(true)
                    .setDelay(200)
                    .singleUse("100511")
                    .show();

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
        if (v != null && v.equals(nav_btn_share)) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://booksyndy.com/download");
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } else {
            Intent profileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivity(profileIntent);

        }
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
                        "Address not found",
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
            address.put("lat", resultData.getDouble("lat"));
            address.put("lng", resultData.getDouble("lng"));
            editor = userPref.edit();
            editor.putFloat(getString(R.string.p_lat), (float) resultData.getDouble("lat"));
            editor.putFloat(getString(R.string.p_lng), (float) resultData.getDouble("lng"));
            editor.putString(getString(R.string.p_area), resultData.getString("addr2"));
            editor.putString(getString(R.string.p_city), resultData.getString("locality"));
            editor.apply();
            // showResults(address);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
//        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);
//        Toast.makeText(HomeActivity.this, "onResume", Toast.LENGTH_SHORT).show();
//        populateUserDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Update required")
                .setMessage("Please update the app to continue using it.")
                .setCancelable(false)
                .setIcon(R.drawable.ic_system_update_alt_24px)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openOnGooglePlay();
                            }
                        })
                /*.setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })*/.create();
        dialog.show();
    }

    public void openOnGooglePlay(){
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

}
