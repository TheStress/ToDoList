package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class infoPop extends Activity {
    Button CloseB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infopopup);

        CloseB = findViewById(R.id.closeInfo);
        //setting the text views to have bold text in part of the string
        TextView s1 = (TextView) findViewById(R.id.text1);
        TextView s2 = (TextView) findViewById(R.id.text2);
        String texts1 = "- Completed tasks are automatically deleted the day after they are marked completed";
        String texts2 = "- If a task has a recurring tag then it will not be deleted after completion and will be unchecked when the day is over";
        SpannableString ss1 = new SpannableString(texts1);
        SpannableString ss2 = new SpannableString(texts2);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        ss1.setSpan(boldSpan,2,17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan,17,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setText(ss1);
        s2.setText(ss2);

        //Changing display height and width
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.5));

    }
    public void CloseClick(View v){
        //if close finish activity
        if(v == CloseB){
            infoPop.this.finish();
        }
    }
}
