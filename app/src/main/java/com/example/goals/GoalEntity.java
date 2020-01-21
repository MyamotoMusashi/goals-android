package com.example.goals;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = GoalEntity.class, parentColumns = "id", childColumns = "parent_goal", onDelete = CASCADE))
public class GoalEntity {

    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "goal")
    private String goal;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "parent_goal")
    private Integer parentGoal;

    public GoalEntity(){}

    public GoalEntity(String goal, Integer parentGoal){
        this.setGoal(goal);
        this.setParentGoal(parentGoal);
    }

    public Integer getId() {
        return this.id;
    }


    public String getGoal() {
        return this.goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentGoal() {
        return this.parentGoal;
    }

    public void setParentGoal(Integer parentGoal) {
        this.parentGoal = parentGoal;
    }

    @Override
    public String toString() {
        return this.getGoal();
    }
}
