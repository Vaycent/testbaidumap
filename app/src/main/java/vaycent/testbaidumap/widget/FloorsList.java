package vaycent.testbaidumap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import vaycent.testbaidumap.R;


/**
 * Created by vaycent on 2017/6/13.
 */

public class FloorsList extends LinearLayout {

    private Context mContext;
    private View mRootView;

    private ListView floorsListView;

    private int chooseFloor;

    public FloorsList(Context context) {
        super(context);
    }
    public FloorsList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ListView getFloorsListView(){
        return this.floorsListView;
    }

    private void initLayout() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.widget_floors_list_layout, this);
        floorsListView = (ListView) mRootView.findViewById(R.id.widget_floors_lv_ist);
    }


}
