package com.opticalix.dropdownmenu;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.opticalix.dropdown_lib.DropdownView;
import com.opticalix.dropdown_lib.LogUtils;

public class MainActivity extends AppCompatActivity {

    private DropdownView mDropdownView;
    private static final String[] titles_arr = {"title-1", "title-2", "title-3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_custom_view);

        RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "I'm a button at the bottom of the layers", Toast.LENGTH_SHORT).show();
            }
        });

        //prepare listViews
        final ListView simpleListView1 = DemoUtils.createSimpleListView(this, 10, 0);
        ListView simpleListView2 = DemoUtils.createSimpleListView(this, 5, 1);
        ListView simpleListView3 = DemoUtils.createSimpleListView(this, 10, 2);
        simpleListView1.setBackgroundResource(android.R.color.white);
        simpleListView2.setBackgroundResource(android.R.color.white);
        simpleListView3.setBackgroundResource(android.R.color.white);

        createFromXml(button, simpleListView1, simpleListView2);

//        createFromCode(root, simpleListView1, simpleListView2, simpleListView3);
    }

    private void createFromCode(RelativeLayout root, ListView simpleListView1, ListView simpleListView2, ListView simpleListView3) {
        DropdownView dropdownView = new DropdownView(this);
        dropdownView.setup(3, titles_arr, simpleListView1, simpleListView2, simpleListView3);
        root.addView(dropdownView);
    }

    private void createFromXml(final Button button, final ListView simpleListView1, ListView simpleListView2) {
        mDropdownView = (DropdownView) findViewById(R.id.dropdown_view);
        mDropdownView.setup(simpleListView1, simpleListView2);
        mDropdownView.setOnDropdownItemClickListener(new DropdownView.OnDropdownItemClickListener() {
            @Override
            public void onItemClick(View v, int whichList, int position) {
                mDropdownView.setTitleText(whichList, whichList + "-" + position);
            }
        });

        //enable functions you need. default value is true
        mDropdownView.enableDimBackground(true);
        mDropdownView.enableAutoBackOnItemClick(true);
        mDropdownView.setWeightRatio(1, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDropdownView.setZ(100);//larger than others
        }
    }
}
