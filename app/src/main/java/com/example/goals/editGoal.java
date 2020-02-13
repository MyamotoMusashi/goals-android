package com.example.goals;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class editGoal extends AppCompatDialogFragment {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextId;
    private addGoal.AddGoalListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (addGoal.AddGoalListener) context;
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
                listener.applyTexts(title,description);

            }
        });

        editTextTitle = view.findViewById(R.id.edit_goal_title);
        editTextTitle.setText(bundle.getString("title"));
        editTextId = view.findViewById(R.id.edit_goal_id);
        editTextId.setText(bundle.getString("id"));
        editTextDescription = view.findViewById(R.id.edit_goal_description);


        return builder.create();
    }

    public interface AddGoalListener{
        void applyTexts(String title, String description);
    }
}
