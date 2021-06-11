package xyz.asif23.easychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.content.*;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class updateProfile extends AppCompatActivity {
    private EditText getusername;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firestore;
    private ImageView getuserimgview;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String ImageURIaccess;
    private androidx.appcompat.widget.Toolbar toobarupdatprofile;
    private ImageButton backbuttonofupdateprof;
    ProgressBar produpdateprof;
    private Uri imagepath;
    Intent intent;
    private static int PICK_IMAGE=123;
    String newname;
    android.widget.Button updatebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        toobarupdatprofile = findViewById(R.id.toolbarupdateprofile);
        backbuttonofupdateprof = findViewById(R.id.backupdtaeprof);
        getuserimgview = findViewById(R.id.getuserimgview);
        produpdateprof = findViewById(R.id.progbarupdatprof);
        getusername = findViewById(R.id.getusername);
        updatebtn = findViewById(R.id.updateprofbtn);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();

        intent = getIntent();

        setSupportActionBar(toobarupdatprofile);

        backbuttonofupdateprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getusername.setText(intent.getStringExtra("nameofuser"));

        DatabaseReference databaseReference =firebaseDatabase.getReference("Users").child(auth.getUid());
        Toast.makeText(getApplicationContext(), "three.1", Toast.LENGTH_SHORT).show();

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newname= getusername.getText().toString();
                if(newname.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Name is Empty", Toast.LENGTH_SHORT).show();
                }
                else if(imagepath!=null){
                    produpdateprof.setVisibility(View.VISIBLE);
                    Userss user = new Userss(newname);
                    databaseReference.setValue(user);
                    updateImageToStorage();
                    Toast.makeText(getApplicationContext(), "uSER uPDATED", Toast.LENGTH_SHORT).show();
                    produpdateprof.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(updateProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    produpdateprof.setVisibility(View.VISIBLE);
                    Userss user = new Userss(newname);
                    databaseReference.setValue(user);
                    updatenameClourFirestore();
                    Toast.makeText(getApplicationContext(), "updated", Toast.LENGTH_SHORT).show();
                    produpdateprof.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(updateProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        getuserimgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });
        storageReference =firebaseStorage.getReference();
        storageReference.child("Images").child(auth.getUid()).child("ProfilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIaccess = uri.toString();
                Picasso.get().load(uri).into(getuserimgview);
            }
        });

    }

    private void updatenameClourFirestore() {
        DocumentReference documentReference = firestore.collection("Users").document(auth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name",newname);
        userdata.put("image",ImageURIaccess);
        userdata.put("uid",auth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data on Cloud FireStore Send Success", Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(getApplicationContext(), "uSER uPDATED", Toast.LENGTH_SHORT).show();
    }

    private void updateImageToStorage() {
        StorageReference imgref = storageReference.child("Images").child(auth.getUid()).child("ProfilePic");
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
                        ImageURIaccess = uri.toString();
                        Toast.makeText(getApplicationContext(), "URI get SUccessful", Toast.LENGTH_SHORT).show();
                        updatenameClourFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI Get Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(getApplicationContext(), "Image Not Uploaded"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            getuserimgview.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}