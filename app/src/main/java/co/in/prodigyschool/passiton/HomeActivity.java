package co.in.prodigyschool.passiton;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
    private FirebaseFirestore mFirestore;
    private String curUserId, snackbarMessage;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView = navigationView.getHeaderView(0);
        navUsername =  headerView.findViewById(R.id.user_title);
        navUserphone = headerView.findViewById(R.id.user_phone);
        navUsername.setOnClickListener(this);
        navUserphone.setOnClickListener(this);
        snackbarMessage = getIntent().getStringExtra("SNACKBAR_MSG");
        if (snackbarMessage!=null) {
            if (snackbarMessage.length() > 0) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, snackbarMessage, Snackbar.LENGTH_SHORT).show();
            }
        }
        populateUserDetails();


    }

    private void populateUserDetails() {

        String userPhone = userPref.getString(getString(R.string.p_userphone),"");
        String userId = userPref.getString(getString(R.string.p_userid),"");
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
        startLocationUpdates();

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




    public boolean CheckGpsStatus() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
            if (!CheckGpsStatus()) {
                //showGpsSettingDialog();
            } else {

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
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(HomeActivity.this,
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Disabled");
        builder.setMessage("Open settings to enable GPS now?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Toast.makeText(getApplicationContext(),"Location Service Disabled",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        builder.show();

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
                        "Address not found, " ,
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
