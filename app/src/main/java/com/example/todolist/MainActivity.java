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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Intial Variable List
    Button addTaskButton;

    //Select Button
    boolean selecting = false;
    Button selectButton;
    Button deleteMultiButton;
    Button editMultiTagsButton;

    //information button
    Button infButton;

    //Add tag button
    Button addTaggButton;

    //edit tag button
    Button editTaggButton;

    ///Tag List
    RecyclerView tagView;
    RecyclerView.Adapter tagAdapter;
    RecyclerView.LayoutManager tagManager;
    int tagAmount;
    ArrayList<Tag> tagList;

    //Saved prefs for tag list
    SharedPreferences tagListPref;
    SharedPreferences.Editor tagListEditor;

    //Task list declarations
    RecyclerView taskView;
    RecyclerView.Adapter taskAdapter;
    RecyclerView.LayoutManager taskManager;
    int taskAmount;
    ArrayList<Task> taskList;
    ArrayList<Task> taskListDisplay;

    //Creating preferences and editor for task list
    SharedPreferences taskListPref;
    SharedPreferences.Editor taskListEditor;

    //Creating preferences for date
    SharedPreferences datePref;
    SharedPreferences.Editor dateEditor;

    //Task List functions
    public class Task{

        boolean completed = false;
        boolean selected = false;
        String name;
        ArrayList<String> tags;

        public Task() {
            name = "New Task";
            tags = new ArrayList<String>();
        }

        public Task(String initalName, boolean initalCompleted, ArrayList<String> initalTags) {
            name = initalName;
            completed = initalCompleted;
            tags = initalTags;
        }
    }

    public Task createTaskFromPreferences(SharedPreferences sharedPreferences, int id) {

        String name = sharedPreferences.getString(id + "name", "New Task");
        boolean completed = sharedPreferences.getBoolean(id + "completed", false);

        //Getting the set from saved prefs
        Set tagSet = new HashSet<String>();
        tagSet = sharedPreferences.getStringSet(id + "tags", tagSet);

        //Converting set to array to array list
        ArrayList<String> tags = new ArrayList<>();
        tags.addAll(tagSet);

        return new Task(name, completed, tags);
    }

    public void saveNewTaskList(ArrayList<Task> taskList) {
        //Clearing out anything before hand
        taskListEditor.clear();

        //Creating set for tags
        Set savingTags = new HashSet<String>();

        //Itterating through the list of tasks
        for (int i = 0; i < taskList.size(); i++) {
            taskListEditor.putString(i + "name", taskList.get(i).name);
            taskListEditor.putBoolean(i + "completed", taskList.get(i).completed);

            //Converting Array List to set to save
            savingTags.clear();
            savingTags.addAll(taskList.get(i).tags);
            //Saving the converted tags
            taskListEditor.putStringSet(i + "tags", savingTags);
        }
        taskListEditor.putInt("taskAmount", taskList.size());
        taskListEditor.commit();
        updateDisplayTaskList();
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
    public class Tag {
        String name;
        boolean checked;

        public Tag() {
            name = "New Tag";
            checked = false;
        }

        public Tag(String initialName, boolean initialChecked) {
            name = initialName;
            checked = initialChecked;
        }
    }

    public void saveNewTagList(ArrayList<Tag> tagList) {
        //Clearing previous
        tagListEditor.clear();

        //Saving everything
        for (int i = 0; i < tagList.size(); i++) {
            tagListEditor.putString(i + "name", tagList.get(i).name);
        }
        tagListEditor.putInt("tagAmount", tagList.size());
        tagListEditor.commit();
        tagAdapter.notifyDataSetChanged();
    }

    public void addingNewTag(Tag tagName) {
        tagList.add(tagName);
        saveNewTagList(tagList);
    }

    //Updates the displaying task list based on selected tags
    public void updateDisplayTaskList() {
        taskListDisplay.clear();

        //Itterating through tag list checking if any are checked off
        boolean foundChecked = false;
        for(int i = 0; i < tagList.size(); i++) {
            Tag tag = tagList.get(i);
            if (tag.checked) {
                foundChecked = true;
            }
        }

        //If nothing is checked just have the original task list display
        if (!foundChecked) {
            taskListDisplay.addAll(taskList);
            taskAdapter.notifyDataSetChanged();
            return;
        }

        //Itterating through the task list
        for(int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);

            //Itterating through tag list checking if they are selected
            for(int j = 0; j < tagList.size(); j++) {
                Tag tag = tagList.get(j);
                if (tag.checked) {

                    //If the user selected "No Tag"
                    if (j == 0) {
                        if (task.tags.size() == 0) {
                            taskListDisplay.add(task);
                            break;
                        }
                    }
                    else {
                        //Itterating through the task's tags to see it they match
                        for (int g = 0; g < task.tags.size(); g++) {
                            String taskTagName = task.tags.get(g);
                            if (tag.name.equals(taskTagName)) {
                                taskListDisplay.add(task);
                            }
                        }
                    }
                }
            }
        }

        taskAdapter.notifyDataSetChanged();
    }

    //Deleting tag and those off of tasks
    public void deleteTag(Tag tag){

        //Itterating through task list
        for(int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);

            //Itterating through the tags of tasks
            int sizeCheck = task.tags.size();
            for(int j = 0; j < sizeCheck; j++) {
                //Checking and removing properly
                if(tag.name.equals(task.tags.get(j))) {
                    task.tags.remove(j);
                    j--;
                    sizeCheck = task.tags.size();
                }
            }
        }
        saveNewTaskList(taskList);
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

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Getting task setup
                        int position = getAdapterPosition();
                        Task task = taskList.get(position);
                        task.selected = true;

                        //Unselecting other tasks
                        for (int i = 0; i < taskList.size(); i++) {
                            if (!taskList.get(i).equals(task)) {
                                taskList.get(i).selected = false;
                            }
                        }

                        //create a new intent of the popup class
                        Intent popUp = new Intent(MainActivity.this,Pop.class);
                        ArrayList<String> inputArray = new ArrayList<String>();
                        for(int i =0; i < tagList.size();i++){
                            inputArray.add(tagList.get(i).name);
                        }
                        popUp.putExtra("ButtonName","Done");
                        popUp.putExtra("TaskName",task.name);
                        popUp.putExtra("InputArray",inputArray);
                        startActivityForResult(popUp,4);
                    }
                });

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

            holder.checkboxTags.setText("Tags: " + tagsList);

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
        ArrayList<Tag> dataSet;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public CheckBox checkBox;
            public Button xButton;

            public MyViewHolder(View v) {
                super(v);
                checkBox = (CheckBox) v.findViewById(R.id.checkbox);
                xButton = (Button) v.findViewById(R.id.xButton);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();

                        //Unselecting other tags when clicking on "no tag" tag
                        if (position == 0) {
                            for (int i = 1; i < dataSet.size(); i++) {
                                dataSet.get(i).checked = false;
                            }
                        }
                        else {
                            dataSet.get(0).checked = false;
                        }

                        //Changing tag checked and updating display list
                        Tag tag = dataSet.get(position);
                        tag.checked = !tag.checked;
                        tagAdapter.notifyDataSetChanged();
                        updateDisplayTaskList();
                    }
                });

                //Delete tag Button
                xButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTag(dataSet.get(getAdapterPosition()));
                        dataSet.remove(getAdapterPosition());
                        saveNewTagList(dataSet);
                    }
                });
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public TagAdapter(ArrayList<Tag> myDataset) {
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

            //Getting the data set
            Tag tag = dataSet.get(position);

            //setting text of button
            holder.checkBox.setText(tag.name);
            holder.checkBox.setChecked(tag.checked);

            //Disabling the delete
            holder.xButton.setEnabled(false);
            holder.xButton.setAlpha(0);

            //Delete tag button visuals
            String name = dataSet.get(position).name;
            if (selecting) {
                //Checking if tags are the default ones
                if (position != 0 && position!= 1) {
                    holder.xButton.setEnabled(true);
                    holder.xButton.setAlpha(1);
                }
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add tag button
        addTaggButton = findViewById(R.id.newTagButton);


        //Select Button
        selectButton = findViewById(R.id.selectButton);

        //edit tag button
        editTaggButton = findViewById(R.id.editMultiTagsButton);


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

        //Default tags
        tagList.add(new Tag("No Tag", false));
        tagList.add(new Tag("Recurring", false));

        //Getting from saved prefs
        for(int i = 2; i < tagAmount; i++) {
            String foundName = tagListPref.getString(i + "name", "");
            tagList.add(new Tag(foundName, false));
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
        taskListDisplay = new ArrayList<>();

        //Getting the previous saved tasks
        for(int i = 0; i < taskAmount; i++) {
            Task task = createTaskFromPreferences(taskListPref, i);
            //Removing the completed tasks
            if(clearCompleted && task.completed==true) {
                if (task.tags.contains("Recurring")) {
                    task.completed = false;
                }
                else {
                    continue;
                }
            }
            taskList.add(task);
        }
        taskListDisplay.addAll(taskList);

        //List View
        taskView = findViewById(R.id.taskListView);

        taskManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        taskView.setLayoutManager(taskManager);

        taskAdapter = new TaskAdapter(taskListDisplay);
        taskView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();


        //Pop up box
        LinearLayout apple = findViewById(R.id.thebigthinglayout);
        apple.setBackgroundColor(Color.WHITE);
        addTaskButton = findViewById(R.id.addTaskButton);
    }

    //on click method for the pop up windows
    public void addTaskPopUp(View v){
        if(v == addTaskButton){
            //create a new intent of the popup class
            Intent popUp = new Intent(MainActivity.this,Pop.class);
            ArrayList<String> inputArray = new ArrayList<String>();
            for(int i =0; i < tagList.size();i++){
                inputArray.add(tagList.get(i).name);
            }
            popUp.putExtra("ButtonName","Add Task");
            popUp.putExtra("TaskName","");
            popUp.putExtra("InputArray",inputArray);
            startActivityForResult(popUp,1);
        }
        if(v == infButton){
            startActivity(new Intent(MainActivity.this,infoPop.class));
        }
        if(v==addTaggButton){
            Intent popUp =new Intent(MainActivity.this,tagPop.class);
            startActivityForResult(popUp,2);

        }
        if(v==editTaggButton){
            Intent popUp = (new Intent(MainActivity.this,editTagPop.class));
            ArrayList<String> inputArray = new ArrayList<String>();
            for(int i =0; i < tagList.size();i++){
                inputArray.add(tagList.get(i).name);
            }

            popUp.putExtra("InputArray",inputArray);
            startActivityForResult(popUp,3);
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
                boolean[] booleanList = data.getBooleanArrayExtra("BoolList");
                //need to fix this

                //New list of tags
                ArrayList<String> newTagList = new ArrayList<>();

                //Getting the selected tags in a new list
                for(int i = 0; i < tagList.size(); i++) {
                    if(booleanList[i]) {
                        newTagList.add(tagList.get(i).name);
                    }
                }

                Task task = new Task(in, false, newTagList);

                createNewTask(task);
            }
        }
        if(requestCode==2){
            if(resultCode == RESULT_OK){

                String tagName = data.getStringExtra("GetTheTextTag");
                addingNewTag(new Tag(tagName, false));

            }

        }
        if(requestCode==3){
            boolean[] booleanList = data.getBooleanArrayExtra("GetTheEditList");

            //New list of tags
            ArrayList<String> newTagList = new ArrayList<>();

            //Getting the selected tags in a new list
            for(int i = 0; i < tagList.size(); i++) {
                if(booleanList[i]) {
                    newTagList.add(tagList.get(i).name);
                }
            }

            //Finding the selected tasks for new list
            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i).selected) {
                    taskList.get(i).tags = newTagList;
                    taskList.get(i).selected = false;
                }
            }
            saveNewTaskList(taskList);
        }

        if(requestCode == 4) {
            String in = data.getStringExtra("GetTheText");
            boolean[] booleanList = data.getBooleanArrayExtra("BoolList");

            //New list of tags
            ArrayList<String> newTagList = new ArrayList<>();

            //Getting the selected tags in a new list
            for(int i = 0; i < tagList.size(); i++) {
                if(booleanList[i]) {
                    newTagList.add(tagList.get(i).name);
                }
            }

            //Editing task
            for(int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                if(task.selected) {
                    task.name = in;
                    task.tags = newTagList;
                    task.selected = false;
                }
            }
            saveNewTaskList(taskList);
        }
    }
}