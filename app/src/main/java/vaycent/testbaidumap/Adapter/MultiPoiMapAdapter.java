package vaycent.testbaidumap.Adapter;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.EventBus.OnePoiMsgEvent;
import vaycent.testbaidumap.EventBus.PathPlanResultMsgEvent;
import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.InDoorActivity;
import vaycent.testbaidumap.MultiPoiMapActivity;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.PathPlanActivity;
import vaycent.testbaidumap.R;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;

/**
 * Created by vaycent on 2017/6/8.
 */

public class MultiPoiMapAdapter extends RecyclerView.Adapter<MultiPoiMapAdapter.MyViewHolder>
{
    private MultiPoiMapActivity mActivity;
    private List<PoiIndoorInfo> mPoiIndoorInfoDatas;
    private BDLocation resultCallBackLocation;
    private int requestCodeFrom;

    private MapUtils mapUtilsHelper = new MapUtils();


    public MultiPoiMapAdapter(MultiPoiMapActivity mActivity, List<PoiIndoorInfo> mPoiIndoorInfoDatas , BDLocation resultCallBackLocation,int requestCodeFrom) {
        this.mActivity = mActivity;
        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
        this.resultCallBackLocation = resultCallBackLocation;
        this.requestCodeFrom = requestCodeFrom;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.adapter_multi_poi_item, parent,
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
                if(0==requestCodeFrom){
                    List<PoiIndoorInfo> mListPoiIndoorInfo = new ArrayList<PoiIndoorInfo>();
                    mListPoiIndoorInfo.add(mPoiIndoorInfoDatas.get(position));
                    PoiIndoorResult mPoiIndoorResult = new PoiIndoorResult();
                    mPoiIndoorResult.setPageNum(1);
                    mPoiIndoorResult.setPoiNum(1);
                    mPoiIndoorResult.setmArrayPoiInfo(mListPoiIndoorInfo);
                    EventBus.getDefault().post(new OnePoiMsgEvent(mPoiIndoorResult,resultCallBackLocation));
                    Intent indoorIntent = new Intent();
                    indoorIntent.setClass(mActivity,InDoorActivity.class);
                    mActivity.startActivity(indoorIntent);
                }else{
                    EventBus.getDefault().post(new PathPlanResultMsgEvent(mPoiIndoorInfoDatas.get(position),requestCodeFrom));
                    Intent intent = new Intent();
                    intent.setClass(mActivity, PathPlanActivity.class);
                    mActivity.startActivity(intent);
                }
            }
        });
        if(requestCodeFrom>0)
            holder.startNavigation.setVisibility(View.GONE);
        else
            holder.startNavigation.setVisibility(View.VISIBLE);
        holder.startNavigation.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                if(null!=resultCallBackLocation){
                    LatLng startLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
                    String startFloor = resultCallBackLocation.getFloor();
                    LatLng endLatLng = mPoiIndoorInfoDatas.get(position).latLng;
                    String endFloor = mPoiIndoorInfoDatas.get(position).floor;
                    ResultRoutePlan mResultRoutePlan = new ResultRoutePlan(startLatLng,startFloor,endLatLng,endFloor);
                    EventBus.getDefault().post(new RoutePlanMsgEvent(mResultRoutePlan));
                    Intent indoorIntent = new Intent();
                    indoorIntent.setClass(mActivity, InDoorActivity.class);
                    mActivity.startActivity(indoorIntent);
                }

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
        ConstraintLayout itemLayout;
        LinearLayout startNavigation;

        public MyViewHolder(View view)
        {
            super(view);
            itemLayout = (ConstraintLayout)view.findViewById(R.id.adapter_poiitem_clyt_itemlayout);
            nameTv = (TextView) view.findViewById(R.id.adapter_poiitem_tv_placename);
            distanceTv = (TextView) view.findViewById(R.id.adapter_poiitem_tv_distance);
            floorTv = (TextView)view.findViewById(R.id.adapter_poiitem_tv_floor);
            startNavigation = (LinearLayout)view.findViewById(R.id.adapter_multi_poi_llyt_startnavigationlayout);
        }
    }

    private String getDistanceText(int position){
        LatLng currentLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
        int distance = mapUtilsHelper.calculateDistance(currentLatLng,mPoiIndoorInfoDatas.get(position).latLng);
        return "离目的地距离"+distance+"米";
    }
}

