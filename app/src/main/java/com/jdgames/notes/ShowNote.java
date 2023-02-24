package com.jdgames.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ShowNote extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Boolean addNew = false;
    EditText editText;
    int position;
    String previousNote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);

        sharedPreferences = this.getSharedPreferences("com.jdgames.notes", Context.MODE_PRIVATE);
        editText = findViewById(R.id.editText);

        position = getIntent().getIntExtra("position", -1);
        if (position == -1) {
            addNew = true;
        } else {
            previousNote = MainActivity.notesList.get(position);
            editText.setText(previousNote);
        }
    }

    @Override
    public void onBackPressed() {
        String note = editText.getText().toString().trim();
        if (addNew) {
            if (!note.equals("")) {
                MainActivity.notesList.add(note);
                MainActivity.notesListView.add(note);
                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!previousNote.equals(note)){
                MainActivity.notesList.set(position, note);
                MainActivity.notesListView.set(position, note);
                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
            }
        }
        try {
            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesList)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < MainActivity.notesListView.size(); i++) {
            if (MainActivity.notesListView.get(i).length() > 25) {
                MainActivity.notesListView.set(i, MainActivity.notesListView.get(i).substring(0, 25) + "...");
            }
        }
        MainActivity.arrayAdapter.notifyDataSetChanged();
        finish();
    }
}
