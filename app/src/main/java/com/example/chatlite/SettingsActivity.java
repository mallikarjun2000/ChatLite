package com.example.chatlite;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextView fname, fstatus;

    public static final Integer GALLERY_PICK = 1;

    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseUser currentuser;
    private FirebaseAuth mAuth;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fname = findViewById(R.id.settings_name);
        fstatus = findViewById(R.id.settings_status);
        imageView = findViewById(R.id.settings_image);



        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        String uid = currentuser.getUid();


        mStorageRef = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                //Toast.makeText(SettingsActivity.this,image,Toast.LENGTH_LONG).show();
                if(!image.equals("default"))
                Picasso.get().load(image).placeholder(R.drawable.background_2).into(imageView);
                else
                    imageView.setBackgroundResource(R.drawable.background_2);


                fname.setText(name);
                fstatus.setText(status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button changeStatus = findViewById(R.id.settings_button_status);
        Button changeImage = findViewById(R.id.settings_button_image);

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(i);
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery_intent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            Toast.makeText(SettingsActivity.this,"id =  "+imageUri,Toast.LENGTH_SHORT).show();

            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                String uid = currentuser.getUid();

                final StorageReference filepath = mStorageRef.child("profile_images").child(uid+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            Task<Uri> urlTask = task.getResult().getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            final String sdownload_url = String.valueOf(downloadUrl);

                            //String image_url = task.getResult().getUploadSessionUri().toString();

                            databaseReference.child("image").setValue(sdownload_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SettingsActivity.this,"Successful Uploaded",Toast.LENGTH_SHORT).show();
                                    }
                                    if(task.isCanceled()){
                                        Toast.makeText(SettingsActivity.this,"Fatal Error!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                            //Toast.makeText(SettingsActivity.this, "Image Updated", Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/

        Uri file = data.getData();
        //img.setImageURI(filePath);
        //t1.setText(uri);
        String uid = currentuser.getUid();
        final StorageReference filepath = mStorageRef.child("profile_images").child(uid+".jpg");

        UploadTask uploadTask = filepath.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //String link = String.valueOf(firebaseStorage.getDownloadUrl());
                final String link = "";
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String link = String.valueOf(uri);
                        databaseReference.child("image").setValue(link);
                    }
                });
                Toast.makeText(SettingsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
