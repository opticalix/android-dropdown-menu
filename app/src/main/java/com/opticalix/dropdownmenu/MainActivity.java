package com.opticalix.dropdownmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.opticalix.dropdown_lib.DropdownView;
import com.opticalix.dropdown_lib.LogUtils;

public class MainActivity extends AppCompatActivity {

    private DropdownView mDropdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_custom_view);

        mDropdownView = (DropdownView) findViewById(R.id.dropdown_view);

        //prepare listViews
        ListView simpleListView1 = DemoUtils.createSimpleListView(this, 10, 0);
        ListView simpleListView2 = DemoUtils.createSimpleListView(this, 5, 1);
        simpleListView1.setBackgroundResource(android.R.color.white);
        simpleListView2.setBackgroundResource(android.R.color.white);

        mDropdownView.attachListView(simpleListView1, simpleListView2);
        mDropdownView.setOnDropdownItemClickListener(new DropdownView.OnDropdownItemClickListener() {
            @Override
            public void onItemClick(View v, int whichList, int position) {
                //you can change title text/textColor/background... when select one item
                mDropdownView.setTitleText(whichList, whichList + "-" + position);
            }
        });

        //enable functions you need. default value is true
        mDropdownView.enableDimBackground(true);
        mDropdownView.enableAutoBackOnItemClick(true);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "I'm a button at the bottom of the layers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
