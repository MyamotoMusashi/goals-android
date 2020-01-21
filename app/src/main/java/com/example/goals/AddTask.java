package com.example.goals;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddTask extends AppCompatDialogFragment {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private AddTaskListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddTaskListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_task_dialog,null);

        builder.setView(view);
        builder.setTitle("Add Task");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                listener.applyTexts(title,description);
            }
        });

        editTextTitle = view.findViewById(R.id.edit_task_title);
        editTextDescription = view.findViewById(R.id.edit_task_description);

        return builder.create();
    }

    public interface AddTaskListener{
        void applyTexts(String title, String description);
    }
}
