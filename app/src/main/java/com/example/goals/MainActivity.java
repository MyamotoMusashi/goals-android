package com.example.goals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements addGoal.AddGoalListener, editGoal.EditGoalListener {

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
        GoalEntity goal =  (GoalEntity) goalsListView.getItemAtPosition(position);
        switch(item.getItemId()){
            case R.id.delete_option:
                db.goalDao().delete(goal);
                goalitems.remove(position);
                goalsAdapter.notifyDataSetChanged();
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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void addGoal(){
        addGoal addGoal =  new addGoal();
        addGoal.show(getSupportFragmentManager(), "Add GoalActivity");
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

    @Override
    public void applyTexts(String title, String description) {
        GoalEntity newGoal = new GoalEntity();
        newGoal.setGoal(title);
        newGoal.setDescription(description);
        db.goalDao().insertOne(newGoal);
        init();
    }

    @Override
    public void editTexts(String title, String description, String id, String parentGoal){
        if(parentGoal.equals("0")) {
            parentGoal = null;
        }

        db.goalDao().editGoal(title, id, parentGoal);
        init();
    }

    public void init(){
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "goals_room").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        db.goalDao().getAllWithoutPanrentGoal().subscribe(new SingleObserver<List<GoalEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<GoalEntity> goalEntities) {
                goalitems = goalEntities;
                System.out.println(db.goalDao().getGoalsWithSubGoals().get(0).getGoal());
                goalsAdapter = new ArrayAdapter<GoalEntity>(MainActivity.this, android.R.layout.simple_list_item_1,goalitems){
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
}
