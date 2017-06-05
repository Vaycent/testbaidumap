package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
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

import java.util.List;

import vaycent.testbaidumap.EventBus.LocationMsgEvent;
import vaycent.testbaidumap.EventBus.OnePoiMsgEvent;
import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.InDoor.BaseStripAdapter;
import vaycent.testbaidumap.InDoor.IndoorRouteOverlay;
import vaycent.testbaidumap.InDoor.MyLocationListener;
import vaycent.testbaidumap.InDoor.StripItem;
import vaycent.testbaidumap.InDoor.StripListView;
import vaycent.testbaidumap.Location.LocationManager;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.Poi.IndoorPoiOverlay;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.InDoorActivityBinding;
import vaycent.testbaidumap.widget.NavigationItemAdapter;


public class InDoorActivity extends AppCompatActivity implements OnGetRoutePlanResultListener,OnGetPoiSearchResultListener,
        BaiduMap.OnBaseIndoorMapListener{

    private BaiduMap mBaiduMap;
    private RoutePlanSearch mRoutePlanSearch;
    private IndoorRouteLine mIndoorRouteline;
    private PoiSearch mPoiSearch;
    private LocationManager mLocationManager;

    private final double testLat = StaticValMapHelper.testLat;
    private final double testLon = StaticValMapHelper.testLon;
    private MapUtils mapUtilsHelper = new MapUtils();

    private int nodeIndex = -1;
    private MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;
    private StripListView stripListView;
    private BaseStripAdapter mFloorListAdapter;

    private String currentTab;
    private String lastTab;
    private NavigationItemAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;
    private int btnResourceCode=0;

    //用于定位
    public LocationClient mLocationClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//自定义监听类

    private InDoorActivityBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_indoor);
        EventBus.getDefault().register(this);

        initLayout();

        initStripListView();

        initMap();

    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mBinding.activityMainMvMap.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mBinding.activityMainMvMap.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(null!=mLocationClient&&mLocationClient.isStarted())
            mLocationClient.stop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.activityMainMvMap.onDestroy();
        mRoutePlanSearch.destroy();
        mPoiSearch.destroy();
        mLocationManager.destroy();
        EventBus.getDefault().unregister(this);
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


        if (indoorRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            IndoorRouteOverlay overlay = new IndoorRouteOverlay(mBaiduMap);
            mIndoorRouteline = indoorRouteResult.getRouteLines().get(0);
            nodeIndex = -1;
            mBinding.activityIndoorLlytPathmsglayout.setVisibility(View.VISIBLE);
            overlay.setData(indoorRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }else{
            Toast.makeText(this,"室内路线规划失败["+indoorRouteResult.error.toString()+"]",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    /* 缩放等级到室内时触发 */
    @Override
    public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
//        if (b) {
//            stripListView.setVisibility(View.VISIBLE);
//            mBinding.activityIndoorBtnNavigationmap.setVisibility(View.VISIBLE);
//            if (mapBaseIndoorMapInfo == null) {
//                return;
//            }
//            mFloorListAdapter.setmFloorList(mapBaseIndoorMapInfo.getFloors());
//            stripListView.setStripAdapter(mFloorListAdapter);
//        } else {
//            stripListView.setVisibility(View.GONE);
//            mBinding.activityIndoorBtnNavigationmap.setVisibility(View.GONE);
//        }
//        mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
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
            IndoorPoiOverlay overlay = new MyIndoorPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(poiIndoorResult);
            overlay.addToMap();
            overlay.zoomToSpan();


//            if(null == poiInfosList)
//                poiInfosList= new ArrayList<PoiIndoorInfo>();
//
//            poiListView.setLayoutManager(new LinearLayoutManager(this));
//            poiInfosList.clear();
//            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息
//
//            poiItemAdapter = new PoiItemAdapter(this,mMapView,poiInfosList,mRoutePlanSearch);
//            poiListView.setAdapter(poiItemAdapter);
//            poiItemAdapter.notifyDataSetChanged();
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
        stripListView = new StripListView(this);
        mBinding.activityIndoorRlytRoot.addView(stripListView);

        mBinding.activityIndoorEtSearchtext.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
            //跳转
            }
        });

        mBinding.activityIndoorBtnInhoursestart.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
//                测试西单大悦城
                IndoorPlanNode startNode = new IndoorPlanNode(new LatLng(39.917380 ,116.37978), "F2");//39.917380,
                IndoorPlanNode endNode = new IndoorPlanNode(new LatLng(39.917239, 116.37955), "F6");
                IndoorRoutePlanOption irpo = new IndoorRoutePlanOption().from(startNode).to(endNode);
                mRoutePlanSearch.walkingIndoorSearch(irpo);
            }
        });


        mBinding.activityIndoorBtnNavigationmap.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                mLocationManager.startLocationSearch();
                btnResourceCode = mBinding.activityIndoorBtnNavigationmap.getId();
            }
        });
        mBinding.activityIndoorBtnCurrentposition.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                mLocationManager.startLocationSearch();
                btnResourceCode = mBinding.activityIndoorBtnCurrentposition.getId();
            }
        });
        mBinding.activityIndoorBtnPath .setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                mLocationManager.startLocationSearch();
                btnResourceCode = mBinding.activityIndoorBtnPath .getId();
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
//        View headView = LayoutInflater.from(this).inflate(R.layout.adapter_striplistview_headlayout, null);
//        View footView = LayoutInflater.from(this).inflate(R.layout.adapter_striplistview_footlayout, null);
//        stripListView.addHeaderView(headView);
//        stripListView.addFooterView(footView );
    }

    private void initMap(){
        mBaiduMap = mBinding.activityMainMvMap.getMap();
        mBaiduMap.setIndoorEnable(true); // 打开室内图
        mapUtilsHelper.isShowBaiDuLogo(mBinding.activityMainMvMap,false);
        mapUtilsHelper.isShowMapScale(mBinding.activityMainMvMap,false);
        mapUtilsHelper.setZoomWidgetPosition(mBinding.activityMainMvMap,0,0,StripItem.dip2px(this, 10),StripItem.dip2px(this, 250));


        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
        mPoiSearch = mPoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mLocationManager = LocationManager.getLocationSingInstance(this,mBinding.activityMainMvMap);

        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (b == false || mapBaseIndoorMapInfo == null) {
                    stripListView.setVisibility(View.INVISIBLE);
                    mBinding.activityIndoorBtnNavigationmap.setVisibility(View.INVISIBLE);
                    mBinding.activityIndoorBtnPath.setVisibility(View.INVISIBLE);
                    return;
                }
                mFloorListAdapter.setmFloorList( mapBaseIndoorMapInfo.getFloors());
                stripListView.setVisibility(View.VISIBLE);
                mBinding.activityIndoorBtnNavigationmap.setVisibility(View.VISIBLE);
                mBinding.activityIndoorBtnPath.setVisibility(View.VISIBLE);
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

            mBinding.activityIndoorTvPathmsg.setText(step.getFloorId() + ":" + nodeTitle);
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
            mBinding.activityIndoorBtnPrepath.setVisibility(View.GONE);
        }else{
            mBinding.activityIndoorBtnPrepath.setVisibility(View.VISIBLE);
        }
        if(nodeIndex == mIndoorRouteline.getAllStep().size()-1){
            mBinding.activityIndoorBtnNextpath.setVisibility(View.GONE);
        }else{
            mBinding.activityIndoorBtnNextpath.setVisibility(View.VISIBLE);
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationMsgEvent event) {
        if(null==event||null==event.getCallbackLocation())
            return;
        mBinding.activityIndoorRlytBottomNavigation.setVisibility(View.GONE);
        if(btnResourceCode ==  mBinding.activityIndoorBtnNavigationmap.getId()){
            BDLocation callbackLocation = event.getCallbackLocation();
            MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
            String indoorId = indoorInfo!=null?indoorInfo.getID():"";

            Intent navigationIntent = new Intent();
            navigationIntent.setClass(InDoorActivity.this,NavigationMapActivity.class);
            navigationIntent.putExtra("callbackLocation",callbackLocation);
            navigationIntent.putExtra("indoorId",indoorId);
            navigationIntent.putStringArrayListExtra("floors",mMapBaseIndoorMapInfo.getFloors());
            startActivity(navigationIntent);
        }else if(btnResourceCode == mBinding.activityIndoorBtnCurrentposition.getId()){
            mapUtilsHelper.mapMoveTo(mBaiduMap,event.getCallbackLocation().getLatitude(),event.getCallbackLocation().getLongitude());
        }else if(btnResourceCode == mBinding.activityIndoorBtnPath.getId()){
            BDLocation callbackLocation = event.getCallbackLocation();
            MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
            String indoorId = indoorInfo!=null?indoorInfo.getID():"";

            Intent pathIntent = new Intent();
            pathIntent.setClass(InDoorActivity.this,PathPlanActivity.class);
            pathIntent.putExtra("callbackLocation",callbackLocation);
            pathIntent.putExtra("indoorId",indoorId);
            startActivity(pathIntent);
        }
        btnResourceCode=0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RoutePlanMsgEvent event) {
        if(null!=event.getResultRoutePlan()){
            mBinding.activityIndoorRlytBottomNavigation.setVisibility(View.GONE);
            ResultRoutePlan mResultRoutePlan = event.getResultRoutePlan();
            IndoorPlanNode startNode = new IndoorPlanNode(mResultRoutePlan.startLatLng, mResultRoutePlan.startFloor);
            IndoorPlanNode endNode = new IndoorPlanNode(mResultRoutePlan.endLatLng,mResultRoutePlan.endFloor);
            IndoorRoutePlanOption irpo = new IndoorRoutePlanOption().from(startNode).to(endNode);
            mRoutePlanSearch.walkingIndoorSearch(irpo);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnePoiMsgEvent event) {
        if(null!=event.getPoiIndoorResult()&&null!=event.getCallBackLocation()){
            PoiIndoorResult mPoiIndoorResult = event.getPoiIndoorResult();

            if(mPoiIndoorResult.error == PoiIndoorResult.ERRORNO.NO_ERROR){
                mBaiduMap.clear();
                IndoorPoiOverlay overlay = new MyIndoorPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(mPoiIndoorResult);
                overlay.addToMap();
                overlay.zoomToSpan();

                initBottomNavigation(mPoiIndoorResult,event.getCallBackLocation());
            }
        }
    }

    private void initBottomNavigation(final PoiIndoorResult mPoiIndoorResult, final BDLocation mCallBackLocation){
        View view = mBinding.activityIndoorIncludeBottomNavigation.getRootView();
        TextView name = (TextView)view.findViewById(R.id.adapter_poiitem_tv_placename);
        TextView floor = (TextView) view.findViewById(R.id.adapter_poiitem_tv_floor);
        TextView distance = (TextView) view.findViewById(R.id.adapter_poiitem_tv_distance);
        name.setText(mPoiIndoorResult.getmArrayPoiInfo().get(0).name);
        floor.setText(mPoiIndoorResult.getmArrayPoiInfo().get(0).floor);
        String distanceStr = getDistanceText(mCallBackLocation,mPoiIndoorResult.getmArrayPoiInfo().get(0));
        distance.setText(distanceStr);
        LinearLayout startNavigation = (LinearLayout)view.findViewById(R.id.adapter_poiitem_iv_startnavigationlayout);
        startNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planRouteInDoor(mCallBackLocation,mPoiIndoorResult.getmArrayPoiInfo().get(0));
            }
        });
        mBinding.activityIndoorRlytBottomNavigation.setVisibility(View.VISIBLE);
    }

    private String getDistanceText(BDLocation mCallBackLocation,PoiIndoorInfo mPoiIndoorInfo){
        LatLng currentLatLng = new LatLng(mCallBackLocation.getLatitude(),mCallBackLocation.getLongitude());
        int distance = mapUtilsHelper.calculateDistance(currentLatLng,mPoiIndoorInfo.latLng);
        return "离目的地距离"+distance+"米";
    }

    private void planRouteInDoor(BDLocation mCallBackLocation,PoiIndoorInfo mPoiIndoorInfo){
        if(null!=mCallBackLocation){
            LatLng startLatLng = new LatLng(mCallBackLocation.getLatitude(),mCallBackLocation.getLongitude());
            String startFloor = mCallBackLocation.getFloor();
            LatLng endLatLng = mPoiIndoorInfo.latLng;
            String endFloor = mPoiIndoorInfo.floor;

            ResultRoutePlan mResultRoutePlan = new ResultRoutePlan(startLatLng,startFloor,endLatLng,endFloor);
            EventBus.getDefault().post(new RoutePlanMsgEvent(mResultRoutePlan));
        }
    }





}
