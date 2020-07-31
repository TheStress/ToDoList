package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Pop extends Activity {
    //declaring the variables
    Button closeButton;
    Button addTask;
    Button openEdit;
    EditText a;
    ArrayList<String> lists;
    boolean[] booleanList;

    //Recycler View
    RecyclerView ListView;
    RecyclerView.Adapter listViewAdapter;
    RecyclerView.LayoutManager listViewManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


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
        addTask =findViewById(R.id.addbutton);


        //setting title of popup
        setTitle("Create New Task");
        Intent intent = getIntent();
        lists = intent.getStringArrayListExtra("InputArray");
        String buttonName = intent.getStringExtra("ButtonName");
        String taskName = intent.getStringExtra("TaskName");

        a =  findViewById(R.id.inputString);

        a.setText(taskName);
        addTask.setText(buttonName);

        booleanList = new boolean[lists.size()];

        //List View
        ListView = findViewById(R.id.recyclerView);

        listViewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ListView.setLayoutManager(listViewManager);

        listViewAdapter = new TagEditAdapter(lists);
        ListView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
    }

    public class TagEditAdapter extends RecyclerView.Adapter<TagEditAdapter.MyViewHolder> {
        //Data set
        ArrayList<String> dataSet;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public CheckBox checkBox;

            public MyViewHolder(View v) {
                super(v);
                checkBox = (CheckBox) v.findViewById(R.id.editTagCheckbox);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        booleanList[position] = !booleanList[position];
                    }
                });
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public TagEditAdapter(ArrayList<String> myDataset) {
            dataSet = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TagEditAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.edit_tag_view_template, parent, false);
            TagEditAdapter.MyViewHolder vh = new TagEditAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(TagEditAdapter.MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            //setting text of button
            holder.checkBox.setText(dataSet.get(position));
            holder.checkBox.setChecked(booleanList[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
    //onclick methods
    public void CP(View v){
        //if close finish activity
        if(v == closeButton){
            Pop.this.finish();
        }
        //if addTask then push two values string name and reccuring back to the main activity and end the activity
        if(v==addTask){
            EditText a = findViewById(R.id.inputString);
            String input= a.getText().toString();

            Intent main = getIntent();
            main.putExtra("GetTheText", input);
            main.putExtra("BoolList",booleanList);
            setResult(RESULT_OK, main);
            finish();
        }
        if(v==openEdit){
            Intent popUp = (new Intent(Pop.this,editTagPop.class));
            popUp.putExtra("InputArray",lists);
            startActivityForResult(popUp,3);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==3){
            booleanList = data.getBooleanArrayExtra("GetTheEditList");
        }
    }
}
