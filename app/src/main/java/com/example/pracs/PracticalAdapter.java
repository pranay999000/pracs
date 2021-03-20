package com.example.pracs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PracticalAdapter extends RecyclerView.Adapter<PracticalAdapter.PracticalViewHolder> {
    private Context context;
    private List<PracticalHolder> list;
    LayoutInflater layoutInflater;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public PracticalAdapter(Context context, List<PracticalHolder> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PracticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practical_list_item, parent, false);
        return new PracticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticalViewHolder holder, int position) {
        PracticalHolder ph = list.get(position);
        holder.textView.setText(ph.getPageNo());
        Picasso.get().load(ph.getImageLink()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PracticalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        EditText editText;
        Button delete;
        Button download;
        Button edit;

        public PracticalViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.pageNoTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
