package com.jdgames.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.jdgames.notes.Database.DatabaseClient;
import com.jdgames.notes.Entity.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowNote extends AppCompatActivity {

    Boolean addNew = false;
    EditText titleEditText, editText;
    int id = -1;
    String previousNote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_show_note);

        titleEditText = findViewById(R.id.titleEditText);
        editText = findViewById(R.id.editText);
        id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            addNew = true;
        } else {
            Notes notes = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase().notesDao().getNoteWithID(id);
            previousNote = notes.getTitle() + "/" + notes.getSubtitle();
            titleEditText.setText(notes.getTitle());
            editText.setText(notes.getSubtitle());
        }
    }

    @Override
    public void onBackPressed() {
        String title = titleEditText.getText().toString().trim();
        String note = editText.getText().toString().trim();
        if (title.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | EEE", Locale.getDefault());
            title = sdf.format(new Date());
        }
        if (addNew) {
            if (!note.isEmpty()) {
                Notes notes = new Notes();
                notes.setTitle(title);
                notes.setSubtitle(note);
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase().notesDao().insert(notes);
            }
        } else {
            if (note.isEmpty()) {
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase().notesDao().deleteNoteWithID(id);
            } else {
                if (!previousNote.equals(title + "/" + note)) {
                    Notes notes = new Notes();
                    notes.setTitle(title);
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
