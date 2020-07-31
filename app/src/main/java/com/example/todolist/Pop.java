package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Pop extends Activity {
    //declaring the variables

    Button closeButton;
    Button addTask;
    Button openEdit;
    EditText a;
    ArrayList<String> lists;
    boolean[] booleanList;
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
