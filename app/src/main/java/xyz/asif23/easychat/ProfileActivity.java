package xyz.asif23.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;
import com.squareup.picasso.Picasso;

import xyz.asif23.easychat.Userss;
import android.content.*;

public class ProfileActivity extends AppCompatActivity {

    EditText viewusername;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    TextView movetoUpdate;
    FirebaseFirestore firestore;
    ImageView vimageuser;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private String ImageURIaccess;

    androidx.appcompat.widget.Toolbar mtoolbarViewProfile;

    ImageButton mbackButtonViewProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        vimageuser = findViewById(R.id.userimgview);
        viewusername = findViewById(R.id.viewusername);
        movetoUpdate = findViewById(R.id.movetoupdate);
        firestore = FirebaseFirestore.getInstance();
        mtoolbarViewProfile = findViewById(R.id.toolbarviewprofle);
        mbackButtonViewProfile = findViewById(R.id.backbuttonofviewprof);
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setSupportActionBar(mtoolbarViewProfile);
        mbackButtonViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(auth.getUid()).child("ProfilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIaccess = uri.toString();
                Picasso.get().load(uri).into(vimageuser);
            }
        });
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(auth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                try {
                    Userss user = snapshot.getValue(Userss.class);
                    viewusername.setText(user.getName());

                }catch (Exception e){
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to fatch", Toast.LENGTH_SHORT).show();
            }
        });

        movetoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,updateProfile.class);
                intent.putExtra("nameofuser",viewusername.getText().toString());
                startActivity(intent);
            }
        });


    }


}