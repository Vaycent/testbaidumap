package vaycent.testbaidumap.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vaycent.testbaidumap.InDoor.StripItem;
import vaycent.testbaidumap.R;

/**
 * Created by vaycent on 2017/6/13.
 */

public class FloorsListAdapter extends BaseAdapter {

    private ArrayList<String> floors;
    private Context context;
    private LayoutInflater mInflater;
    private int selectedPos;

    public FloorsListAdapter(Context context, ArrayList<String> floors){
        this.floors = floors;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return floors.size();
    }

    @Override
    public Object getItem(int position) {
        return floors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final NoteViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_adapter_floor_layout,null);

            holder = new NoteViewHolder();

//            convertView = new StripItem(context);
//            holder.mFloorTextTV = ((StripItem) convertView).getmText();
            holder.floorItem = (RelativeLayout)convertView.findViewById(R.id.item_adapter_floor_item);
            holder.mFloorTextTV = (TextView) convertView.findViewById(R.id.item_adapter_floor_floor);
            convertView.setTag(holder);
        } else {
            holder = (NoteViewHolder) convertView.getTag();
        }




        String floor = floors.get(position);
        if (floor != null) {
            holder.mFloorTextTV.setText(floor);
        }

        if (selectedPos == position) {
            refreshViewStyle(holder.mFloorTextTV, true);
        } else {
            refreshViewStyle(holder.mFloorTextTV, false);
        }
        return convertView;
    }

    private class NoteViewHolder {
        private TextView mFloorTextTV;
        private RelativeLayout floorItem;
    }


    private void refreshViewStyle(TextView view, boolean isSelected) {

        view.setTextColor(Color.BLACK);

        if (isSelected) {
            view.setBackgroundColor(StripItem.colorSelected);
            view.setTextColor(Color.WHITE);
        } else {
            view.setBackgroundColor(StripItem.color);
            view.setTextColor(Color.BLACK);
        }
        view.setSelected(isSelected);
    }

    public void setSelectedPostion(int postion) {
        selectedPos = postion;
    }
}
