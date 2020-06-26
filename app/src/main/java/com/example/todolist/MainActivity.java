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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.nio.file.StandardWatchEventKinds;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    Button addTaskButton;

    //Test
    int test = 0;

    //Task list declarations
    ListView listView;
    int taskAmount;
    ArrayList<Task> taskList;
    TaskAdapter taskAdapter;

    //Creating new preferences and the editor
    SharedPreferences taskListPref;
    SharedPreferences.Editor editor;

    public class Task{

        boolean completed = false;
        boolean recurring;
        String name;
        CheckBox checkBox;

        public Task() {
            name = "New Task";
            recurring = false;
        }

        public Task(String initalName, boolean initalRecurring, boolean initalCompleted) {
            name = initalName;
            recurring = initalRecurring;
            completed = initalCompleted;
            checkBox = checkBoxFromTask(this);
        }
    }

    public CheckBox checkBoxFromTask(Task task) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(task.completed);
        checkBox.setText(task.name);
        return checkBox;
    }

    public Task createTaskFromPreferences(SharedPreferences sharedPreferences, int id) {

        String name = sharedPreferences.getString(id + "name", "New Task");
        boolean recurring = sharedPreferences.getBoolean(id + "recurring", false);
        boolean completed = sharedPreferences.getBoolean(id + "completed", false);

        return new Task(name, recurring, completed);
    }

    public void saveNewTaskList(ArrayList<Task> taskList) {
        //Clearing out anything before hand
        editor.clear();

        //Saving everything
        for (int i = 0; i < taskList.size(); i++) {
            editor.putString(i + "name", taskList.get(i).name);
            editor.putBoolean(i + "recurring", taskList.get(i).recurring);
            editor.putBoolean(i + "completed", taskList.get(i).completed);
        }
        editor.putInt("taskAmount", taskList.size());
        editor.commit();

        /*
        taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);

         */
        taskAdapter.notifyDataSetChanged();
    }

    public void createNewTask(Task task) {
        boolean foundCompletedTask = false;
        if(taskList.size() == 0)  {
            taskList.add(task);
        }
        else {
            for (int i = 0; i < taskList.size(); i++) {

                //Sorting list if found something
                if (taskList.get(i).completed) {
                    taskList.add(task);
                    //Rearranging list for completed task
                    for (int j = taskList.size() - 1; j > i; j--) {
                        taskList.set(j, taskList.get(j - 1));
                    }
                    taskList.set(i, task);
                    foundCompletedTask = true;
                    break;
                }
            }

            if(!foundCompletedTask) {
                taskList.add(task);
            }
        }
        saveNewTaskList(taskList);
    }

    public void addDefaultTask(View view) {
        Task task = new Task(String.valueOf(test), false, false);
        test++;
        createNewTask(task);
    }

    public void clearTasks(View view) {
        taskList.clear();
        saveNewTaskList(taskList);
    }

    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(Context context, ArrayList<Task> data) {
            super(context, 0, data);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Task task = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_template, parent, false);
            }
            // Lookup view for data population
            CheckBox checkBox = convertView.findViewById(R.id.checkBox);

            // Populate the data into the template view using the data object
            checkBox.setText(task.name);
            checkBox.setChecked(task.completed);

            if(checkBox.isChecked()) {
                checkBox.setAlpha(0.25f);
            }
            else {
                checkBox.setAlpha(1f);
            }

            // Return the completed view to render on screen
            return convertView;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //List View
        listView = findViewById(R.id.taskListView);

        //Saved preferences
        taskListPref = getSharedPreferences("TASK_LIST", Context.MODE_PRIVATE);
        editor = taskListPref.edit();

        //Getting preferences and creating task list
        taskAmount = taskListPref.getInt("taskAmount", 0);
        taskList = new ArrayList<>();

        //Getting the previous saved tasks
        for(int i = 0; i < taskAmount; i++) {
            Task task = createTaskFromPreferences(taskListPref, i);
            taskList.add(task);
        }
        taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = taskList.get(position);

                task.completed = !task.completed;
                if(task.completed) {
                    //Rearranging list for completed task
                    taskList.add(task);
                    for(int i = position; i < taskList.size() - 1; i++) {
                        taskList.set(i, taskList.get(i + 1));
                    }
                    taskList.remove(taskList.size() - 1);
                }
                else {
                    for(int i = 0; i < taskList.size(); i++) {
                        //If you find itself at the top then just break
                        if (taskList.get(i).equals(task)) {
                            break;
                        }

                        //Sorting list if found something
                        if(taskList.get(i).completed == true) {
                            //Rearranging list for completed task
                            for(int j = position; j > i; j--) {
                                taskList.set(j, taskList.get(j - 1));
                            }
                            taskList.set(i, task);
                            break;
                        }
                    }

                }
                saveNewTaskList(taskList);
            }
        });


        //Pop up box
        LinearLayout apple = findViewById(R.id.thebigthinglayout);
        apple.setBackgroundColor(Color.WHITE);
        addTaskButton = findViewById(R.id.addTaskButton);


    }
    public void addTaskPopUp(View v){
        if(v == addTaskButton){
            startActivity(new Intent(MainActivity.this,Pop.class));

            LinearLayout owo = findViewById(R.id.thebigthinglayout);
            owo.setBackgroundColor(Color.GRAY);
        }

    }

}