package com.example.goals;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;


public class GoalActivity extends AppCompatActivity implements AddTask.AddTaskListener, editGoal.EditGoalListener {

    private List<GoalEntity> tasksItems;
    private String oldParentGoal;
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
                db.goalDao().delete(goal);
                tasksItems.remove(position);
                tasksAdapter.notifyDataSetChanged();
                return true;
            case R.id.count_option:
                String count = String.valueOf(db.goalDao().getAll().size());
                Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit_option:
                editGoal(goal.getId().toString(), goal.getGoal(), goal.getParentGoal());
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
        oldParentGoal = id;

        goalsDB = new DatabaseHelper(this);
        init(id);
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
        init(String.valueOf(id));
    }

    @Override
    public void editTexts(String title, String description, String id, String parentGoal){
        if(parentGoal.equals("0")) {
            parentGoal = null;
        }
        db.goalDao().editGoal(title, id, parentGoal);
        init(oldParentGoal);
    }

    public void editGoal(String id, String title, Integer parentGoal){
        editGoal editGoal = new editGoal();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("parent_goal",String.valueOf(parentGoal));
        editGoal.setArguments(bundle);
        editGoal.show(getSupportFragmentManager(),"Edit Goal");
    }

    public void init(String id){
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "goals_room").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        db.goalDao().loadGoalsById(Integer.parseInt(id)).subscribe(new SingleObserver<List<GoalEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<GoalEntity> goalEntities) {
                tasksItems = goalEntities;
                tasksAdapter = new ArrayAdapter<GoalEntity>(GoalActivity.this, android.R.layout.simple_list_item_1,tasksItems){
                    @Override
                    public View getView(int position, View convertView, final ViewGroup parent) {
                        final View row = super.getView(position, convertView, parent);

                        db.goalDao().hasChildren(super.getItem(position).getId()).subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                if (integer > 0) {

                                    row.setBackgroundColor (Color.parseColor("#50c878"));
                                }
                                else {
                                    row.setBackgroundColor (Color.WHITE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                        return row;
                    }
                };

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

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}
