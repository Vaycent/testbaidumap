package vaycent.testbaidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;

import org.greenrobot.eventbus.EventBus;

import vaycent.testbaidumap.Adapter.FloorsListAdapter;
import vaycent.testbaidumap.EventBus.PathPlanResultMsgEvent;
import vaycent.testbaidumap.Objects.Indoor;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.widget.FloorsList;

public class ChooseOnMapActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapUtils mMapUtils = new MapUtils();
    private Indoor mIndoorData;
    private int requestCode;
    private MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;

    private TextView placeName,placeDistance,placeFloor;
    private RelativeLayout bottomRlyt;
    private Button btnConfirm;
    private String chooseFloor;
    private MapPoi mMapPoi;
    private FloorsList mFloorsList;
    private FloorsListAdapter mFloorsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_on_map);

        initData();

        initLayout();

        initMap();

    }

    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }



    private void initData(){

        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);
        requestCode = fromIntent.getIntExtra("requestCode",0);

        chooseFloor = mIndoorData.getFloorsList().get(0);
    }

    private void initLayout(){
        mMapView = (MapView)findViewById(R.id.activity_choose_on_map_mv);
        placeName=(TextView) findViewById(R.id.include_choose_onmap_tv_placename);
        placeDistance=(TextView) findViewById(R.id.include_choose_onmap_tv_distance);
        placeFloor=(TextView) findViewById(R.id.include_choose_onmap_tv_floor);
        btnConfirm=(Button) findViewById(R.id.include_choose_onmap_btn_confirm);
        bottomRlyt = (RelativeLayout)findViewById(R.id.include_choose_on_map_bottomlayout);

        btnConfirm.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                PoiIndoorInfo mPoiIndoorInfo = new PoiIndoorInfo();
                mPoiIndoorInfo.name = mMapPoi.getName();
                mPoiIndoorInfo.floor = chooseFloor;
                mPoiIndoorInfo.latLng = mMapPoi.getPosition();


                EventBus.getDefault().post(new PathPlanResultMsgEvent(mPoiIndoorInfo,requestCode));
                Intent intent = new Intent();
                intent.setClass(ChooseOnMapActivity.this, PathPlanActivity.class);
                startActivity(intent);
            }
        });

        mFloorsList = (FloorsList)findViewById(R.id.activity_choose_on_map_fl_floors);
        mFloorsList.getFloorsListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mMapBaseIndoorMapInfo == null) {
                    return;
                }
                mFloorsListAdapter.setSelectedPostion(position);
                mFloorsListAdapter.notifyDataSetChanged();
                String floor = (String) mFloorsListAdapter.getItem(position);
                mBaiduMap.switchBaseIndoorMapFloor(floor, mMapBaseIndoorMapInfo.getID());
                chooseFloor = floor;
            }
        });

    }

    private void initMap(){
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setIndoorEnable(true);
        mMapUtils.isShowMapScale(mMapView,false);
        mMapUtils.isShowBaiDuLogo(mMapView,false);
        mMapUtils.isShowZoomWidget(mMapView,false);

        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (b == false || mapBaseIndoorMapInfo == null) {
                    mFloorsList.setVisibility(View.INVISIBLE);
                    return;
                }
                mFloorsList.setVisibility(View.VISIBLE);
                mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;


                initFloorsList();
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {

                mMapPoi = mapPoi;
                mMapUtils.drawMarkerWithLatLng(mBaiduMap,mMapPoi.getPosition());
                placeName.setText(mapPoi.getName());
                placeDistance.setText(getDistanceText(mMapPoi));
                placeFloor.setText(chooseFloor);
                if(bottomRlyt.getVisibility()==View.GONE)
                    bottomRlyt.setVisibility(View.VISIBLE);
                if(btnConfirm.getVisibility()==View.GONE)
                    btnConfirm.setVisibility(View.VISIBLE);

                return false;
            }
        });

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMapUtils.mapMoveTo(mBaiduMap,mIndoorData.getAirportLatLng().latitude,mIndoorData.getAirportLatLng().longitude);
            }
        });
    }

    private String getDistanceText(MapPoi mapPoi){
        LatLng currentLatLng = new LatLng(mIndoorData.getCurrentLocation().getLatitude(),mIndoorData.getCurrentLocation().getLongitude());
        LatLng targetLatLng = new LatLng(mapPoi.getPosition().latitude,mapPoi.getPosition().longitude);
        int distance = mMapUtils.calculateDistance(currentLatLng,targetLatLng);
        return "离目的地距离"+distance+"米";
    }

    private void initFloorsList(){
        if(null == mFloorsListAdapter)
            mFloorsListAdapter = new FloorsListAdapter(this,mMapBaseIndoorMapInfo.getFloors());
        mFloorsList.getFloorsListView().setAdapter(mFloorsListAdapter);
        mFloorsListAdapter.notifyDataSetInvalidated();



    }
}
