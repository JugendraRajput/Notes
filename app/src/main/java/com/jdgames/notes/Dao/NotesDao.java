package com.jdgames.notes.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jdgames.notes.Entity.Notes;

import java.util.List;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM notes")
    List<Notes> getAll();

    @Query("SELECT * FROM notes where `id` = :id")
    Notes getNoteWithID(int id);

    @Query("DELETE FROM notes where id = :id")
    void deleteNoteWithID(int id);

    @Insert
    void insert(Notes note);

    @Update
    void update(Notes note);

}
