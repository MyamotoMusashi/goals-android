package com.example.goals;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class GoalActivity extends AppCompatActivity implements AddTask.AddTaskListener{

    private List<GoalEntity> tasksItems;
    private ArrayAdapter<GoalEntity> tasksAdapter;
    private ListView tasksListView;
    private Button addTaskButton;
    private DatabaseHelper goalsDB;
    AppDatabase db;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.goal_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        GoalEntity goal = (GoalEntity) tasksListView.getItemAtPosition(position);
        System.out.println(goal);
        switch(item.getItemId()){
            case R.id.delete_option:
                //Cursor cursor = goalsDB.getItemID(goal);
                //goalsDB.Recursion(cursor);
                db.goalDao().delete(goal);
                tasksItems.remove(position);
                tasksAdapter.notifyDataSetChanged();
                return true;
            case R.id.count_option:
                String count = String.valueOf(db.goalDao().getAll().size());
                Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        String goal = getIntent().getStringExtra("goal");
        String id = getIntent().getStringExtra("id");
        setTitle(goal + id);

        goalsDB = new DatabaseHelper(this);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "goals_room").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        /*Cursor cursor = goalsDB.getItemID(goal);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        Cursor goalsData = goalsDB.getListContentsByID(id);

        while (goalsData.moveToNext()){
            tasksItems.add(goalsData.getString(1));
        }

         */
        tasksItems = db.goalDao().loadGoalsById(Integer.parseInt(id));
        tasksAdapter = new ArrayAdapter<GoalEntity>(this, android.R.layout.simple_list_item_1,tasksItems);
        tasksListView = (ListView) findViewById(R.id.tasks_list_view);
        tasksListView.setAdapter(tasksAdapter);

        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoalEntity goal = (GoalEntity) tasksListView.getItemAtPosition(position);
                Intent GoalIntent = new Intent(GoalActivity.this, GoalActivity.class);
                GoalIntent.putExtra("goal", goal.toString() );
                GoalIntent.putExtra("id", goal.getId().toString());
                startActivity(GoalIntent);
            }
        });

        registerForContextMenu(tasksListView);

        addTaskButton = findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

    }

    public void addTask(){
        AddTask addTask =  new AddTask();
        addTask.show(getSupportFragmentManager(), "Add Task");
    }

    public void applyTexts(String title, String description) {
        String goal = getIntent().getStringExtra("goal");
        Integer id = db.goalDao().findByName(goal).getId();
        GoalEntity subGoal = new GoalEntity();
        subGoal.setGoal(title);
        subGoal.setParentGoal(id);
        long subGoalId = db.goalDao().insertOne(subGoal);
        tasksItems.clear();
        tasksItems = db.goalDao().loadGoalsById(id);
        tasksAdapter.notifyDataSetChanged();
    }
}
