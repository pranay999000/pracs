package com.example.pracs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CreateNewAdapter extends BaseAdapter {
    private Context context;
    private List list;
    LayoutInflater layoutInflater;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public CreateNewAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        convertView = null;

        if(convertView == null) {
            holder = new ViewHolder();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.create_new_item, null);

            SessionClass sessionClass = new SessionClass(context);
            String branch = sessionClass.getUserBranch();
            int batch = sessionClass.getUserBatch();

            holder.caption = convertView.findViewById(R.id.pracNoEditText);
            holder.textView = convertView.findViewById(R.id.AddNewTextView);
            holder.button = convertView.findViewById(R.id.AddNewButton);
            holder.caption.setTag(position);
            holder.textView.setText(list.get(position).toString());
            holder.button.setEnabled(false);

            final CharSequence[] practicalNo = new CharSequence[1];

            holder.caption.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    holder.button.setEnabled(false);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() != 0) {
                        holder.button.setEnabled(true);
                        practicalNo[0] = s;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            DatabaseReference navRef = database.getReference()
                    .child("port")
                    .child(branch)
                    .child(String.valueOf(batch))
                    .child("nav")
                    .child(list.get(position).toString());

            holder.button.setOnClickListener(v -> {
                navRef.orderByChild("no").equalTo(practicalNo[0].toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            String pushId = navRef.push().getKey();
                            Navigation navigation = new Navigation(list.get(position).toString(), practicalNo[0].toString());
                            navRef.child(pushId).setValue(navigation);

                            holder.caption.setText("");
                            navRef.removeEventListener(this);
                            moveToMain(list.get(position).toString(), practicalNo[0].toString());
                        } else {
                            Toast.makeText(context, practicalNo[0] + " already exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        int tag_pos = (int) holder.caption.getTag();
        holder.caption.setId(tag_pos);

        return convertView;
    }
    private void moveToMain(String sub, String prac) {
        Intent MainIntent = new Intent(context, MainActivity.class);
        MainIntent.putExtra("subject", sub);
        MainIntent.putExtra("practical", prac);
        context.startActivity(MainIntent);
    }
}

class ViewHolder{
    TextView textView;
    EditText caption;
    Button button;
}

class Navigation{
    String from;
    String no;

    public Navigation(){
    }

    public Navigation(String from, String no) {
        this.from = from;
        this.no = no;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
