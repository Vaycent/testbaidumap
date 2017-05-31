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
import com.baidu.mapapi.search.poi.PoiIndoorResult;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.NavigationMapActivity;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.R;

/**
 * Created by vaycent on 2017/5/19.
 */

public class PoiItemAdapter extends RecyclerView.Adapter<PoiItemAdapter.MyViewHolder>
{
    private NavigationMapActivity activity;
    private List<PoiIndoorInfo> mPoiIndoorInfoDatas;
    private BDLocation resultCallBackLocation;

    public PoiItemAdapter(NavigationMapActivity activity, List<PoiIndoorInfo> mPoiIndoorInfoDatas ,BDLocation resultCallBackLocation) {
        this.activity = activity;
        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
        this.resultCallBackLocation = resultCallBackLocation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                activity).inflate(R.layout.adapter_poiitem, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.nameTv.setText(mPoiIndoorInfoDatas.get(position).name);
        holder.distanceTv.setText(mPoiIndoorInfoDatas.get(position).address);
        holder.floorTv.setText(mPoiIndoorInfoDatas.get(position).floor);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnePoiResult(position);
            }
        });
        holder.startIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planRouteInDoor(position);
            }
        });
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


    private void searchOnePoiResult(int position){
        List<PoiIndoorInfo> mListPoiIndoorInfo = new ArrayList<PoiIndoorInfo>();
        mListPoiIndoorInfo.add(mPoiIndoorInfoDatas.get(position));
        PoiIndoorResult mPoiIndoorResult = new PoiIndoorResult();
        mPoiIndoorResult.setPageNum(1);
        mPoiIndoorResult.setPoiNum(1);
        mPoiIndoorResult.setmArrayPoiInfo(mListPoiIndoorInfo);
        activity.returnResultOnePoi(mPoiIndoorResult);
    }

    private void planRouteInDoor(int position){
        if(null!=resultCallBackLocation){
            LatLng startLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
            String startFloor = resultCallBackLocation.getFloor();
            LatLng endLatLng = mPoiIndoorInfoDatas.get(position).latLng;
            String endFloor = mPoiIndoorInfoDatas.get(position).floor;

            ResultRoutePlan mResultRoutePlan = new ResultRoutePlan(startLatLng,startFloor,endLatLng,endFloor);
            activity.returnResultRoutePlan(mResultRoutePlan);
        }
    }
}
