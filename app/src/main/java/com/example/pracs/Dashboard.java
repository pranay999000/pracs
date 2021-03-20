package com.example.pracs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    ExpandableListView expandableListView;
    CustomExpandableListViewAdapter customExpandableListViewAdapter;

    List<String> SubList;
    HashMap<String, List<String>> pracList;

    List<String> CreateSubList;
    CreateNewAdapter createNewAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setTitle("Dashboard");

        expandableListView = findViewById(R.id.SublistExpandableListView);

        SessionClass sessionClass = new SessionClass(Dashboard.this);
        String branch = sessionClass.getUserBranch();
        int batch = sessionClass.getUserBatch();

        SetStandardGroups(branch, batch);
        customExpandableListViewAdapter = new CustomExpandableListViewAdapter(this, SubList, pracList);
        expandableListView.setAdapter(customExpandableListViewAdapter);
    }

    private void SetStandardGroups(String branch, int batch) {
        SubList = new ArrayList<>();
        pracList = new HashMap<>();

        DatabaseReference navRef = database.getReference().child("port").child(branch).child(String.valueOf(batch)).child("nav");

        ValueEventListener listener = new ValueEventListener() {
            List<String> PracItem;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SubList.clear();
                pracList.clear();
                for(DataSnapshot snap: snapshot.getChildren()) {
                    final String cSub = snap.getKey();
                    SubList.add(cSub);
                    Log.d("SUB", cSub);
                    PracItem = new ArrayList<>();

                    assert cSub != null;
                    for (DataSnapshot snap1 : snapshot.child(cSub).getChildren()) {
                        String Practical = (String) snap1.child("no").getValue();
                        PracItem.add(Practical);
                    }
                    pracList.put(cSub, PracItem);
                    customExpandableListViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        navRef.addValueEventListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.addPracMenu) {
            createDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDialog() {
        CreateSubList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        View view = getLayoutInflater().inflate(R.layout.add_dialog, null);
        EditText editText = view.findViewById(R.id.addNewEditText);
        Button button = view.findViewById(R.id.newSubButton);
        ListView listView = view.findViewById(R.id.createNewListView);
        listView.setItemsCanFocus(true);

        SessionClass sessionClass = new SessionClass(Dashboard.this);
        String branch = sessionClass.getUserBranch();
        int batch = sessionClass.getUserBatch();

        DatabaseReference subRef = database.getReference().child("port").child(branch).child(String.valueOf(batch)).child("subs");

        final CharSequence[] subject = new CharSequence[1];
        button.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                button.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    button.setEnabled(true);
                    subject[0] = s;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button.setOnClickListener(v -> {
            subRef.orderByValue().equalTo(subject[0].toString().toUpperCase()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) {
                        String pushID = subRef.push().getKey();
                        assert pushID != null;
                        subRef.child(pushID).setValue(subject[0].toString().toUpperCase());
                        editText.setText("");

                        subRef.removeEventListener(this);
                    } else {
                        Toast.makeText(Dashboard.this, subject[0].toString() + " already exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        ValueEventListener subjectListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CreateSubList.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    CreateSubList.add((String) snap.getValue());
                    createNewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        subRef.addValueEventListener(subjectListener);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        createNewAdapter = new CreateNewAdapter(this, CreateSubList);
        listView.setAdapter(createNewAdapter);

    }
}