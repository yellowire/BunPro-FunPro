package com.example.bunprofunpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.bunprofunpro.MainActivity.apiKey;

public class GetApiKey extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_api_key);

    }

    public String findUser() {
        Utilities.StudyQueue studyQueue;
        try {
            studyQueue = Utilities.buildStudyQueue();
        }
        catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e.getCause());
            return "error";
        }

        return studyQueue.username;
    }



    public Boolean saveCreds() {

        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("apiKey", apiKey);
            editor.commit();
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }



    public Boolean deleteCreds() {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("apiKey", null);
            editor.commit();
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }



    public void onSubmit(View v) {
        EditText apiInputText = findViewById(R.id.apiInputText);
        TextView apiResponse = findViewById(R.id.apiResponse);
        TextView found = findViewById(R.id.found);
        Button apiSubmit = findViewById(R.id.apiSubmit);
        Button apiConfirm = findViewById(R.id.apiConfirm);

        apiKey = apiInputText.getText().toString();

        if (!saveCreds()) {
            apiResponse.setText(R.string.key_save_fail);
            return;
        }

        String username = findUser();

        TextView textView = findViewById(R.id.textView);
        if (username != "") {
            apiSubmit.setBackgroundColor(ContextCompat.getColor(GetApiKey.this, R.color.colorPrimaryDark));
            apiResponse.setText(username);
            found.setVisibility(1);
            apiConfirm.setVisibility(1);
        }
        else {
            if (!deleteCreds()) {
                apiResponse.setText(R.string.poop);
            }
            apiResponse.setText(R.string.key_invalid);
        }
    }



    public void onConfirm(View v) {
        startActivity(new Intent(GetApiKey.this, MainActivity.class));
    };
}