package xyz.asif23.easychat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class messagesadapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<messages> messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public messagesadapter(Context context, ArrayList<messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.receiverchatlayout,parent,false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        messages messages = messagesArrayList.get(position);
        if (holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            viewHolder.textViewMsg.setText(messages.getMessage());
            viewHolder.timeofMsg.setText(messages.getCurrenttime());
        }
        else
        {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.textViewMsg.setText(messages.getMessage());
            viewHolder.timeofMsg.setText(messages.getCurrenttime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        messages message = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId()))
        {
            return ITEM_SEND;
        }
        else
        {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView textViewMsg;
        TextView timeofMsg;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMsg=itemView.findViewById(R.id.sendermsg);
            timeofMsg=itemView.findViewById(R.id.timeofmsg);
        }
    }
    class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView textViewMsg;
        TextView timeofMsg;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMsg=itemView.findViewById(R.id.receivermsg);
            timeofMsg=itemView.findViewById(R.id.timeofmsgreceiver);
        }
    }
}
