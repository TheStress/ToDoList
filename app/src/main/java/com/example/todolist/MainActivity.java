package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    Button editMenuButton;
    Button deleteMultiButton;
    Button editMultiTagsButton;

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
    RecyclerView taskView;
    RecyclerView.Adapter taskAdapter;
    RecyclerView.LayoutManager taskManager;
    int taskAmount;
    ArrayList<Task> taskList;

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
        ArrayList<String> tags;

        public Task() {
            name = "New Task";
            recurring = false;
            tags = new ArrayList<String>();
        }

        public Task(String initalName, boolean initalRecurring, boolean initalCompleted) {
            name = initalName;
            recurring = initalRecurring;
            completed = initalCompleted;
            tags = new ArrayList<String>();
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

    public void addingNewTag(String tagName) {
        tagList.add(tagName);
        saveNewTagList(tagList);
    }

    //Misc functions
    public void selectToggle(View view) {
        if (selecting) {
            selecting = false;
            selectButton.setText("Edit");

            editMultiTagsButton.setEnabled(false);
            deleteMultiButton.setEnabled(false);
            editMultiTagsButton.setAlpha(0);
            deleteMultiButton.setAlpha(0);

            //Clearing the selects from list
            for(int i = 0; i < taskList.size(); i++) {
                taskList.get(i).selected = false;
            }
        }
        else {
            selecting = true;
            selectButton.setText("Cancel");

            editMultiTagsButton.setEnabled(true);
            deleteMultiButton.setEnabled(true);
            editMultiTagsButton.setAlpha(1);
            deleteMultiButton.setAlpha(1);
        }

        //Updating adapters
        taskAdapter.notifyDataSetChanged();
        tagAdapter.notifyDataSetChanged();
    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        //Data set
        ArrayList<Task> taskDataSet;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class TaskViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public Button editButton;
            public TextView checkboxName;
            public TextView checkboxTags;
            public CheckBox checkBox;
            public ConstraintLayout constraintLayout;

            public TaskViewHolder(View v) {
                super(v);
                editButton = (Button) v.findViewById(R.id.checkboxEditButton);
                checkboxName = (TextView) v.findViewById(R.id.checkboxName);
                checkboxTags = (TextView) v.findViewById(R.id.checkboxTags);
                checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                constraintLayout = (ConstraintLayout) v.findViewById(R.id.taskConstraintLayout);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();

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
                                //Adding a task to the end of the list
                                taskList.add(task);

                                //Moving everything back by one
                                for (int i = position; i < taskList.size() - 1; i++) {
                                    taskList.set(i, taskList.get(i + 1));
                                }
                                //Removing the last item
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
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public TaskAdapter(ArrayList<Task> myDataset) {
            taskDataSet = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TaskAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_view_template, parent, false);
            TaskViewHolder vh = new TaskViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(TaskAdapter.TaskViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            //Getting element
            Task task = taskDataSet.get(position);

            //Naming task
            holder.checkboxName.setText(task.name);

            //Listing out Tags
            String tagsList = "";
            for(int i = 0; i < task.tags.size(); i++) {
                if (i == task.tags.size() - 1) {
                    tagsList += task.tags.get(i);
                }
                else {
                    tagsList += task.tags.get(i) + ", ";
                }
            }

            holder.checkboxTags.setText("Tags: ");

            //Checkbox Style and checked state
            Drawable drawable;
            if(selecting) {
                //Showing edit button
                holder.editButton.setEnabled(true);
                holder.editButton.setAlpha(1);

                //Changing checkbox
                holder.checkBox.setChecked(task.selected);
                drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.select_checkbox_image);
            }
            else {
                //Showing edit button
                holder.editButton.setEnabled(false);
                holder.editButton.setAlpha(0);

                //Changing checkbox
                holder.checkBox.setChecked(task.completed);
                drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.regular_checkbox_image);
            }
            holder.checkBox.setButtonDrawable(drawable);

            //Transparency
            if(task.completed) {
                holder.constraintLayout.setAlpha(0.25f);
            }
            else {
                holder.constraintLayout.setAlpha(1f);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return taskDataSet.size();
        }
    }

    public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> {
        //Data set
        ArrayList<String> dataSet;

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

                //Delete tag Button
                xButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataSet.remove(getAdapterPosition());
                        saveNewTagList(dataSet);
                    }
                });
            }
        }

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

            //Delete tag button visuals
            if (selecting) {
                holder.xButton.setEnabled(true);
                holder.xButton.setAlpha(1);
            }
            else {
                holder.xButton.setEnabled(false);
                holder.xButton.setAlpha(0);
            }
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

        deleteMultiButton = findViewById(R.id.deleteMultiButton);
        editMultiTagsButton = findViewById(R.id.editMultiTagsButton);

        editMultiTagsButton.setEnabled(false);
        deleteMultiButton.setEnabled(false);
        editMultiTagsButton.setAlpha(0);
        deleteMultiButton.setAlpha(0);

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
        /*ADD DEFAULT TAGAS OR JUST MAKE RECURRING WITHOUT TAGS*/
        tagList.add("");
        for(int i = 2; i < tagAmount; i++) {
            String foundTag = tagListPref.getString(i + "name", "");
            tagList.add(foundTag);
        }

        //Tag list
        tagView = findViewById(R.id.tagView);

        tagManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tagView.setLayoutManager(tagManager);

        tagAdapter = new TagAdapter(tagList);
        tagView.setAdapter(tagAdapter);

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

        //List View
        taskView = findViewById(R.id.taskListView);

        taskManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        taskView.setLayoutManager(taskManager);

        taskAdapter = new TaskAdapter(taskList);
        taskView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();

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