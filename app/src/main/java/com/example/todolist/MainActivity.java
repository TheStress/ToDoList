package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    Button addTaskButton;
    Boolean reccuring;
    String taskName;

    //Test
    int test = 0;
    String currentTime;

    //Select Button
    boolean selecting = false;
    Button selectButton;
    Button trashButton;

    //Task list declarations
    ListView listView;
    int taskAmount;
    ArrayList<Task> taskList;
    TaskAdapter taskAdapter;

    //Creating preferences and editor for task list
    SharedPreferences taskListPref;
    SharedPreferences.Editor taskListEditor;

    //Creating preferences for date
    SharedPreferences datePref;
    SharedPreferences.Editor dateEditor;

    public class Task{

        boolean completed = false;
        boolean recurring;
        boolean selected = false;
        String name;

        public Task() {
            name = "New Task";
            recurring = false;
        }

        public Task(String initalName, boolean initalRecurring, boolean initalCompleted) {
            name = initalName;
            recurring = initalRecurring;
            completed = initalCompleted;
        }
    }

    public Task createTaskFromPreferences(SharedPreferences sharedPreferences, int id) {

        String name = sharedPreferences.getString(id + "name", "New Task");
        boolean recurring = sharedPreferences.getBoolean(id + "recurring", false);
        boolean completed = sharedPreferences.getBoolean(id + "completed", false);

        return new Task(name, recurring, completed);
    }

    public void saveNewTaskList(ArrayList<Task> taskList) {
        //Clearing out anything before hand
        taskListEditor.clear();

        //Saving everything
        for (int i = 0; i < taskList.size(); i++) {
            taskListEditor.putString(i + "name", taskList.get(i).name);
            taskListEditor.putBoolean(i + "recurring", taskList.get(i).recurring);
            taskListEditor.putBoolean(i + "completed", taskList.get(i).completed);
        }
        taskListEditor.putInt("taskAmount", taskList.size());
        taskListEditor.commit();
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

    public void selectToggle(View view) {
        if (selecting) {
            selecting = false;
            selectButton.setText("Select");
            trashButton.setEnabled(false);
            trashButton.setAlpha(0);

            //Clearing the selects from list
            for(int i = 0; i < taskList.size(); i++) {
                taskList.get(i).selected = false;
            }
        }
        else {
            selecting = true;
            selectButton.setText("Cancel");
            trashButton.setEnabled(true);
            trashButton.setAlpha(1);
        }
        taskAdapter.notifyDataSetChanged();
    }

    public void deleteSelectedTasks(View view) {
        //Deleting tasks
        int sizeCheck = taskList.size();
        for(int i = 0; i < sizeCheck; i++) {
            if(taskList.get(i).selected) {
               taskList.remove(i);
               i--;
               sizeCheck = taskList.size();
            }
        }
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

            //Checkbox Style and checked state
            Drawable drawable;
            if(selecting) {
                checkBox.setChecked(task.selected);
                drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.select_checkbox_image);
            }
            else {
                checkBox.setChecked(task.completed);
                drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.regular_checkbox_image);
            }
            checkBox.setButtonDrawable(drawable);

            //Transparency
            if(task.completed) {
                checkBox.setAlpha(0.25f);
            }
            else {
                checkBox.setAlpha(1f);
            }
            if(task.recurring==true){
                checkBox.setAlpha(0.5F);
            }else{
                checkBox.setAlpha(1F);
            }

            // Return the completed view to render on screen
            return convertView;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Select Button
        selectButton = findViewById(R.id.selectButton);
        trashButton = findViewById(R.id.trashButton);
        trashButton.setEnabled(false);
        trashButton.setAlpha(0);

        //List View
        listView = findViewById(R.id.taskListView);

        //Date preferences
        datePref = getSharedPreferences("DATE_PREF", Context.MODE_PRIVATE);
        dateEditor = datePref.edit();

        //Comparing previous login date with current to clear completed tasks
        Calendar currentCalendar = Calendar.getInstance();

        int lastDaySaved = datePref.getInt("lastDayLogin", 0);
        int lastYearSaved = datePref.getInt("lastYearLogin", 0);

        int today = currentCalendar.get(currentCalendar.DAY_OF_YEAR);
        int thisYear = currentCalendar.get(currentCalendar.YEAR);

        boolean clearCompleted = false;
        if(today > lastDaySaved || thisYear > lastYearSaved) {
            clearCompleted = true;
        }

        dateEditor.putInt("lastDayLogin", today);
        dateEditor.putInt("lastYearLogin", thisYear);
        dateEditor.commit();

        //Saved preferences for task list
        taskListPref = getSharedPreferences("TASK_LIST", Context.MODE_PRIVATE);
        taskListEditor = taskListPref.edit();

        //Getting preferences and creating task list
        taskAmount = taskListPref.getInt("taskAmount", 0);
        taskList = new ArrayList<>();

        //Getting the previous saved tasks
        for(int i = 0; i < taskAmount; i++) {
            Task task = createTaskFromPreferences(taskListPref, i);
            if(clearCompleted&& task.completed==true) {
                if (task.recurring != true) {
                    break;
                }
                if (task.recurring == true) {
                    task.completed = false;
                }
            }
            taskList.add(task);
        }

        //Setting adapter to list view
        taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);
        saveNewTaskList(taskList);

        //Setting onclick for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Getting task
                Task task = taskList.get(position);
                if (selecting) {
                    //Changing checked satus
                    task.selected = !task.selected;
                    taskAdapter.notifyDataSetChanged();
                } else {
                    //Changing checked status
                    task.completed = !task.completed;

                    //Rearranging checkboxes completed/uncompleted
                    if (task.completed) {
                        //Rearranging list for completed task
                        taskList.add(task);
                        for (int i = position; i < taskList.size() - 1; i++) {
                            taskList.set(i, taskList.get(i + 1));
                        }
                        taskList.remove(taskList.size() - 1);
                    } else {
                        for (int i = 0; i < taskList.size(); i++) {
                            //If you find itself at the top then just break
                            if (taskList.get(i).equals(task)) {
                                break;
                            }

                            //Sorting list if found something
                            if (taskList.get(i).completed == true) {
                                //Rearranging list for completed task
                                for (int j = position; j > i; j--) {
                                    taskList.set(j, taskList.get(j - 1));
                                }
                                taskList.set(i, task);
                                break;
                            }
                        }

                    }
                    saveNewTaskList(taskList);
                }
            }
        });

        //Pop up box
        LinearLayout apple = findViewById(R.id.thebigthinglayout);
        apple.setBackgroundColor(Color.WHITE);
        addTaskButton = findViewById(R.id.addTaskButton);
    }
    //on click method for the pop up
    public void addTaskPopUp(View v){
        if(v == addTaskButton){
            //create a new intent of the popup class
            Intent popUp =new Intent(MainActivity.this,Pop.class);
            startActivityForResult(popUp,1);
            //in addition set the background color to grey
            LinearLayout owo = findViewById(R.id.thebigthinglayout);
            owo.setBackgroundColor(Color.GRAY);
        }

    }

    //on the end of the activity do this
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //change background back to white
        LinearLayout owo = findViewById(R.id.thebigthinglayout);
        owo.setBackgroundColor(Color.WHITE);
        super.onActivityResult(requestCode, resultCode, data);
        //if pop up activity make a new task with given elements from pop up activity
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String in = data.getStringExtra("GetTheText");
                Boolean inBool = data.getBooleanExtra("GetTheRec",false);
                Task task =new Task(in, inBool,false);
                createNewTask(task);
            }
        }
    }
}