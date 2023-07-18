package com.jdgames.notes.Database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient mInstance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "Notes_Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}