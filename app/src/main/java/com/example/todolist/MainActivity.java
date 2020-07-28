package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SharedElementCallback;
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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    Button addTaskButton;
    Boolean reccuring;
    String taskName;

    //Select Button
    boolean selecting = false;
    Button selectButton;
    Button trashButton;

    //information button
    Button infButton;

    ///Tag List
    RecyclerView tagView;
    RecyclerView.Adapter tagAdapter;
    RecyclerView.LayoutManager tagManager;
    int tagAmount;
    ArrayList<String> tagList;

    int test;

    //Saved prefs for tag list
    SharedPreferences tagListPref;
    SharedPreferences.Editor tagListEditor;

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

    //Task List functions
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

    //Tag List functions
    public void saveNewTagList(ArrayList<String> tagList) {
        //Clearing previous
        tagListEditor.clear();

        //Saving everything
        for (int i = 0; i < tagList.size(); i++) {
            tagListEditor.putString(i + "name", tagList.get(i));
        }
        tagListEditor.putInt("tagAmount", tagList.size());
        tagListEditor.commit();
        tagAdapter.notifyDataSetChanged();
    }

    //Misc functions
    public void selectToggle(View view) {
        if (selecting) {
            selecting = false;
            selectButton.setText("Edit");
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
        tagAdapter.notifyDataSetChanged();
    }

    //Adapters
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

            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> {
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public Button button;
            public Button xButton;
            public MyViewHolder(View v) {
                super(v);
                button = (Button) v.findViewById(R.id.button);
                xButton = (Button) v.findViewById(R.id.xButton);
            }
        }

        //Data set
        ArrayList<String> dataSet;

        // Provide a suitable constructor (depends on the kind of dataset)
        public TagAdapter(ArrayList<String> myDataset) {
            dataSet = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TagAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tag_view_template, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(TagAdapter.MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            //setting text of button
            holder.button.setText(dataSet.get(position));

            //Delete tag button
            if (selecting) {
                holder.xButton.setEnabled(true);
                holder.xButton.setAlpha(1);
            }
            else {
                holder.xButton.setEnabled(false);
                holder.xButton.setAlpha(0);
            }

            holder.xButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSet.remove(position);
                    saveNewTagList(dataSet);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    public void createDefaultTag(View view) {
        tagList.add(Integer.toString(test));
        test++;
        saveNewTagList(tagList);
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

        //information button
        infButton = (Button) findViewById(R.id.infoButton);

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

        //Save Pref for tag
        tagListPref = getSharedPreferences("TAG_LIST", Context.MODE_PRIVATE);
        tagListEditor = tagListPref.edit();

        //Getting saved Tags
        tagAmount = tagListPref.getInt("tagAmount", 0);
        tagList = new ArrayList<>();

        for(int i = 0; i < tagAmount; i++) {
            String foundTag = tagListPref.getString(i + "name", "");
            tagList.add(foundTag);
        }

        //Tag list
        tagView = findViewById(R.id.tagView);

        tagManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tagView.setLayoutManager(tagManager);

        tagView.setHasFixedSize(true);

        tagAdapter = new TagAdapter(tagList);
        tagView.setAdapter(tagAdapter);

        //List View
        listView = findViewById(R.id.taskListView);

        //Saved preferences for task list
        taskListPref = getSharedPreferences("TASK_LIST", Context.MODE_PRIVATE);
        taskListEditor = taskListPref.edit();

        //Getting preferences and creating task list
        taskAmount = taskListPref.getInt("taskAmount", 0);
        taskList = new ArrayList<>();

        //Getting the previous saved tasks
        for(int i = 0; i < taskAmount; i++) {
            Task task = createTaskFromPreferences(taskListPref, i);
            //Removing the completed tasks
            if(clearCompleted && task.completed==true) {
                if (task.recurring) {
                    task.completed = false;
                }
                else {
                    continue;
                }
            }
            taskList.add(task);
        }

        //Setting adapter to list view
        taskAdapter = new TaskAdapter(this, taskList);
        listView.setAdapter(taskAdapter);
        saveNewTaskList(taskList);

        //Setting onclick for list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Getting task
                Task task = taskList.get(position);
                if (selecting) {
                    //Changing checked status
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

        }
        if(v == infButton){
            startActivity(new Intent(MainActivity.this,infoPop.class));
        }


    }

    //on the end of the activity do this
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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