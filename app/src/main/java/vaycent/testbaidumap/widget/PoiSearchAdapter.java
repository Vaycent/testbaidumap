package vaycent.testbaidumap.widget;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;

import java.util.List;

import vaycent.testbaidumap.PoiInDoorSearchActivity;
import vaycent.testbaidumap.R;
import vaycent.testbaidumap.Utils.MapUtils;

/**
 * Created by vaycent on 2017/6/1.
 */

public class PoiSearchAdapter extends RecyclerView.Adapter<PoiSearchAdapter.MyViewHolder>
{
    private PoiInDoorSearchActivity mActivity;
    private List<PoiIndoorInfo> mPoiIndoorInfoDatas;
    private BDLocation resultCallBackLocation;

    private MapUtils mapUtilsHelper = new MapUtils();


    public PoiSearchAdapter(PoiInDoorSearchActivity mActivity, List<PoiIndoorInfo> mPoiIndoorInfoDatas , BDLocation resultCallBackLocation) {
        this.mActivity = mActivity;
        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
        this.resultCallBackLocation = resultCallBackLocation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.adapter_poiitem, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.nameTv.setText(mPoiIndoorInfoDatas.get(position).name);

        holder.distanceTv.setText(getDistanceText(position));

        holder.floorTv.setText(mPoiIndoorInfoDatas.get(position).floor);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnePoiResult(position);
            }
        });

        holder.startIconIv.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount()
    {
        return mPoiIndoorInfoDatas.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView nameTv,distanceTv,floorTv;
        Button startIconIv;
        ConstraintLayout itemLayout;

        public MyViewHolder(View view)
        {
            super(view);
            itemLayout = (ConstraintLayout)view.findViewById(R.id.adapter_poiitem_clyt_itemlayout);
            nameTv = (TextView) view.findViewById(R.id.adapter_poiitem_tv_placename);
            distanceTv = (TextView) view.findViewById(R.id.adapter_poiitem_tv_distance);
            floorTv = (TextView)view.findViewById(R.id.adapter_poiitem_tv_floor);
            startIconIv = (Button) view.findViewById(R.id.adapter_poiitem_iv_startnavigation);
        }
    }

    private String getDistanceText(int position){
        LatLng currentLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
        int distance = mapUtilsHelper.calculateDistance(currentLatLng,mPoiIndoorInfoDatas.get(position).latLng);
        return "离目的地距离"+distance+"米";
    }

    private void searchOnePoiResult(int position){
        mActivity.returnPoiIndoorInfo(mPoiIndoorInfoDatas.get(position));
    }


}
