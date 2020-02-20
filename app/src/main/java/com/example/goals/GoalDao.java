package com.example.goals;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface GoalDao {

    @Query("SELECT * FROM goalentity")
    List<GoalEntity> getAll();

    @Query("SELECT * FROM goalentity WHERE parent_goal IS NULL")
    Single<List<GoalEntity>> getAllWithoutPanrentGoal();

    @Query("SELECT * FROM goalentity WHERE id IN (:userIds)")
    List<GoalEntity> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM goalentity WHERE parent_goal LIKE :goalId")
    Single<List<GoalEntity>> loadGoalsById(int goalId);

    @Query("SELECT * FROM goalentity WHERE goal LIKE :goal LIMIT 1")
    GoalEntity findByName(String goal);

    @Query("UPDATE goalentity SET goal = :goal, parent_goal = :parentGoal WHERE id LIKE :id")
    void editGoal(String goal, String id, String parentGoal);

    @Insert
    void insertAll(GoalEntity... goals);

    @Insert
    long insertOne(GoalEntity goal);

    @Delete
    void delete(GoalEntity goal);

}