package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    int taskAmount = 0;
    Button addTaskButton;

    public class Task {

        boolean completed = false;
        boolean recurring = false;
        int position;
        String name = "New Task";

        public Task(String initalName, int initalPosition, boolean initalRecurring) {
            name = initalName;
            position = initalPosition;
            recurring = initalRecurring;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating new preferences and the editor
        SharedPreferences taskList = getSharedPreferences("TASK_LIST", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = taskList.edit();
        LinearLayout apple = (LinearLayout ) findViewById(R.id.thebigthinglayout);
        apple.setBackgroundColor(Color.WHITE);
        editor.apply();
        addTaskButton =(Button)findViewById(R.id.addTaskButton);


    }
    public void addTaskPopUp(View v){
        if(v == addTaskButton){
            startActivity(new Intent(MainActivity.this,Pop.class));

            LinearLayout owo = (LinearLayout ) findViewById(R.id.thebigthinglayout);
            owo.setBackgroundColor(Color.GRAY);


        }

    }

}