package com.example.goals;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class GoalPojo extends GoalEntity {

    @Embedded(prefix = "subGoal") public GoalEntity goal;
    @Relation(
            parentColumn = "id",
            entityColumn = "parent_goal"
    )
    List<GoalEntity> subGoals;
}
