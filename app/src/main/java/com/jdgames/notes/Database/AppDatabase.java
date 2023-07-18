package com.jdgames.notes.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jdgames.notes.Dao.NotesDao;
import com.jdgames.notes.Entity.Notes;

@Database(entities = {Notes.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NotesDao notesDao();
}