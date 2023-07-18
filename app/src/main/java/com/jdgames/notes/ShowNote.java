package com.jdgames.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.jdgames.notes.Database.DatabaseClient;
import com.jdgames.notes.Entity.Notes;

public class ShowNote extends AppCompatActivity {

    Boolean addNew = false;
    EditText editText;
    int id = -1;
    String previousNote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);

        editText = findViewById(R.id.editText);
        id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            addNew = true;
        } else {
            Notes notes = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase().notesDao().getNoteWithID(id);
            previousNote = notes.getSubtitle();
            editText.setText(notes.getSubtitle());
        }
    }

    @Override
    public void onBackPressed() {
        String note = editText.getText().toString().trim();
        if (addNew) {
            if (!note.isEmpty()) {
                Notes notes = new Notes();
                notes.setTitle("Title");
                notes.setSubtitle(note);
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase().notesDao().insert(notes);
            }
        } else {
            if (note.isEmpty()) {
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase().notesDao().deleteNoteWithID(id);
            } else {
                if (!previousNote.equals(note)) {
                    Notes notes = new Notes();
                    notes.setTitle("Title");
                    notes.setSubtitle(note);
                    notes.setId(id);
                    DatabaseClient.getInstance(getApplicationContext())
                            .getAppDatabase().notesDao().update(notes);
                }
            }
        }
        finish();
    }
}
