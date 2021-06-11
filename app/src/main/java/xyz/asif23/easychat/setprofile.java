package xyz.asif23.easychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;
import android.content.*;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import xyz.asif23.easychat.Userss;

public class setprofile extends AppCompatActivity {
     private CardView userimage;
     private ImageView userimageView;
     private static int PICK_IMAGE =123;
     private Uri imagepath;

     private EditText userame;

     private android.widget.Button sveprof;
     public FirebaseAuth auth;

     private String name;

     private FirebaseStorage storage;

     private StorageReference storref;

     private String ImageUrlAccesstoken;
     private FirebaseFirestore firestore;

     ProgressBar progsaveprof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setprofile);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storref = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        userame = findViewById(R.id.username);
        userimage = findViewById(R.id.guestimg);
        userimageView = findViewById(R.id.userimg);

        sveprof = findViewById(R.id.btnsaveprof);
        progsaveprof = findViewById(R.id.progsetprof);

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        sveprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = userame.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(setprofile.this, "Name is Empty", Toast.LENGTH_SHORT).show();
                }
                else if(imagepath==null)
                {
                    Toast.makeText(setprofile.this, "Image Is Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progsaveprof.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    name= userame.getText().toString().trim();
                    Toast.makeText(getApplicationContext(), "User Profile Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(setprofile.this,chatActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            userimageView.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendDataForNewUser(){
        sendonRealtimeDatabase();
    }

    private void sendonRealtimeDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(auth.getUid());

        try {
            Userss u =new Userss(name);
            databaseReference.setValue(u);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//        databaseReference.child("name").setValue(name);
        Toast.makeText(getApplicationContext(), "User Profile Added Successfully", Toast.LENGTH_SHORT).show();
        sendImageStorage();
    }

    private void sendImageStorage() {
        StorageReference imgref = storref.child("Images").child(auth.getUid()).child("ProfilePic");
        //compress image
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data =byteArrayOutputStream.toByteArray();

        //store database
        UploadTask uploadTask = imgref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUrlAccesstoken = uri.toString();
                        Toast.makeText(getApplicationContext(), "URI get SUccessful", Toast.LENGTH_SHORT).show();
                        sendDataToClourStorage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        Toast.makeText(getApplicationContext(), "URI Get Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(getApplicationContext(), "Image Not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToClourStorage() {
        DocumentReference documentReference = firestore.collection("Users").document(auth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",ImageUrlAccesstoken);
        userdata.put("uid",auth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data on Cloud FireStore Send Success", Toast.LENGTH_SHORT).show();
            }
        });
    }
}