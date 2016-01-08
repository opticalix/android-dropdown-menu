package com.opticalix.dropdown_lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Make easier to use dropdown menu.
 * Created by opticalix@gmail.com on 16/1/4.
 */
public class DropdownView extends RelativeLayout {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static final String TEXT_VIEW_TAG_PREFIX = "textView";
    private static final String LIST_VIEW_TAG_PREFIX = "listView";

    private Drawable mArrowDownDrawable;
    private Drawable mArrowUpDrawable;
    private CharSequence[] mTitles;
    private int mDropdownListCount;
    private int mDrawablePadding;
    private int mAnchor;
    private LinearLayout mLinearContainer;

    private ListView[] mListViews;//all listViews
    private int mCheckedIndex = -1;//which listView is shown now
    private Animation mBackgroundShowAnimation;
    private Animation mBackgroundDismissAnimation;
    private int mDuration;
    private boolean mMoving;//indicate animation running or not
    private FrameLayout[] mTitleFrameLayouts;
    private int[] mTitleLinearLayoutPadding;//left top bottom right
    private ScrollerChecker mScrollerChecker;
    private int mListViewHeight;
    private FrameLayout mCovering;
    private boolean mEnableDim = true;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private Drawable mTitleBackground;
    private boolean mEnableAutoBackOnItemClick = true;

    public DropdownView(Context context) {
        this(context, null);
    }

    public DropdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(context, attrs, defStyleAttr);

    }

    public void setup(int dropdownListCount, int titlesArrayRes, ListView... listViews){
        mDropdownListCount = dropdownListCount;
        resetTitles(getResources().getStringArray(titlesArrayRes));

        initAnchor();
        attachListView(listViews);
    }

    public void setup(int dropdownListCount, CharSequence[] titles, ListView... listViews){
        mDropdownListCount = dropdownListCount;
        resetTitles(titles);

        initAnchor();
        attachListView(listViews);
    }

    public void setup(ListView... listViews){
        initAnchor();
        attachListView(listViews);
    }

    private void resetTitles(CharSequence[] stringArray){
        mTitles = stringArray;
    }

    private void initParams(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropdownView, defStyleAttr, 0);
        mDropdownListCount = a.getInteger(R.styleable.DropdownView_drop_down_list_count, 1);
        mTitles = a.getTextArray(R.styleable.DropdownView_drop_down_title_text);//
        mTitleTextSize = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_text_size, 30);
        mTitleTextColor = a.getColor(R.styleable.DropdownView_drop_down_title_text_color, 0xff444444);//colorStateList?
        mTitleBackground = a.getDrawable(R.styleable.DropdownView_drop_down_title_background);

        mArrowUpDrawable = a.getDrawable(R.styleable.DropdownView_drop_down_arrow_up);
        mArrowDownDrawable = a.getDrawable(R.styleable.DropdownView_drop_down_arrow_down);
        mDuration = a.getInteger(R.styleable.DropdownView_drop_down_duration, 500);
        mListViewHeight = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_list_height, 500);
        mDrawablePadding = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_drawable_padding, 10);

        mTitleLinearLayoutPadding = new int[4];
        int padding = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_padding, 0);
        for (int i = 0; i < 4; i++) {
            mTitleLinearLayoutPadding[i] = padding;
        }
        mTitleLinearLayoutPadding[0] = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_padding_left, 0);
        mTitleLinearLayoutPadding[1] = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_padding_top, 0);
        mTitleLinearLayoutPadding[2] = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_padding_right, 0);
        mTitleLinearLayoutPadding[3] = a.getDimensionPixelSize(R.styleable.DropdownView_drop_down_title_padding_bottom, 0);
        mEnableDim = a.getBoolean(R.styleable.DropdownView_drop_down_enable_dim, true);

        a.recycle();

        if (mArrowDownDrawable == null) {
            mArrowDownDrawable = getResources().getDrawable(R.mipmap.ic_arrow_down);
        }
        if (mArrowDownDrawable != null) {
            mArrowDownDrawable.setBounds(0, 0, mArrowDownDrawable.getMinimumWidth(), mArrowDownDrawable.getMinimumHeight());
        }
        if (mArrowUpDrawable == null) {
            mArrowUpDrawable = getResources().getDrawable(R.mipmap.ic_arrow_up);
        }
        if (mArrowUpDrawable != null) {
            mArrowUpDrawable.setBounds(0, 0, mArrowUpDrawable.getMinimumWidth(), mArrowUpDrawable.getMinimumHeight());
        }
        for (int i = 0; i < mDropdownListCount; i++) {
            if (mTitles != null && TextUtils.isEmpty(mTitles[i])) {
                mTitles[i] = "Title-" + i;//for test
            }
        }
        if(mTitleBackground == null){
            mTitleBackground = new ColorDrawable(getResources().getColor(android.R.color.white));
        }

        //set animation
        mBackgroundShowAnimation = new AlphaAnimation(0.0f, 0.7f);
        mBackgroundDismissAnimation = new AlphaAnimation(0.7f, 0.0f);
        mBackgroundShowAnimation.setFillAfter(true);
        mBackgroundShowAnimation.setDuration(mDuration);
        mBackgroundDismissAnimation.setDuration(mDuration);
    }

    private void initAnchor() {
        mTitleFrameLayouts = new FrameLayout[mDropdownListCount];
        //prepare inside titles
        for (int i = 0; i < mDropdownListCount; i++) {
            FrameLayout frameLayout = new FrameLayout(getContext());

            TextView title = new TextView(getContext());
            title.setTag(generateUniqueTag(TEXT_VIEW_TAG_PREFIX, i));
            title.setCompoundDrawables(null, null, mArrowDownDrawable, null);
            title.setCompoundDrawablePadding(mDrawablePadding);
            FrameLayout.LayoutParams frameLps = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLps.gravity = Gravity.CENTER;
            title.setLayoutParams(frameLps);
            title.setTextSize(mTitleTextSize);
            title.setTextColor(mTitleTextColor);
            title.setText(mTitles[i]);

            frameLayout.addView(title);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.weight = 1;
            lps.gravity = Gravity.CENTER_VERTICAL | Gravity.TOP;
            frameLayout.setLayoutParams(lps);

            frameLayout.setTag(i);
            frameLayout.setOnClickListener(mOnTitleClickListener);
            mTitleFrameLayouts[i] = frameLayout;
        }

        //add anchorView
        mTitleFrameLayouts[0].measure(0, 0);
        LinearLayout anchorView = new LinearLayout(getContext());
        anchorView.setBackgroundResource(android.R.color.transparent);
        if (mAnchor == 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                anchorView.setId(mAnchor = myGenerateViewId());
            } else {
                anchorView.setId((mAnchor = generateViewId()));
            }
            LogUtils.d("generate id=" + mAnchor);
        }
        LogUtils.d(this, "mTitleFrameLayouts[0].getMeasuredHeight()=" + mTitleFrameLayouts[0].getMeasuredHeight());
        addView(anchorView, generateRelativeLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTitleFrameLayouts[0].getMeasuredHeight() + mTitleLinearLayoutPadding[1] + mTitleLinearLayoutPadding[3]));

        initCovering();

    }

    private void initCovering() {
        //add cover
        mCovering = new FrameLayout(getContext());
        mCovering.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCovering.setBackgroundResource(android.R.color.black);
        mCovering.setClickable(true);
        mCovering.setVisibility(GONE);
        mCovering.setOnClickListener(mOnCoverClickListener);
        addView(mCovering);
    }


    private void innerCreateViews() {
        //change background
        setBackgroundResource(android.R.color.transparent);//force set background transparent

        //add linearLayout first
        mLinearContainer = new LinearLayout(getContext());
        mLinearContainer.setBackgroundDrawable(mTitleBackground);//
        mLinearContainer.setPadding(mTitleLinearLayoutPadding[0], mTitleLinearLayoutPadding[1], mTitleLinearLayoutPadding[2], mTitleLinearLayoutPadding[3]);
        mLinearContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLinearContainer, generateRelativeLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //fill title
        for (int i = 0; i < mDropdownListCount; i++) {
            mLinearContainer.addView(mTitleFrameLayouts[i]);
        }

    }


    OnClickListener mOnCoverClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtils.d("covering clicked" + v.getVisibility());
            //if moving, ignore this click
            if (mMoving) {
                return;
            }
            if (mCheckedIndex != -1) {
                toggleTitleDrawable(mCheckedIndex);
                tryToggleListViewByTranslation(mCheckedIndex);
                mCheckedIndex = -1;
            }
        }
    };

    private OnClickListener mOnTitleClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof FrameLayout) {
                int vTag = (int) v.getTag();
                LogUtils.d("tag=" + vTag);
                onTitleClick(v, vTag);
            }

        }
    };

    private void onTitleClick(View v, int which) {
        //if moving, ignore this click
        if (mMoving) {
            return;
        }
        if (mCheckedIndex != -1 && mCheckedIndex != which) {
            //click another title
            toggleTitleDrawable(mCheckedIndex);//restore last checked one
            toggleListViewVisibility(mCheckedIndex);
        }
        mCheckedIndex = mCheckedIndex == which ? -1 : which;
        toggleTitleDrawable(which);
        tryToggleListViewByTranslation(which);
    }

    private void translateListViewInY(int which, int y) {
        mListViews[which].setTranslationY(y);
//        mListViews[which].invalidate();
    }

    private void tryToggleListViewByTranslation(int which) {
        //scroll? offset? animation? propertyAnimation? translation?
        int height = mListViewHeight;

        if (mListViews[which].getVisibility() == GONE) {
            //move down, need to be visible
            toggleListViewVisibility(which);
            mScrollerChecker.setWhich(which).setHeight(height).setDirection(ScrollerChecker.MOVE_DOWN).startScroll();

            if (mEnableDim && mCovering != null) {
                mCovering.setVisibility(VISIBLE);
                mCovering.startAnimation(mBackgroundShowAnimation);
            }
        } else {
            mScrollerChecker.setWhich(which).setHeight(height).setDirection(ScrollerChecker.MOVE_UP).startScroll();

            if (mEnableDim && mCovering != null) {
                mCovering.setVisibility(VISIBLE);
                mCovering.startAnimation(mBackgroundDismissAnimation);
            }
        }
    }

    private void toggleListViewVisibility(int which) {
        mListViews[which].setVisibility(mListViews[which].getVisibility() == VISIBLE ? GONE : VISIBLE);
    }

    private void toggleTitleDrawable(int which) {
        View view = findViewWithTag(generateUniqueTag(TEXT_VIEW_TAG_PREFIX, which));
        if (view != null && view instanceof TextView) {
            TextView viewWithTag = (TextView) view;
            viewWithTag.setCompoundDrawables(null, null, viewWithTag.getCompoundDrawables()[2] == mArrowDownDrawable ? mArrowUpDrawable : mArrowDownDrawable, null);
        }
    }

    private String generateUniqueTag(String prefix, int index) {
        return prefix + "-" + index;
    }

    /**
     * Attach your listViews with this dropdownView. Those listViews will be shown as dropdown menu when clicking titles.
     *
     * @param listViews
     */
    private void attachListView(ListView... listViews) {
        String tagPrefix = LIST_VIEW_TAG_PREFIX;
        if (listViews == null || listViews.length < mDropdownListCount)
            throw new RuntimeException("should offer enough listViews");

        mListViews = listViews;
        for (int i = 0; i < mDropdownListCount; i++) {
            LayoutParams params;
            ListView listView;
            listView = listViews[i];
            listView.setTag(generateUniqueTag(tagPrefix, i));
            addView(listView);
            if (listView.getBackground() == null) {
                LogUtils.w(this, "attachListView: ListView should have a background.");
            }
            params = (LayoutParams) listView.getLayoutParams();
            if (params == null) {
                params = generateRelativeLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mListViewHeight);
                listView.setLayoutParams(params);
            }
            listView.getLayoutParams().height = mListViewHeight;
            ((LayoutParams) listView.getLayoutParams()).addRule(RelativeLayout.BELOW, mAnchor);

            //put it to upper position
            listView.setTranslationY(-mListViewHeight);
            listView.setVisibility(GONE);

            //add listener
            final int finalIndex = i;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mOnDropdownItemClickListener != null) {
                        mOnDropdownItemClickListener.onItemClick(view, finalIndex, position);
                    }

                    if (mEnableAutoBackOnItemClick) {
                        //if moving, ignore this click
                        if (mMoving) {
                            return;
                        }
                        mCheckedIndex = -1;
                        toggleTitleDrawable(finalIndex);
                        tryToggleListViewByTranslation(finalIndex);
                    }
                }
            });
        }

        mScrollerChecker = new ScrollerChecker();
        innerCreateViews();
    }

    private void reattachListViews() {
        String tagPrefix = "listView";

        if (mListViews.length == 0 || (findViewWithTag(generateUniqueTag(tagPrefix, 0)) == null)) {
            LogUtils.w(this, "no listViews added");
            return;
        }

        for (int i = 0; i < mDropdownListCount; i++) {
            LayoutParams params;
            ListView listView;
            listView = (ListView) findViewWithTag(generateUniqueTag(tagPrefix, i));

            params = (LayoutParams) listView.getLayoutParams();
            if (params == null) {
                params = generateRelativeLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mListViewHeight);
                listView.setLayoutParams(params);
            }
            listView.getLayoutParams().height = mListViewHeight;
        }

    }

    /**
     * Enable dim background, click dim background will scroll back listView
     *
     * @param b
     */
    public void enableDimBackground(boolean b) {
        mEnableDim = b;
    }

    public void enableAutoBackOnItemClick(boolean b) {
        mEnableAutoBackOnItemClick = b;
    }

    private LayoutParams generateRelativeLayoutParams(int w, int h) {
        return new LayoutParams(w, h);
    }

    /**
     * Re-allocate how large the width of each title is.
     *
     * @param weight
     */
    public void setWeightRatio(int... weight) {
        if (weight == null || weight.length != mDropdownListCount)
            throw new RuntimeException("not match DropdownListCount");

        for (int i = 0; i < mDropdownListCount; i++) {
            View viewWithTag = findViewWithTag(i);
            if (viewWithTag != null && viewWithTag instanceof FrameLayout) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewWithTag.getLayoutParams();
                layoutParams.weight = weight[i];
            }
        }
        requestLayout();
        invalidate();
    }

    /**
     * Set specified title text
     *
     * @param which
     * @param title
     */
    public void setTitleText(int which, CharSequence title) {
        View viewWithTag = findViewWithTag(generateUniqueTag(TEXT_VIEW_TAG_PREFIX, which));
        if (viewWithTag != null && viewWithTag instanceof TextView) {
            ((TextView) viewWithTag).setText(title);
        }
    }

    /**
     * Set specified title color
     *
     * @param which
     * @param color
     */
    public void setTitleTextColor(int which, int color) {
        View viewWithTag = findViewWithTag(generateUniqueTag(TEXT_VIEW_TAG_PREFIX, which));
        if (viewWithTag != null && viewWithTag instanceof TextView) {
            ((TextView) viewWithTag).setTextColor(color);
        }
    }

    /**
     * Set specified title size. See {@link TypedValue} for the possible dimension units.
     *
     * @param which Which one you want to modify.
     * @param unit  The desired dimension unit.
     * @param size  The desired size in the given units.
     */
    public void setTitleTextSize(int which, int unit, int size) {
        View viewWithTag = findViewWithTag(generateUniqueTag(TEXT_VIEW_TAG_PREFIX, which));
        if (viewWithTag != null && viewWithTag instanceof TextView) {
            ((TextView) viewWithTag).setTextSize(unit, size);
        }
    }

    /**
     * Set title background with color provided.
     *
     * @param color
     */
    public void setTitleBackgroundColor(int color) {
        mLinearContainer.setBackgroundColor(color);
    }

    /**
     * Set title background with resource provided.
     *
     * @param res
     */
    public void setTitleBackgroundResource(int res) {
        mLinearContainer.setBackgroundResource(res);
    }

    /**
     * Set title arrow-up drawable with resource provided. Will replace original one.
     *
     * @param res
     */
    public void setArrowUpDrawable(int res) {
        mArrowUpDrawable = getResources().getDrawable(res);
        if (mArrowUpDrawable != null) {
            mArrowUpDrawable.setBounds(0, 0, mArrowUpDrawable.getMinimumWidth(), mArrowUpDrawable.getMinimumHeight());
        }
    }

    /**
     * Set title arrow-down drawable with resource provided. Will replace original one.
     *
     * @param res
     */
    public void setArrowDownDrawable(int res) {
        mArrowDownDrawable = getResources().getDrawable(res);
        if (mArrowDownDrawable != null) {
            mArrowDownDrawable.setBounds(0, 0, mArrowUpDrawable.getMinimumWidth(), mArrowUpDrawable.getMinimumHeight());
        }
    }

    /**
     * Set listView's height. See {@link TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    public void setListViewHeight(int unit, int size) {
        String tagPrefix = LIST_VIEW_TAG_PREFIX;
        mListViewHeight = Math.round(TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
        if (mListViews.length > 0 && (findViewWithTag(generateUniqueTag(tagPrefix, 0)) != null)) {
            reattachListViews();
        }
    }

    /**
     * Set duration of the translation animation.
     * @param duration Duration in milliseconds
     */
    public void setDuration(int duration){
        mDuration = duration;
        if(mBackgroundShowAnimation != null && mBackgroundDismissAnimation != null){
            mBackgroundShowAnimation.setDuration(mDuration);
            mBackgroundDismissAnimation.setDuration(mDuration);
        }
    }

    /**
     * Remove all listViews added.
     */
    public void removeAllListViews(){
        for(int i=0; i<mDropdownListCount; i++){
            removeView(mListViews[i]);
        }
    }

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int myGenerateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    class ScrollerChecker implements Runnable {
        private int mHeight;
        public static final int MOVE_UP = 1;
        public static final int MOVE_DOWN = 2;
        private int mDirection;
        Scroller mScroller;
        private int mWhich;

        public ScrollerChecker() {
            mScroller = new Scroller(getContext());
        }

        public ScrollerChecker setHeight(int height) {
            mHeight = height;
            return this;
        }

        public ScrollerChecker setDirection(int direction) {
            mDirection = direction;
            return this;
        }

        public ScrollerChecker setWhich(int which) {
            mWhich = which;
            return this;
        }

        public void startScroll() {
            mScroller.forceFinished(true);
            removeCallbacks(this);
            if (mDirection == MOVE_DOWN) {
                mScroller.startScroll(0, -mHeight, 0, mHeight, mDuration);
            } else if (mDirection == MOVE_UP) {
                mScroller.startScroll(0, 0, 0, -mHeight, mDuration);
            }
            mMoving = true;
            post(this);
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int currY = mScroller.getCurrY();
                translateListViewInY(mWhich, currY);
                post(this);
            } else {
                removeCallbacks(this);
                if (mDirection == MOVE_UP) {
                    //move up, need gone
                    toggleListViewVisibility(mWhich);

                    if (mEnableDim) {
                        mCovering.clearAnimation();
                        mCovering.setVisibility(GONE);//http://stackoverflow.com/questions/4728908/android-view-with-view-gone-still-receives-ontouch-and-onclick
                    }
                } else {

                }
                mMoving = false;
            }
        }
    }

    private OnDropdownItemClickListener mOnDropdownItemClickListener;

    /**
     * Register a callback to be invoked when the item of specified listView is clicked.
     *
     * @param onDropdownItemClickListener The callback that will run
     */
    public void setOnDropdownItemClickListener(OnDropdownItemClickListener onDropdownItemClickListener) {
        mOnDropdownItemClickListener = onDropdownItemClickListener;
    }

    public interface OnDropdownItemClickListener {
        void onItemClick(View v, int whichList, int position);
    }

}
