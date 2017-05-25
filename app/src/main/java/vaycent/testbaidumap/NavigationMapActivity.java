package vaycent.testbaidumap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import vaycent.testbaidumap.InDoor.BaseStripAdapter;
import vaycent.testbaidumap.InDoor.StripListView;
import vaycent.testbaidumap.Poi.IndoorPoiOverlay;


public class NavigationMapActivity extends AppCompatActivity implements OnGetPoiSearchResultListener,
        BaiduMap.OnBaseIndoorMapListener{

    private MapView mNavigationMapView;
    private BaiduMap mNavigationBaiduMap;
    private PoiSearch mPoiSearch = null;

    private StripListView stripView;
    private BaseStripAdapter mFloorListAdapter;
    private MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;


    private final double testLat = MyMapHelper.testLat;
    private final double testLon = MyMapHelper.testLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationMapView = (MapView)findViewById(R.id.activity_navigationmap_mv_map) ;
        mNavigationBaiduMap = mNavigationMapView.getMap();
        MapUtils.mapMoveTo(mNavigationBaiduMap,testLat,testLon); // 北京南站


        stripView = new StripListView(this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.viewStub);
        layout.addView(stripView);



        mFloorListAdapter = new BaseStripAdapter(this);
        mNavigationBaiduMap.setOnBaseIndoorMapListener(this);
        mNavigationBaiduMap.setIndoorEnable(true); // 设置打开室内图开关



        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);


        stripView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mMapBaseIndoorMapInfo == null) {
                    return;
                }
                String floor = (String) mFloorListAdapter.getItem(position);
                mNavigationBaiduMap.switchBaseIndoorMapFloor(floor, mMapBaseIndoorMapInfo.getID());
                mFloorListAdapter.setSelectedPostion(position);
                mFloorListAdapter.notifyDataSetInvalidated();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mNavigationMapView.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNavigationMapView.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
        if (b) {
            stripView.setVisibility(View.VISIBLE);
            if (mapBaseIndoorMapInfo == null) {
                return;
            }
            mFloorListAdapter.setmFloorList(mapBaseIndoorMapInfo.getFloors());
            stripView.setStripAdapter(mFloorListAdapter);

        } else {
            stripView.setVisibility(View.GONE);
        }
        mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        // 获取POI检索结果
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(NavigationMapActivity.this, "未找到结果",Toast.LENGTH_LONG).show();
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

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        mNavigationBaiduMap.clear();
        if (poiIndoorResult == null  || poiIndoorResult.error == PoiIndoorResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(NavigationMapActivity.this, "无结果" , Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("Vaycent","result.getmArrayPoiInfo().size:"+poiIndoorResult.getmArrayPoiInfo().size());
        for(int i=0;i<poiIndoorResult.getmArrayPoiInfo().size();i++){
            Log.e("Vaycent","name:"+poiIndoorResult.getmArrayPoiInfo().get(i).name);
        }

        if (poiIndoorResult.error == PoiIndoorResult.ERRORNO.NO_ERROR) {
            IndoorPoiOverlay overlay = new MyIndoorPoiOverlay(mNavigationBaiduMap);
            mNavigationBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(poiIndoorResult);
            overlay.addToMap();
            overlay.zoomToSpan();

        }
    }

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
            Toast.makeText(NavigationMapActivity.this, info.name + ",在" + info.floor + "层,坐标:"+info.latLng.latitude+","+info.latLng.longitude, Toast.LENGTH_LONG).show();
            return false;
        }

    }






    //周边
    private void searchNeayBy(){

        MapBaseIndoorMapInfo indoorInfo = mNavigationBaiduMap.getFocusedBaseIndoorMapInfo();
        if (indoorInfo == null) {
            Toast.makeText(NavigationMapActivity.this, "当前无室内图，无法搜索" , Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("Vaycent","indoorInfo.getID:"+indoorInfo.getID());
        Log.e("Vaycent","indoorInfo.getCurFloor:"+indoorInfo.getCurFloor());
        Log.e("Vaycent","indoorInfo.getFloors:"+indoorInfo.getFloors());


//        PoiIndoorOption option = new PoiIndoorOption().poiIndoorBid(
//                indoorInfo.getID());
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(indoorInfo.getID());
        option.poiIndoorWd("餐饮");


        if(option == null){
            Toast.makeText(NavigationMapActivity.this, "当前无室内POI无法搜索" , Toast.LENGTH_LONG).show();
            return;
        }
//        Log.e("Vaycent","option.bid:"+option.bid);
        mPoiSearch.searchPoiIndoor(option);


//        nearbySearch(1,"购物");



    }


}
