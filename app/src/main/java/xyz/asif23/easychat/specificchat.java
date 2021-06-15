package xyz.asif23.easychat;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class specificchat extends AppCompatActivity {
    EditText getmsg;
    ImageButton btnsendmsg;

    androidx.cardview.widget.CardView sendmessagecardview;
    androidx.appcompat.widget.Toolbar toolbarofspecificchat;
    ImageView imgofspecificuser;
    TextView nameofspecificuser;

    private String enterenmsg;
    Intent intent;
    String receivername,sendername,receiveruid,senderuid;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom,receiverroom;
    ImageButton backbuttonspecificchat;

    RecyclerView mmessagerecycleview;

    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    messagesadapter messagesadapter;
    ArrayList<messages>messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);


            getmsg=findViewById(R.id.getMsg);
            sendmessagecardview=findViewById(R.id.cardviewsendmsg);
            btnsendmsg = findViewById(R.id.imgSend);
            toolbarofspecificchat = findViewById(R.id.toolbarspecificchat);
            nameofspecificuser = findViewById(R.id.nameofspecificuser);
            imgofspecificuser  = findViewById(R.id.specificuserimg);
            backbuttonspecificchat=findViewById(R.id.backbuttonofspecificchat);
            intent=getIntent();

            messagesArrayList=new ArrayList<>();
            mmessagerecycleview=findViewById(R.id.recycleviewofspecificuser);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            mmessagerecycleview.setLayoutManager(linearLayoutManager);
            messagesadapter = new messagesadapter(specificchat.this,messagesArrayList);
            mmessagerecycleview.setAdapter(messagesadapter);

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = firebaseDatabase.getInstance();
            calendar = Calendar.getInstance();
            simpleDateFormat=new SimpleDateFormat("hh:mm a");

            senderuid=firebaseAuth.getUid();
            receiveruid=getIntent().getStringExtra("receiverid");
            receivername = getIntent().getStringExtra("name");



            senderroom=senderuid+receiveruid;
            receiverroom=receiveruid+senderuid;
            setSupportActionBar(toolbarofspecificchat);
            toolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"Toolbasr si clicked",Toast.LENGTH_LONG).show();
                }
            });



            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Chats").child(senderroom).child("messages");
            messagesadapter=new messagesadapter(specificchat.this,messagesArrayList);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    messagesArrayList.clear();
                    for (DataSnapshot snapshot1:snapshot.getChildren())
                    {
                            messages message = snapshot1.getValue(messages.class);
                            messagesArrayList.add(message);
                    }
                    messagesadapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });

            backbuttonspecificchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            nameofspecificuser.setText(receivername);
            String uri = intent.getStringExtra("imageuri");
            if(uri.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "Null is Receiver", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Picasso.get().load(uri).into(imgofspecificuser);
            }

            btnsendmsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterenmsg = getmsg.getText().toString();
                    if(enterenmsg.isEmpty()){
                        Toast.makeText(specificchat.this, "Enter Message first", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Date date = new Date();
                        currentTime=simpleDateFormat.format(calendar.getTime());
                        messages message = new messages(enterenmsg,firebaseAuth.getUid(),date.getTime(),currentTime);
                        firebaseDatabase=FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference().child("Chats")
                                .child(senderroom)
                                .child("messages")
                                .push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {
                                firebaseDatabase.getReference()
                                        .child("Chats")
                                        .child(receiverroom)
                                        .child("messages")
                                        .push().setValue(message)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull  Task<Void> task) {
                                                Toast.makeText(specificchat.this, "Done", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                        getmsg.setText(null);
                    }
                }
            });






    }
    @Override
    public void onStart() {
        super.onStart();
        messagesadapter.notifyDataSetChanged();

    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesadapter!=null) {
            messagesadapter.notifyDataSetChanged();
        }
    }
}