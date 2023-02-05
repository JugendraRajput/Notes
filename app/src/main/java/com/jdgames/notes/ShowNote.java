package com.jdgames.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ShowNote extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Boolean check = true;
    EditText editText;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);

        editText = findViewById(R.id.editText);
        sharedPreferences = this.getSharedPreferences("com.jdgames.notes", Context.MODE_PRIVATE);

        i = getIntent().getIntExtra("position", -1);

        if (i != -1) {
            editText.setText(MainActivity.notesList.get(i));
        } else {
            check = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (check) {
            MainActivity.notesList.set(i, editText.getText().toString().trim());
            MainActivity.notesListView.set(i, editText.getText().toString().trim());
        } else {
            if (!editText.getText().toString().equals("")) {
                MainActivity.notesList.add(editText.getText().toString().trim());
                MainActivity.notesListView.add(editText.getText().toString().trim());
            }
        }
        try {
            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesList)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < MainActivity.notesListView.size(); i++) {
            if (MainActivity.notesListView.get(i).length() > 30) {
                MainActivity.notesListView.set(i, MainActivity.notesListView.get(i).substring(0, 25) + "...");
            }
        }
        MainActivity.arrayAdapter.notifyDataSetChanged();
        finish();
    }
}
