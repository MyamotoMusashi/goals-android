package com.example.goals;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class editGoal extends AppCompatDialogFragment {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextId;
    private EditText editParentGoal;
    private editGoal.EditGoalListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (editGoal.EditGoalListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddGoalListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_edit_goal,null);

        Bundle bundle = getArguments();

        builder.setView(view);
        builder.setTitle("Edit Goal");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String id = editTextId.getText().toString();
                String parentGoal = editParentGoal.getText().toString();
                listener.editTexts(title,description, id, parentGoal);

            }
        });

        editTextTitle = view.findViewById(R.id.edit_goal_title);
        editTextTitle.setText(bundle.getString("title"));
        editTextId = view.findViewById(R.id.edit_goal_id);
        editTextId.setText(bundle.getString("id"));
        editTextDescription = view.findViewById(R.id.edit_goal_description);
        editParentGoal = view.findViewById(R.id.edit_goal_parent_goal);
        if(bundle.getString("parent_goal" ) == "null") {
            editParentGoal.setText("0");
        }
        else {
            editParentGoal.setText(bundle.getString("parent_goal"));
        }

        return builder.create();
    }

    public interface EditGoalListener{
        void editTexts(String title, String description, String id, String parentGoal);
    }
}
