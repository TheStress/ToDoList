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
    RecyclerView ListView;
    RecyclerView.Adapter listViewAdapter;
    RecyclerView.LayoutManager listViewManager;
    boolean[] endList;
    ArrayList<String> inList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittaskspopup);

        Intent intent = getIntent();
        inList = intent.getStringArrayListExtra("InputArray");

        endList = new boolean[inList.size()];

        closeEdit = findViewById(R.id.closeEditTag);

        //Changing display height and width
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.5));

        //List View
        ListView = findViewById(R.id.listview);

        listViewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ListView.setLayoutManager(listViewManager);

        listViewAdapter = new TagEditAdapter(inList);
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
                        endList[position] = !endList[position];
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
            holder.checkBox.setChecked(endList[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    public void editTagOnClick(View v){
        if(v==closeEdit){
            Intent main = getIntent();
            main.putExtra("GetTheEditList", endList);
            setResult(RESULT_OK, main);
            finish();
        }
    }
}

