package com.example.tics.mysocialapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tics.mysocialapp.model.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private static final String REQUIRED = "Required";
    private Location userLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Geocoder geocoder;
    private DatabaseReference mySocialApp;
    private ImagePicker imagePicker;
    private Uri setectedUri;
    private ImageButton imageSelected;
    private TextView addressLocation;
    private EditText titleEdiTxt, descriptionEdiTxt;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        geocoder = new Geocoder(this, Locale.getDefault());
        addressLocation = findViewById(R.id.postLocation);
        titleEdiTxt = findViewById(R.id.postTitleEditText);
        descriptionEdiTxt = findViewById(R.id.descriptionPostEditText);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    userLocation = locationResult.getLocations().get(0);
                    stopLocationUpdates();
                    setAddressNameFromLocation();
                }
            }
        };
        initImagePicker();
        startLocationUpdates();

        findViewById(R.id.submitPostButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        final String title = titleEdiTxt.getText().toString();
        final String description = descriptionEdiTxt.getText().toString();
        submitImageToStorage();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            titleEdiTxt.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(description)) {
            descriptionEdiTxt.setError(REQUIRED);
            return;
        }

        /*if (TextUtils.isEmpty(imageUrl)) {
            descriptionEdiTxt.setError(REQUIRED);
            return;
        }*/

        final String userId = getUid();
        mySocialApp.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        writeNewPost(userId, imageUrl, title, description);
                        finish();
                        // Get user value
                        /*User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, imageUrl, title, description);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        /*Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);*/
                        // [END_EXCLUDE]
                    }
                });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void writeNewPost(String userId, String image, String title, String body) {

        String key = mySocialApp.child("posts").push().getKey();
        Post post = new Post(userId, image, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mySocialApp.updateChildren(childUpdates);
    }

    private void setAddressNameFromLocation() {
        try {
            List<Address> addresses = new ArrayList<>(geocoder.getFromLocation(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    1));
            addressLocation.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void initImagePicker() {
        imageSelected = findViewById(R.id.imagePostImageButton);
        imageSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(NewPostActivity.this, null, new OnImagePickedListener() {
                        @Override
                        public void onImagePicked(Uri imageUri) {
                            setectedUri = imageUri;
                            imageSelected.setImageURI(imageUri);
                        }
                    });
                }
                imagePicker.choosePicture(true);
            }
        });
    }

    private void submitImageToStorage() {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("post_images");
        storageReference.putFile(setectedUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    //TODO upload to database Post data + imageUrl
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }
}
