package com.example.pracs;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionClass {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String SHARED_PREFERENCE_NAME = "session";
    String SESSION_ID = "session_user_id";
    String SESSION_BRANCH = "session_user_branch";
    String SESSION_BATCH = "session_user_batch";

    public SessionClass(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User user){
        int id = user.getId();
        String branch = user.getBranch();
        int batch = user.getBatch();

        editor.putInt(SESSION_ID, id).commit();
        editor.putString(SESSION_BRANCH, branch).commit();
        editor.putInt(SESSION_BATCH, batch).commit();
    }

    public int getUserID(){
        return sharedPreferences.getInt(SESSION_ID, -1);
    }

    public String getUserBranch(){
        return sharedPreferences.getString(SESSION_BRANCH, "null");
    }

    public int getUserBatch(){
        return sharedPreferences.getInt(SESSION_BATCH, -1);
    }

    public void clearSession(){
        editor.putInt(SESSION_ID, -1).commit();
        editor.putString(SESSION_BRANCH, "null").commit();
        editor.putInt(SESSION_BATCH, -1).commit();
    }
}
