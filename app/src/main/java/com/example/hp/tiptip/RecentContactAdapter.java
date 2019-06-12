package com.example.hp.tiptip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecentContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private List<HashMap<String,Object>> recentContactList;

        public RecentContactAdapter(Context c, ArrayList<HashMap<String,Object>> list){
            context = c;
            recentContactList = list;
        }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = View.inflate(context,R.layout.recent_contact_item,null);
            return new RecentContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((RecentContactViewHolder)viewHolder).textView1.setText(recentContactList.get(i).get("recent_contact_id").toString());
        ((RecentContactViewHolder)viewHolder).textView2.setText(recentContactList.get(i).get("recent_contact_content").toString());
    }

    @Override
    public int getItemCount() {
        return recentContactList.size();
    }

    class RecentContactViewHolder extends RecyclerView.ViewHolder{
            TextView textView1;
            TextView textView2;

            public RecentContactViewHolder(@NonNull View itemView) {
                super(itemView);
                textView1 = itemView.findViewById(R.id.recent_contact_id);
                textView2 = itemView.findViewById(R.id.recent_contact_content);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("friendId",textView1.getText().toString());
                        intent.putExtra("bundle",bundle);
                        context.startActivity(intent);
                    }
                });
            }
        }
}
