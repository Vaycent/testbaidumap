package vaycent.testbaidumap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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


public class IndoorSearchActivity extends Activity implements OnGetPoiSearchResultListener,
        BaiduMap.OnBaseIndoorMapListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch = null;
    private Button searchBtn,testBtn;
    EditText searchContent;
    StripListView stripView;
    BaseStripAdapter mFloorListAdapter;
    MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;


    private final double testLat = MyMapHelper.beijingNanLat;
    private final double testLon = MyMapHelper.beijingNanLon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_search);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();


        MapUtils.mapMoveTo(mBaiduMap,testLat,testLon); // 北京南站

     //   stripView = (StripListView) findViewById(R.id.stripView);
        stripView = new StripListView(this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.viewStub);
        layout.addView(stripView);
        mFloorListAdapter = new BaseStripAdapter(this);
        mBaiduMap.setOnBaseIndoorMapListener(this);
        mBaiduMap.setIndoorEnable(true); // 设置打开室内图开关

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        searchContent = (EditText) findViewById(R.id.activity_indoor_search_et_end);
        searchBtn = (Button) findViewById(R.id.activity_indoor_search_btn_indoorsearch);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
                if (indoorInfo == null) {
                    Toast.makeText(IndoorSearchActivity.this, "当前无室内图，无法搜索" , Toast.LENGTH_LONG).show();
                    return;
                }
                PoiIndoorOption option = new PoiIndoorOption().poiIndoorBid(
                        indoorInfo.getID()).poiIndoorWd(searchContent.getText().toString());
                mPoiSearch.searchPoiIndoor( option);
            }
        });

        stripView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

//    @Override
//    public void onGetPoiResult(PoiResult result) {
//
//    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }

    /**
     * 获取室内图搜索结果，得到searchPoiIndoor返回的结果
     * @param result
     */
    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult result) {
        mBaiduMap.clear();
        if (result == null  || result.error == PoiIndoorResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(IndoorSearchActivity.this, "无结果" , Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("Vaycent","result.getmArrayPoiInfo().size:"+result.getmArrayPoiInfo().size());
        for(int i=0;i<result.getmArrayPoiInfo().size();i++){
            Log.e("Vaycent","address:"+result.getmArrayPoiInfo().get(i).name);
        }

        if (result.error == PoiIndoorResult.ERRORNO.NO_ERROR) {
            IndoorPoiOverlay overlay = new MyIndoorPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

        }
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
            Toast.makeText(IndoorSearchActivity.this, info.name + ",在" + info.floor + "层,坐标:"+info.latLng.latitude+","+info.latLng.longitude, Toast.LENGTH_LONG).show();
            return false;
        }

    }




    //周边
    private void searchNeayBy(){
//         POI初始化搜索模块，注册搜索事件监听
//        mPoiSearch = PoiSearch.newInstance();
//        mPoiSearch.setOnGetPoiSearchResultListener(this);
//        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption();
//
////        poiNearbySearchOption.keyword("室内");
//        poiNearbySearchOption.location(new LatLng(39.871281, 116.385306));
//        poiNearbySearchOption.radius(500);  // 检索半径，单位是米
//        poiNearbySearchOption.pageCapacity(20);  // 默认每页10条
//        mPoiSearch.searchNearby(poiNearbySearchOption);  // 发起附近检索请求



        MapBaseIndoorMapInfo indoorInfo = mBaiduMap.getFocusedBaseIndoorMapInfo();
        if (indoorInfo == null) {
            Toast.makeText(IndoorSearchActivity.this, "当前无室内图，无法搜索" , Toast.LENGTH_LONG).show();
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
            Toast.makeText(IndoorSearchActivity.this, "当前无室内POI无法搜索" , Toast.LENGTH_LONG).show();
            return;
        }
//        Log.e("Vaycent","option.bid:"+option.bid);
        mPoiSearch.searchPoiIndoor(option);


//        nearbySearch(1,"购物");



    }
    @Override
    public void onGetPoiResult(PoiResult result) {
        // 获取POI检索结果
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(IndoorSearchActivity.this, "未找到结果",Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
//          mBaiduMap.clear();
            if(result != null){
                if(result.getAllPoi()!= null && result.getAllPoi().size()>0){
                    Log.e("Vaycent","result.getAllPoi() size:"+result.getAllPoi());
                    for(int i=0;i<result.getAllPoi().size();i++){
                        PoiInfo info = result.getAllPoi().get(i);
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


}
