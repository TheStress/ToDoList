package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    Button addTaskButton;

    ListView listView;
    int taskAmount;
    ArrayList<Task> taskList;

    //Creating new preferences and the editor
    SharedPreferences taskListPref;
    SharedPreferences.Editor editor;

    public class Task{

        boolean completed = false;
        boolean recurring;
        int position;
        String name = "";
        CheckBox checkBox;

        public Task() {
            name = "New Task";
            position = 0;
            recurring = false;
            checkBox = createCheckBoxFromTask(this);
        }

        public Task(String initalName, int initalPosition, boolean initalRecurring, boolean initalCompleted) {
            name = initalName;
            position = initalPosition;
            recurring = initalRecurring;
            completed = initalCompleted;
            checkBox = createCheckBoxFromTask(this);
        }
    }

    public Task createTaskFromPreferences(SharedPreferences sharedPreferences, int id) {

        String name = sharedPreferences.getString(id + "name", "New Task");
        int position = sharedPreferences.getInt(id + "position", id);
        boolean recurring = sharedPreferences.getBoolean(id + "recurring", false);
        boolean completed = sharedPreferences.getBoolean(id + "completed", false);

        return new Task(name, position, recurring, completed);
    }

    public CheckBox createCheckBoxFromTask(Task task) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(task.name);
        checkBox.setChecked(task.completed);
        return checkBox;
    }

    public void saveNewTaskList(ArrayList<Task> taskList) {

        for (int i = 0; i < taskList.size(); i++) {
            editor.putString(i + "name", taskList.get(i).name);
            editor.putInt(i + "position", taskList.get(i).position);
            editor.putBoolean(i + "recurring", taskList.get(i).recurring);
            editor.putBoolean(i + "completed", taskList.get(i).completed);
        }
        editor.putInt("taskAmount", taskList.size());
        editor.commit();

        TaskAdapter taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);
    }

    public void addDefaultTask(View view) {
        Task task = new Task("other name", 0, false, false);
        taskList.add(task);
        saveNewTaskList(taskList);
    }

    public void clearTasks(View view) {
        taskList.clear();
        saveNewTaskList(taskList);
    }

    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(Context context, ArrayList<Task> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Task task = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_template, parent, false);
            }
            // Lookup view for data population
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            // Populate the data into the template view using the data object
            checkBox.setText(task.name);
            checkBox.setChecked(task.completed);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Layout
        listView = findViewById(R.id.taskListView);

        //Saved preferences
        taskListPref = getSharedPreferences("TASK_LIST", Context.MODE_PRIVATE);
        editor = taskListPref.edit();

        //Getting preferences and creating task list
        taskAmount = taskListPref.getInt("taskAmount", 0);
        taskList = new ArrayList<>();

        for(int i = 0; i < taskAmount; i++) {
            Task task = createTaskFromPreferences(taskListPref, i);
            taskList.add(task);
        }
        TaskAdapter taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);


        //Pop up box
        LinearLayout apple = (LinearLayout ) findViewById(R.id.thebigthinglayout);
        apple.setBackgroundColor(Color.WHITE);
        addTaskButton = (Button)findViewById(R.id.addTaskButton);


    }
    public void addTaskPopUp(View v){
        if(v == addTaskButton){
            startActivity(new Intent(MainActivity.this,Pop.class));

            LinearLayout owo = (LinearLayout ) findViewById(R.id.thebigthinglayout);
            owo.setBackgroundColor(Color.GRAY);
        }

    }

}