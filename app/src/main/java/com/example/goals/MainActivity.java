package com.example.goals;

import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements addGoal.AddGoalListener {

    List<GoalEntity> goalitems;
    ArrayAdapter<GoalEntity> goalsAdapter;
    ListView goalsListView;
    DatabaseHelper goalsDB;
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
        //String goal = goalsListView.getItemAtPosition(position).toString();
        GoalEntity goal =  (GoalEntity) goalsListView.getItemAtPosition(position);
        switch(item.getItemId()){
            case R.id.delete_option:
                //Cursor cursor = goalsDB.getItemID(goal);
                //goalsDB.Recursion(cursor);
                db.goalDao().delete(goal);
                goalitems.remove(position);
                goalsAdapter.notifyDataSetChanged();
                return true;
            case R.id.count_option:
                String count = String.valueOf(db.goalDao().getAll().size());
                Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit_option:
                editGoal();
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "goals_room").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        //goalsDB = new DatabaseHelper(this);
        //Cursor goalsData = goalsDB.getListContents();
        //GoalEntity item1 = new GoalEntity("gogo",null);
        //db.goalDao().insertAll(item1);
        db.goalDao().getAllWithoutPanrentGoal().subscribe(new SingleObserver<List<GoalEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<GoalEntity> goalEntities) {
                goalitems = goalEntities;
                System.out.println(goalitems);
                goalsAdapter = new ArrayAdapter<GoalEntity>(MainActivity.this, android.R.layout.simple_list_item_1,goalitems);
                goalsListView = findViewById(R.id.goalsListView);
                goalsListView.setAdapter(goalsAdapter);

                goalsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GoalEntity goal = (GoalEntity) goalsListView.getItemAtPosition(position);
                        Intent GoalIntent = new Intent(MainActivity.this, GoalActivity.class);
                        GoalIntent.putExtra("goal", goal.toString() );
                        GoalIntent.putExtra("id", goal.getId().toString());
                        startActivity(GoalIntent);

                    }
                });

                registerForContextMenu(goalsListView);

                Button button = findViewById(R.id.addGoalButton);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addGoal();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void addGoal(){
        addGoal addGoal =  new addGoal();
        addGoal.show(getSupportFragmentManager(), "Add GoalActivity");
    }

    public void editGoal(){
        editGoal editGoal = new editGoal();
        Bundle bundle = new Bundle();
        bundle.putString("title", "hello");
        editGoal.setArguments(bundle);
        editGoal.show(getSupportFragmentManager(),"Edit Goal");
    }

    @Override
    public void applyTexts(String title, String description) {
        //boolean insertData = goalsDB.addData(title);
        GoalEntity newGoal = new GoalEntity();
        newGoal.setGoal(title);
        newGoal.setDescription(description);
        db.goalDao().insertOne(newGoal);
    }
}
