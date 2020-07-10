package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Pop extends Activity {
    //declaring the variables

    Button closeButton;
    Button addTask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //setting title of popup
        setTitle("Create New Task");

        //making Popup window into a width x height dimension
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popu_up_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.5));

        //Setting the Buttons on the xml to button variables
        closeButton = findViewById(R.id.closeButton);
        addTask = findViewById(R.id.addbutton);
    }

    //onclick methods
    public void CP(View v){
        //if close finish activity
        if(v == closeButton){
            Pop.this.finish();
        }
        //if addTask then push two values string name and reccuring back to the main activity and end the activity
        if(v==addTask){
            EditText a = findViewById(R.id.input);
            String input= a.getText().toString();
            boolean recur= ( (Switch) findViewById(R.id.reccurringswitch) ).isChecked();
            Intent main = getIntent();
            main.putExtra("GetTheText", input);
            main.putExtra("GetTheRec", recur);
            setResult(RESULT_OK, main);
            finish();
        }
    }
}
