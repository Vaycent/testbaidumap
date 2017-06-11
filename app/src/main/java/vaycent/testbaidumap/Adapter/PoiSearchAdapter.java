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

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.InDoorActivity;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.PoiInDoorSearchActivity;
import vaycent.testbaidumap.R;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;

/**
 * Created by vaycent on 2017/6/1.
 */

public class PoiSearchAdapter extends RecyclerView.Adapter<PoiSearchAdapter.MyViewHolder>
{
    private PoiInDoorSearchActivity mActivity;
    private List<PoiIndoorInfo> mPoiIndoorInfoDatas;
    private BDLocation resultCallBackLocation;
    private MapUtils mapUtilsHelper = new MapUtils();
    private int requestCode;


    public PoiSearchAdapter(PoiInDoorSearchActivity mActivity, List<PoiIndoorInfo> mPoiIndoorInfoDatas , BDLocation resultCallBackLocation,int requestCode) {
        this.mActivity = mActivity;
        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
        this.resultCallBackLocation = resultCallBackLocation;
        this.requestCode = requestCode;
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

        if(0==requestCode){
            holder.startLayout.setVisibility(View.VISIBLE);
        }else{
            holder.startLayout.setVisibility(View.GONE);
        }
        holder.startLayout.setOnClickListener(new NoMultiClickListener() {
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
        LinearLayout startLayout;

        public MyViewHolder(View view)
        {
            super(view);
            itemLayout = (ConstraintLayout)view.findViewById(R.id.adapter_poiitem_clyt_itemlayout);
            nameTv = (TextView) view.findViewById(R.id.adapter_poiitem_tv_placename);
            distanceTv = (TextView) view.findViewById(R.id.adapter_poiitem_tv_distance);
            floorTv = (TextView)view.findViewById(R.id.adapter_poiitem_tv_floor);
            startLayout = (LinearLayout) view.findViewById(R.id.adapter_poiitem_llyt_startnavigationlayout);
        }
    }

    private String getDistanceText(int position){
        LatLng currentLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
        int distance = mapUtilsHelper.calculateDistance(currentLatLng,mPoiIndoorInfoDatas.get(position).latLng);
        return "离目的地距离"+distance+"米";
    }

    private void searchOnePoiResult(int position){
        mActivity.returnSearchResult(mPoiIndoorInfoDatas.get(position));
    }


}
