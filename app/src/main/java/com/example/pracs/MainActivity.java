package com.example.pracs;

import android.content.ClipData;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppUpdateManager appUpdateManager;
    int RequestUpdate = 1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    String subject;
    String practical_no;

    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;
    List<PracticalHolder> practicalList;
    PracticalAdapter practicalAdapter;

    String imageEncoded;
    List<String> imageEncodedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        subject = intent.hasExtra("subject") ? getIntent().getExtras().getString("subject") : "null";
        practical_no = intent.hasExtra("practical") ? getIntent().getExtras().getString("practical") : "null";

        TextView batchTextView = findViewById(R.id.navTextView);
        TextView hintTextView = findViewById(R.id.hintTextView);
        recyclerView = findViewById(R.id.practicalsRecyclerView);
        Button addPageButton = findViewById(R.id.addPageButton);

        if(subject.equals("null") && practical_no.equals("null")) {
            batchTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            hintTextView.setVisibility(View.GONE);
            addPageButton.setVisibility(View.VISIBLE);
            batchTextView.setText(MessageFormat.format("{0}> #{1}", subject, practical_no));
        }

        SessionClass sessionClass = new SessionClass(MainActivity.this);
        int batch = sessionClass.getUserBatch();
        String branch = sessionClass.getUserBranch();

        addPageButton.setOnClickListener(v -> {
            Intent selectImageIntent = new Intent();
            selectImageIntent.setType("image/*");
            selectImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(selectImageIntent, "Select pictures"), 1);
        });

//        Log.v("LOG_TAG", "Selected " + imageEncodedList.size());

//        if(!imageEncodedList.isEmpty()) {
//            Toast.makeText(this, imageEncodedList.size(), Toast.LENGTH_SHORT).show();
//        }

        ExtendedFloatingActionButton eFab = findViewById(R.id.fab);
        eFab.setOnClickListener(view -> {
            Intent dashboardIntent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(dashboardIntent);
        });

        nestedScrollView = findViewById(R.id.nestedScrollView);
        if(nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(scrollY >  oldScrollY)
                        eFab.setVisibility(View.GONE);
                    else if(scrollY < oldScrollY)
                        eFab.setVisibility(View.VISIBLE);
                }
            });
        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference pracRef = database.getReference()
                .child("port")
                .child(branch)
                .child(String.valueOf(batch))
                .child("prac")
                .child(subject)
                .child(practical_no);

        practicalList = new ArrayList<>();

        ValueEventListener practicalListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                practicalList.clear();
                PracticalHolder practicalHolder;
                for(DataSnapshot snap: snapshot.getChildren()) {
                    Log.d("IMAGE", (String) snap.child("image").getValue());
                    String imageLink = (String) snap.child("image").getValue();
                    String pageNo = snap.child("pageNo").exists() ? String.valueOf(snap.child("pageNo").getValue()) : "";

                    practicalHolder = new PracticalHolder(imageLink, pageNo);
                    practicalList.add(practicalHolder);
                    practicalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        practicalAdapter = new PracticalAdapter(this, practicalList);
        recyclerView.setAdapter(practicalAdapter);

        pracRef.addValueEventListener(practicalListener);
        Log.d("SIZE", String.valueOf(practicalList.size()));

        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if(result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, RequestUpdate);
                    }catch (IntentSender.SendIntentException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(requestCode == 1 && resultCode == -1 && data != null) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imageEncodedList = new ArrayList<>();

                if(data.getData() != null) {
                    Uri imageUri = data.getData();
                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imageEncodedList.add(imageEncoded);
                    cursor.close();
                } else {
                    if(data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        ArrayList<Uri> arrayList = new ArrayList<>();

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            arrayList.add(uri);

                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imageEncodedList.add(imageEncoded);
                            cursor.close();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked any images", Toast.LENGTH_SHORT);
            }

            //TODO: Upload to Database
            if(!subject.equals("null"))
                Toast.makeText(this, subject, Toast.LENGTH_SHORT).show();

            Log.v("LOG_TAG", "Selected images " + imageEncodedList.size());
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
            if(result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                try {
                    appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, RequestUpdate);
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        CoordinatorLayout layout = findViewById(R.id.main);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(layout, "Settings Clicked", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        if(id == R.id.action_clear){
            clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clear() {
        SessionClass sessionClass = new SessionClass(MainActivity.this);
        sessionClass.clearSession();

        moveToRegister();
    }

    private void moveToRegister() {
        Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerIntent);
    }
}