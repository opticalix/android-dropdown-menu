# android-dropdown-menu
An dropdown menu for android, you can attach your own listView(s) with this.

# 文档
[DropdownMenu][1]

# Demo
![DropdownMenu demo][2]

# Usage
You can use this to make several(as you want) listViews act like dropdown menu, the 'weight' of titles' width is adjustable. It will not intercept touch events when it get back.

Use this view in xml:

    <com.opticalix.dropdown_lib.DropdownView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/dropdown_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:drop_down_arrow_up="@mipmap/ic_arrow_up"
        app:drop_down_duration="200"
        app:drop_down_enable_dim="true"
        app:drop_down_list_count="2"
        app:drop_down_list_height="150dp"
        app:drop_down_title_background="@android:color/white"
        app:drop_down_title_drawable_padding="10dp"
        app:drop_down_title_padding_bottom="7dp"
        app:drop_down_title_padding_top="7dp"
        app:drop_down_title_text="@array/drop_down_titles"
        app:drop_down_title_text_color="#444444"
        app:drop_down_title_text_size="10sp"/>

Then find this view and setup():

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

Or you can just new DropdownView in code:

        DropdownView dropdownView = new DropdownView(this);
        dropdownView.setup(3, titles_arr, simpleListView1, simpleListView2, simpleListView3);
        root.addView(dropdownView);

Remember to provide listViews, and register onDropdownItemClickListener.

# Include
You can use gradle to grab it from maven central:

    compile 'com.opticalix.dropdownmenu:dropdown-lib:1.0.1'

# Config
You may need to modify part of the DropdownView, methods like 'setTitleBackgroundColor' are available.

- optional
    - titleTextColor
    - titleTextSize
    - titleBackground
    - titlePadding
    - arrowDrawable
    - arrowDrawablePadding
    - listViewHeight
    - duration
	- enableDimBackground
    - enableAutoBackOnItemClick

- necessary
    - listCount
    - listViews
    - titleText


# Notice
If you find dropdownView is cover by other views in LOLLIPOP, you can:

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDropdownView.setZ(100);//larger than others
        }


  [1]: https://opticalix.github.io/2016/01/09/dropdown-menu/
  [2]: http://7xp2qc.com1.z0.glb.clouddn.com/dropdown_demo.gif