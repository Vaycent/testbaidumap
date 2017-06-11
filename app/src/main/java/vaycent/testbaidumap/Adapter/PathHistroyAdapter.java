package vaycent.testbaidumap.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vaycent.testbaidumap.PoiInDoorSearchActivity;
import vaycent.testbaidumap.R;
import vaycent.testbaidumap.Utils.NoMultiClickListener;

/**
 * Created by vaycent on 2017/6/2.
 */

public class PathHistroyAdapter extends RecyclerView.Adapter<PathHistroyAdapter.MyViewHolder>
{
    private PoiInDoorSearchActivity mActivity;
    private List<String> mKeyWords;

    public PathHistroyAdapter(PoiInDoorSearchActivity mActivity, List<String> mKeyWords) {
        this.mActivity = mActivity;
        this.mKeyWords = mKeyWords;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.adapter_searchhistroy_item, parent,
                false));

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.nameTv.setText(mKeyWords.get(position));
        holder.itemLayout.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                mActivity.refreshSearchText(mKeyWords.get(position));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mKeyWords.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView nameTv;
        LinearLayout itemLayout;
        public MyViewHolder(View view) {
            super(view);
            nameTv = (TextView) view.findViewById(R.id.adapter_searchhistroy_tv_placename);
            itemLayout = (LinearLayout)view.findViewById(R.id.adapter_searchhistroy_itemlayout);
        }
    }


}
