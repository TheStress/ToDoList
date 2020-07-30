package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.Nullable;
public class tagPop extends Activity {
    Button CloseTag,addTag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taggpop);

        //defining buttons
        addTag = findViewById(R.id.addTagButton);
        CloseTag = findViewById(R.id.closeTagButton);


        //Changing display height and width
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.5));

    }
    public void tagOnClick(View v){
        //if close finish activity
        if(v == CloseTag){
            tagPop.this.finish();
        }
        if(v==addTag){
            //if add tag push the string tag back to main activity
            EditText a = findViewById(R.id.tagInput);
            String input= a.getText().toString();
            Intent main = getIntent();
            main.putExtra("GetTheTextTag", input);
            setResult(RESULT_OK, main);
            finish();
        }
    }
}

