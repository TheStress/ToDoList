package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    int taskAmount = 0;

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
        editor.apply();
    }

}