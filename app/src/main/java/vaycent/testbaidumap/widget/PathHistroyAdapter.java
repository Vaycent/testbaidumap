package vaycent.testbaidumap.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vaycent.testbaidumap.R;

/**
 * Created by vaycent on 2017/6/2.
 */

public class PathHistroyAdapter extends RecyclerView.Adapter<PathHistroyAdapter.MyViewHolder>
{
    private Context context;
    private List<String> mKeyWords;
//    private BDLocation resultCallBackLocation;

//    private MapUtils mapUtilsHelper = new MapUtils();


    public PathHistroyAdapter(Context context, List<String> mKeyWords) {
        this.context = context;
        this.mKeyWords = mKeyWords;
//        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
//        this.resultCallBackLocation = resultCallBackLocation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.adapter_searchhistroy_item, parent,
                false));

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.nameTv.setText(mKeyWords.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mKeyWords.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView nameTv;
        public MyViewHolder(View view) {
            super(view);
            nameTv = (TextView) view.findViewById(R.id.adapter_searchhistroy_tv_placename);
        }
    }


}
