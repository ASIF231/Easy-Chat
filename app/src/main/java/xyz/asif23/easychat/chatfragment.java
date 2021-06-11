package xyz.asif23.easychat;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class chatfragment extends Fragment {
    private FirebaseFirestore firestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth auth;

    ImageView imageViewUser;

    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder> chatAdapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.chatfragmentlayout, container, false);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.recycleview);

//        Query query = firestore.collection("Users");
        Query query = firestore.collection("Users").whereNotEqualTo("uid",auth.getUid());
        FirestoreRecyclerOptions<firebasemodel> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusername)  {
            @Override
            protected void onBindViewHolder(@NonNull  NoteViewHolder holder, int position, @NonNull  firebasemodel model) {
                holder.username.setText(model.getName());
                String uri = model.getImage();
                Picasso.get().load(uri).into(imageViewUser);
                if(model.getStatus().equals("Online")){
                    holder.statususer.setText(model.getStatus());
                    holder.statususer.setTextColor(Color.GREEN);
                }
                else
                {
                    holder.statususer.setText(model.getStatus());
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Item Is Clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(),specificchat.class);
                            intent.putExtra("name",model.getName());
                            intent.putExtra("receiverid",model.getUid());
                            intent.putExtra("imageuri",model.getImage());
                            startActivity(intent);
                    }

                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatactivitylayout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        return v;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView statususer;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.nameuser);
            statususer = itemView.findViewById(R.id.userstatus);
            imageViewUser = itemView.findViewById(R.id.imguser);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null) {
        chatAdapter.stopListening();
        }
    }
}
