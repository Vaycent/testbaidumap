package vaycent.testbaidumap.widget;

import android.content.Context;
import android.content.Intent;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.EventBus.OnePoiMsgEvent;
import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.InDoorActivity;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.R;
import vaycent.testbaidumap.Utils.MapUtils;

/**
 * Created by vaycent on 2017/5/19.
 */

public class NavigationItemAdapter extends RecyclerView.Adapter<NavigationItemAdapter.MyViewHolder>
{
    private Context context;
    private List<PoiIndoorInfo> mPoiIndoorInfoDatas;
    private BDLocation resultCallBackLocation;

    private MapUtils mapUtilsHelper = new MapUtils();


    public NavigationItemAdapter(Context context, List<PoiIndoorInfo> mPoiIndoorInfoDatas , BDLocation resultCallBackLocation) {
        this.context = context;
        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
        this.resultCallBackLocation = resultCallBackLocation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.adapter_poiitem, parent,
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

    private String getDistanceText(int position){
        LatLng currentLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
        int distance = mapUtilsHelper.calculateDistance(currentLatLng,mPoiIndoorInfoDatas.get(position).latLng);
        return "离目的地距离"+distance+"米";
    }

    private void searchOnePoiResult(int position){
        List<PoiIndoorInfo> mListPoiIndoorInfo = new ArrayList<PoiIndoorInfo>();
        mListPoiIndoorInfo.add(mPoiIndoorInfoDatas.get(position));
        PoiIndoorResult mPoiIndoorResult = new PoiIndoorResult();
        mPoiIndoorResult.setPageNum(1);
        mPoiIndoorResult.setPoiNum(1);
        mPoiIndoorResult.setmArrayPoiInfo(mListPoiIndoorInfo);
//        activity.returnResultOnePoi(mPoiIndoorResult);
        EventBus.getDefault().post(new OnePoiMsgEvent(mPoiIndoorResult,resultCallBackLocation));
        backToInDoorActivity();
    }

    private void planRouteInDoor(int position){
        if(null!=resultCallBackLocation){
            LatLng startLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
            String startFloor = resultCallBackLocation.getFloor();
            LatLng endLatLng = mPoiIndoorInfoDatas.get(position).latLng;
            String endFloor = mPoiIndoorInfoDatas.get(position).floor;

            ResultRoutePlan mResultRoutePlan = new ResultRoutePlan(startLatLng,startFloor,endLatLng,endFloor);
//            activity.returnResultRoutePlan(mResultRoutePlan);

            EventBus.getDefault().post(new RoutePlanMsgEvent(mResultRoutePlan));
            backToInDoorActivity();
        }
    }

    private void backToInDoorActivity(){
        Intent indoorIntent = new Intent();
        indoorIntent.setClass(context, InDoorActivity.class);
        context.startActivity(indoorIntent);
    }
}
