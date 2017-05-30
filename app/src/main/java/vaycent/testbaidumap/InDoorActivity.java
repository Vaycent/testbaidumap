package vaycent.testbaidumap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorPlanNode;
import com.baidu.mapapi.search.route.IndoorRouteLine;
import com.baidu.mapapi.search.route.IndoorRoutePlanOption;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.EventBus.LocationMsgEvent;
import vaycent.testbaidumap.InDoor.BaseStripAdapter;
import vaycent.testbaidumap.InDoor.IndoorRouteOverlay;
import vaycent.testbaidumap.InDoor.MyLocationListener;
import vaycent.testbaidumap.InDoor.StripListView;
import vaycent.testbaidumap.Location.LocationManager;
import vaycent.testbaidumap.Poi.IndoorPoiOverlay;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.widget.PoiItemAdapter;



public class InDoorActivity extends AppCompatActivity implements OnGetRoutePlanResultListener,OnGetPoiSearchResultListener,
        BaiduMap.OnBaseIndoorMapListener{

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private RoutePlanSearch mRoutePlanSearch;
    private IndoorRouteLine mIndoorRouteline;
    private PoiSearch mPoiSearch;
    private LocationManager mLocationManager;

    private final double testLat = MyMapHelper.testLat;
    private final double testLon = MyMapHelper.testLon;

    private int nodeIndex = -1;
    private MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;
    private StripListView stripListView;
    private BaseStripAdapter mFloorListAdapter;

    private Button btnInhourseStart,btnNavigationMap,btnGetCurrentPosition,btnPath;
    private Button mBtnPre, mBtnNext; //节点
    private Button mBtnStartSearch;
    private EditText mEdtSearchTx;
    private LinearLayout navigationListLayout;
    private LinearLayout pathMsgLayout;
    private TextView mTxPathMsg;
    private RadioGroup radiogroup;
    private RadioButton radioButtonMeal;

    private String currentTab;
    private String lastTab;
    private RecyclerView poiListView;
    private PoiItemAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;


    private int btnResourceCode=0;
    private MapUtils mapUtilsHelper = new MapUtils();




    //用于定位
    public LocationClient mLocationClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//自定义监听类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();

        initStripListView();

        initMap();

        changeTabMode();


//        isZh();

    }
    @Override
    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        if(null!=mLocationClient&&mLocationClient.isStarted())
            mLocationClient.stop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mRoutePlanSearch.destroy();
        mPoiSearch.destroy();
        mLocationManager.destroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(View.VISIBLE ==navigationListLayout.getVisibility()){
                dimissListAndShowFloor();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    /* 获取室内路线 */
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

//        navigationListLayout.setVisibility(View.GONE);
//        stripListView.setVisibility(View.VISIBLE);

        if (indoorRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            IndoorRouteOverlay overlay = new IndoorRouteOverlay(mBaiduMap);
            mIndoorRouteline = indoorRouteResult.getRouteLines().get(0);
            nodeIndex = -1;
            pathMsgLayout.setVisibility(View.VISIBLE);
            overlay.setData(indoorRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
//            Toast.makeText(this,"onGetIndoorRouteResult",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    /* 缩放等级到室内时触发 */
    @Override
    public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
        if (b) {
            stripListView.setVisibility(View.VISIBLE);
            btnNavigationMap.setVisibility(View.VISIBLE);
            if (mapBaseIndoorMapInfo == null) {
                return;
            }
            mFloorListAdapter.setmFloorList(mapBaseIndoorMapInfo.getFloors());
            stripListView.setStripAdapter(mFloorListAdapter);
        } else {
            stripListView.setVisibility(View.GONE);
            btnNavigationMap.setVisibility(View.GONE);
        }
        mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
    }

    /* 获取Poi结果 */
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        // 获取POI检索结果
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(InDoorActivity.this, "未找到结果",Toast.LENGTH_LONG).show();
            return;
        }

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
//          mBaiduMap.clear();
            if(poiResult != null){
                if(poiResult.getAllPoi()!= null && poiResult.getAllPoi().size()>0){
                    Log.e("Vaycent","result.getAllPoi() size:"+poiResult.getAllPoi());
                    for(int i=0;i<poiResult.getAllPoi().size();i++){
                        PoiInfo info = poiResult.getAllPoi().get(i);
                        Log.e("Vaycent","name:"+info.name);
                        Log.e("Vaycent","address:"+info.address);
                        Log.e("Vaycent","location lat:"+info.location.latitude);
                        Log.e("Vaycent","location lon:"+info.location.longitude);
                        Log.e("Vaycent","hasCaterDetails:"+info.hasCaterDetails);
                        Log.e("Vaycent","isPano:"+info.isPano);

                    }
//                    dataList.addAll(result.getAllPoi());
////                  adapter.notifyDataSetChanged();
//                    Message msg = new Message();
//                    msg.what = 0;
//                    handler.sendMessage(msg);
                }
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    /* 获取室内的Poi结果 */
    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        mBaiduMap.clear();
        if (poiIndoorResult == null  || poiIndoorResult.error == PoiIndoorResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(InDoorActivity.this, "无结果" , Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("Vaycent","result.getmArrayPoiInfo().size:"+poiIndoorResult.getmArrayPoiInfo().size());
        for(int i=0;i<poiIndoorResult.getmArrayPoiInfo().size();i++){
            Log.e("Vaycent","name:"+poiIndoorResult.getmArrayPoiInfo().get(i).name);
        }

        if (poiIndoorResult.error == PoiIndoorResult.ERRORNO.NO_ERROR) {
//            IndoorPoiOverlay overlay = new MyIndoorPoiOverlay(mBaiduMap);
//            mBaiduMap.setOnMarkerClickListener(overlay);
//            overlay.setData(poiIndoorResult);
//            overlay.addToMap();
//            overlay.zoomToSpan();


            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            poiListView.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new PoiItemAdapter(this,mMapView,poiInfosList,mRoutePlanSearch);
            poiListView.setAdapter(poiItemAdapter);
            poiItemAdapter.notifyDataSetChanged();
        }
    }

    /* Poi的Overlay事件 */
    private class MyIndoorPoiOverlay extends IndoorPoiOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该 IndoorPoiOverlay 引用的 BaiduMap 对象
         */
        public MyIndoorPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        /**
         * 响应点击室内POI点事件
         * @param i
         *            被点击的poi在
         *            {@link com.baidu.mapapi.search.poi.PoiIndoorResult#getmArrayPoiInfo()} } 中的索引
         * @return
         */
        public boolean onPoiClick(int i) {
            PoiIndoorInfo info =  getIndoorPoiResult().getmArrayPoiInfo().get(i);
            Toast.makeText(InDoorActivity.this, info.name + ",在" + info.floor + "层,坐标:"+info.latLng.latitude+","+info.latLng.longitude, Toast.LENGTH_LONG).show();
            return false;
        }

    }










    private void initLayout(){
        RelativeLayout layout = new RelativeLayout(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mainview = inflater.inflate(R.layout.activity_indoor, null);
        layout.addView(mainview);
//        setContentView(R.layout.activity_indoor);
        stripListView = new StripListView(this);
        layout.addView( stripListView );
        setContentView(layout);


        btnInhourseStart = (Button)findViewById(R.id.activity_indoor_btn_inhoursestart);
        btnNavigationMap = (Button)findViewById(R.id.activity_indoor_btn_navigationmap);
        btnGetCurrentPosition = (Button)findViewById(R.id.activity_indoor_btn_currentposition);
        btnPath = (Button)findViewById(R.id.activity_indoor_btn_path);
        navigationListLayout =(LinearLayout)findViewById(R.id.activity_indoor_llyt_navigationlistlayout) ;


        mMapView = (MapView)findViewById(R.id.activity_main_mv_map);
        mBtnPre = (Button) findViewById(R.id.activity_indoor_btn_prepath);
        mBtnNext = (Button) findViewById(R.id.activity_indoor_btn_nextpath);
        mBtnStartSearch = (Button)findViewById(R.id.activity_indoor_btn_startsearch);
        mEdtSearchTx = (EditText)findViewById(R.id.activity_indoor_et_searchtext);

        mTxPathMsg= (TextView) findViewById(R.id.activity_indoor_tv_pathmsg);
        pathMsgLayout = (LinearLayout)findViewById(R.id.activity_indoor_llyt_pathmsglayout);

        poiListView = (RecyclerView) findViewById(R.id.activity_routemap_rv_commonflightlistview);
        radiogroup = (RadioGroup) findViewById(R.id.activity_routemap_rd_content_tab);
        radioButtonMeal = (RadioButton) findViewById(R.id.activity_routemap_rbtn_contentfirst);

        mBtnStartSearch.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
               if("".equals(mEdtSearchTx.getText().toString().trim())){
                   return;
               }
                MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
                if (indoorInfo == null) {
                    Toast.makeText(InDoorActivity.this, "当前无室内图，无法搜索" , Toast.LENGTH_LONG).show();
                    return;
                }
               PoiIndoorOption option = new PoiIndoorOption().poiIndoorBid(
                        indoorInfo.getID()).poiIndoorWd(mEdtSearchTx.getText().toString());
                mPoiSearch.searchPoiIndoor( option);
            }
        });

        btnInhourseStart.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
//                测试西单大悦城
                IndoorPlanNode startNode = new IndoorPlanNode(new LatLng(39.917380 ,116.37978), "F2");//39.917380,
                IndoorPlanNode endNode = new IndoorPlanNode(new LatLng(39.917239, 116.37955), "F6");
                IndoorRoutePlanOption irpo = new IndoorRoutePlanOption().from(startNode).to(endNode);
                mRoutePlanSearch.walkingIndoorSearch(irpo);
            }
        });

        btnPath.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                Intent intent = new Intent();
                intent.setClass(InDoorActivity.this,IndoorSearchActivity.class);
                startActivity(intent);
            }
        });
        btnNavigationMap.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
//                showListAndShowFloor();
//                mEdtSearchTx.setText("");
//                radioButtonMeal.setChecked(true);
                mLocationManager.startLocationSearch();
                btnResourceCode = btnNavigationMap.getId();
            }
        });
        btnGetCurrentPosition.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                mLocationManager.startLocationSearch();
                btnResourceCode = btnGetCurrentPosition.getId();
            }
        });
    }

    private void initStripListView(){
        mFloorListAdapter = new BaseStripAdapter(InDoorActivity.this);
        stripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mMapBaseIndoorMapInfo == null) {
                    return;
                }
                String floor = (String) mFloorListAdapter.getItem(position);
                mBaiduMap.switchBaseIndoorMapFloor(floor, mMapBaseIndoorMapInfo.getID());
                mFloorListAdapter.setSelectedPostion(position);
                mFloorListAdapter.notifyDataSetInvalidated();
            }
        });
    }

    private void initMap(){
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setIndoorEnable(true); // 打开室内图
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 隐藏地图上比例尺
        mMapView.showScaleControl(false);
        mMapView.getChildAt(2).setPadding(0,0,50,800);//这是控制缩放控件的位置


        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
        mPoiSearch = mPoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mLocationManager = LocationManager.getLocationSingInstance(this,mMapView);

        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (b == false || mapBaseIndoorMapInfo == null) {
                    stripListView.setVisibility(View.INVISIBLE);
                    return;
                }
                mFloorListAdapter.setmFloorList( mapBaseIndoorMapInfo.getFloors());
                stripListView.setVisibility(View.VISIBLE);
                stripListView.setStripAdapter(mFloorListAdapter);
                mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
            }
        });
        mapUtilsHelper.mapMoveTo(mBaiduMap,testLat,testLon );
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (mBaiduMap.isBaseIndoorMapMode()) {
            LatLng nodeLocation = null;
            String nodeTitle = null;
            IndoorRouteLine.IndoorRouteStep step = null;


            if (mIndoorRouteline == null || mIndoorRouteline.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.activity_indoor_btn_prepath) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.activity_indoor_btn_nextpath) {
                if (nodeIndex < mIndoorRouteline.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.activity_indoor_btn_prepath) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            step = mIndoorRouteline.getAllStep().get(nodeIndex);
            nodeLocation = step.getEntrace().getLocation();
            nodeTitle = step.getInstructions();

            if (nodeLocation == null || nodeTitle == null) {
                return;
            }

            // 移动节点至中心
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
            // show popup
//            popupText = new TextView(MainActivity.this);
//            popupText.setBackgroundResource(R.drawable.popup);
//            popupText.setTextColor(0xFF000000);
//            popupText.setText(step.getFloorId() + ":" + nodeTitle);

            mTxPathMsg.setText(step.getFloorId() + ":" + nodeTitle);
//            mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

            // 让楼层对应变化
            mBaiduMap.switchBaseIndoorMapFloor(step.getFloorId(), mMapBaseIndoorMapInfo.getID());
//        mFloorListAdapter.setSelectedPostion();
            mFloorListAdapter.notifyDataSetInvalidated();
        }else{
            Toast.makeText(InDoorActivity.this,"请打开室内图或将室内图移入屏幕内",Toast.LENGTH_SHORT).show();
        }

        //控制显示隐藏
        if(0==nodeIndex){
            mBtnPre.setVisibility(View.GONE);
        }else{
            mBtnPre.setVisibility(View.VISIBLE);
        }
        if(nodeIndex == mIndoorRouteline.getAllStep().size()-1){
            mBtnNext.setVisibility(View.GONE);
        }else{
            mBtnNext.setVisibility(View.VISIBLE);
        }
    }

    private void initCurrentLoacation(){
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener(mBaiduMap);
        mLocationClient.registerLocationListener(mMyLocationListener);

        InitLocation();//初始化
    }

    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(5*1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setNeedDeviceDirect(true); //返回的定位结果包含手机机头方向
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
        mLocationClient.startIndoorMode();
    }

    private void outdoorNearbySearch(int page, String keyword) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        Double latitude = testLat;
        Double longitude = testLon;
        nearbySearchOption.location(new LatLng(latitude, longitude));
        nearbySearchOption.keyword(keyword);
        nearbySearchOption.radius(1000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        nearbySearchOption.pageCapacity(20);
        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    private void indoorNearBySearch(int page, String keyword){
        MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
        if (indoorInfo == null) {
            Toast.makeText(InDoorActivity.this, "当前无室内图，无法搜索", Toast.LENGTH_LONG).show();
            return;
        }
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(indoorInfo.getID());
        option.poiIndoorWd(keyword);

        if(option == null){
            Toast.makeText(InDoorActivity.this, "当前无室内POI无法搜索" , Toast.LENGTH_LONG).show();
            return;
        }
        mPoiSearch.searchPoiIndoor(option);
    }

    private void changeTabMode() {
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    lastTab = currentTab;
                    switch (i) {
                        case R.id.activity_routemap_rbtn_contentfirst:
                            currentTab = "餐饮";
                            break;
                        case R.id.activity_routemap_rbtn_contentsecond:
                            currentTab = "值机口";
                            break;
                        case R.id.activity_routemap_rbtn_contentthird:
                            currentTab = "登机口";
                            break;
                        case R.id.activity_routemap_rbtn_contentfourth:
                            currentTab = "到达口";
                            break;
                        case R.id.activity_routemap_rbtn_contentfifth:
                            currentTab = "出发厅";
                            break;
                    }

                    //根据选择的tab进行查询
                    if (currentTab != lastTab ) {//|| poiInfosList.size() == 0)
//                        mapManager.boundSearch(currentNum, currentTab);
//                        outdoorNearbySearch(1, currentTab);
                        indoorNearBySearch(1,currentTab);
                    }
                }
            });
    }




    public void dimissListAndShowFloor(){
        navigationListLayout.setVisibility(View.GONE);
        MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
        if (indoorInfo != null) {
            stripListView.setVisibility(View.VISIBLE);
        }
    }

    public void showListAndShowFloor(){
        navigationListLayout.setVisibility(View.VISIBLE);
        stripListView.setVisibility(View.GONE);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationMsgEvent event) {

        if(btnResourceCode == btnNavigationMap.getId()){
            BDLocation callbackLocation = event.getCallbackLocation();

            MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
            String indoorId = indoorInfo!=null?indoorInfo.getID():"";

            Intent navigationIntent = new Intent();
            navigationIntent.setClass(InDoorActivity.this,NavigationMapActivity.class);
            navigationIntent.putExtra("callbackLocation",callbackLocation);
            navigationIntent.putExtra("indoorId",indoorId);
            startActivity(navigationIntent);
        }else if(btnResourceCode == btnGetCurrentPosition.getId()){
            mapUtilsHelper.mapMoveTo(mLocationManager.getMapView().getMap(),event.getCallbackLocation().getLatitude(),event.getCallbackLocation().getLongitude());
        }
        btnResourceCode=0;
    }

}
