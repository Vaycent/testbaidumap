package vaycent.testbaidumap.widget;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.route.IndoorPlanNode;
import com.baidu.mapapi.search.route.IndoorRoutePlanOption;
import com.baidu.mapapi.search.route.RoutePlanSearch;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.InDoorActivity;
import vaycent.testbaidumap.Location.LocationManager;
import vaycent.testbaidumap.MyMapHelper;
import vaycent.testbaidumap.R;

/**
 * Created by vaycent on 2017/5/19.
 */

public class PoiItemAdapter extends RecyclerView.Adapter<PoiItemAdapter.MyViewHolder>
{
    private InDoorActivity activity;
    private List<PoiIndoorInfo> mPoiIndoorInfoDatas;
    private RoutePlanSearch mRoutePlanSearch;
    private MapView mMapView;

    public PoiItemAdapter(InDoorActivity activity, MapView mMapView, List<PoiIndoorInfo> mPoiIndoorInfoDatas, RoutePlanSearch mRoutePlanSearch) {
        this.activity = activity;
        this.mMapView = mMapView;
        this.mPoiIndoorInfoDatas = mPoiIndoorInfoDatas;
        this.mRoutePlanSearch = mRoutePlanSearch;
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
                LocationManager mStartLM = startLocation();

                planRouteInDoor(position,mStartLM);

                searchOnePoiResult(position);
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
//            adapter_poiitem_tv_floor
            startIconIv = (Button) view.findViewById(R.id.adapter_poiitem_iv_startnavigation);
        }
    }

    private LocationManager startLocation(){
        LocationManager mLocationManager = LocationManager.getLocationSingInstance(activity,mMapView);
        mLocationManager.startLocationSearch();
        return mLocationManager;
    }

    private void searchOnePoiResult(int position){
        List<PoiIndoorInfo> mListPoiIndoorInfo = new ArrayList<PoiIndoorInfo>();
        mListPoiIndoorInfo.add(mPoiIndoorInfoDatas.get(position));
        PoiIndoorResult mPoiIndoorResult = new PoiIndoorResult();
        mPoiIndoorResult.setPageNum(1);
        mPoiIndoorResult.setPoiNum(1);
        mPoiIndoorResult.setmArrayPoiInfo(mListPoiIndoorInfo);

        activity.onGetPoiIndoorResult(mPoiIndoorResult);
        activity.dimissListAndShowFloor();
    }

    private void planRouteInDoor(int position,LocationManager mStartLM){
        BDLocation location = mStartLM.getResultLocation();
        if(null!=location){
            LatLng startLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            String startFloor = location.getFloor();
            LatLng endLatLng = mPoiIndoorInfoDatas.get(position).latLng;
            String endFloor = mPoiIndoorInfoDatas.get(position).floor;

            IndoorPlanNode startNode = new IndoorPlanNode(startLatLng, MyMapHelper.testFloor);//39.917380,
            IndoorPlanNode endNode = new IndoorPlanNode(endLatLng,endFloor);
            IndoorRoutePlanOption irpo = new IndoorRoutePlanOption().from(startNode).to(endNode);
            mRoutePlanSearch.walkingIndoorSearch(irpo);
            Toast.makeText(activity,"路线规划",Toast.LENGTH_SHORT).show();
        }
    }
}
