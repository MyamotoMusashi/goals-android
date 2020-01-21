package com.example.goals;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GoalEntity.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
}
