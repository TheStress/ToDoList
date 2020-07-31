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

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class editTagPop extends Activity {
    Button closeEdit;
/*    RecyclerView ListView;
    RecyclerView.Adapter listViewAdapter;
    RecyclerView.LayoutManager listViewManager;

    Intent intent = getIntent();
    ArrayList inList = intent.getStringArrayListExtra("InputArray");*/
    boolean[] endList;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittaskspopup);

        //endList = new boolean[inList.size()];

        closeEdit = findViewById(R.id.closeEditTag);

        //Changing display height and width
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.5));
/*
        //List View
        ListView = findViewById(R.id.listview);

        listViewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ListView.setLayoutManager(listViewManager);

        listViewAdapter = new MainActivity.TaskAdapter(inList);
        ListView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();*/

    }
/*    public class TagAdapter extends RecyclerView.Adapter<MainActivity.TagAdapter.MyViewHolder> {
        //Data set
        ArrayList<MainActivity.Tag> dataSet;

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
                        MainActivity.Tag tag = dataSet.get(position);
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
        public TagAdapter(ArrayList<MainActivity.Tag> myDataset) {
            dataSet = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MainActivity.TagAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tag_view_template, parent, false);
            MainActivity.TagAdapter.MyViewHolder vh = new MainActivity.TagAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MainActivity.TagAdapter.MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            //Getting the data set
            MainActivity.Tag tag = dataSet.get(position);

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
    }*/

    public void editTagOnClick(View v){
        if(v==closeEdit){
            Intent main = getIntent();
            main.putExtra("GetTheEditList", endList);
            setResult(RESULT_OK, main);
            finish();
        }
    }
}

