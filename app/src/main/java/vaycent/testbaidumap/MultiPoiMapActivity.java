package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.Adapter.MultiPoiMapAdapter;
import vaycent.testbaidumap.Objects.Indoor;
import vaycent.testbaidumap.Utils.AlertUtils;
import vaycent.testbaidumap.Utils.FormatUtils;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.databinding.MultiPoiMapActivityBinding;
import vaycent.testbaidumap.widget.DividerItemDecoration;

public class MultiPoiMapActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private MultiPoiMapActivityBinding mBinding;
    private String keyWord;
    private Indoor mIndoorData;
    private int requestCodeFrom;

    private PoiSearch navigationPoiSearch;
    private String searchFloor;
    private List<PoiIndoorInfo> poiInfosList;
    private MultiPoiMapAdapter mMultiPoiMapAdapter;

    private BaiduMap mBaiduMap;
    private MapUtils mapUtilsHelper = new MapUtils();
    private AlertUtils mAlertUtils = new AlertUtils(this);
    private FormatUtils mFormatUtils = new FormatUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_multi_poi_map);

        initData();

        initMap();

        initTabLayout();

    }

    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);
        if (null != fromIntent && null != fromIntent.getStringExtra("keyWord"))
            keyWord = fromIntent.getStringExtra("keyWord");
        if (null != fromIntent)
            requestCodeFrom = fromIntent.getIntExtra("requestCode",0);

        searchFloor = "全部";

        navigationPoiSearch = navigationPoiSearch.newInstance();
        navigationPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initMap(){
        mBaiduMap = mBinding.activityMultiPoiMapMvSmallmap.getMap();
        mBaiduMap.setIndoorEnable(true); // 打开室内图
        mapUtilsHelper.isShowBaiDuLogo(mBinding.activityMultiPoiMapMvSmallmap,false);
        mapUtilsHelper.isShowMapScale(mBinding.activityMultiPoiMapMvSmallmap,false);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mapUtilsHelper.mapMoveTo(mBaiduMap,mIndoorData.getAirportLatLng().latitude,mIndoorData.getAirportLatLng().longitude );
            }
        });
    }

    private void initTabLayout(){
        ArrayList<String> floors = mFormatUtils.floorsFormat(mIndoorData.getFloorsList());
        for(String floor : floors){
            RadioButton radioButton = new RadioButton(this);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(MapUtils.dip2px(this,48), MapUtils.dip2px(this,16));
            layoutParams.setMargins(MapUtils.dip2px(this,10), MapUtils.dip2px(this,10), MapUtils.dip2px(this,10), MapUtils.dip2px(this,10));
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(floor);
            radioButton.setTextSize(12);
            radioButton.setButtonDrawable(android.R.color.transparent);//隐藏单选圆形按钮
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextColor(getResources().getColorStateList(R.color.tc_indoor_floors_item));
            radioButton.setBackgroundResource(R.drawable.bg_indoor_floors_selector);//设置按钮选中/未选中的背景
            mBinding.activityMultiPoiMapRgFloorsTab.addView(radioButton);//将单选按钮添加到RadioGroup中
            if(radioButton.getText().equals("全部")){
                radioButton.setChecked(true);
            }
        }
        mBinding.activityMultiPoiMapRgFloorsTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                searchFloor = rb.getText().toString();
                indoorNearBySearch(1, keyWord,searchFloor);
            }
        });

        indoorNearBySearch(1, keyWord,searchFloor);
    }

    private void indoorNearBySearch(int page, String keyword,String floor) {
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(mIndoorData.getIndoorMapId());
        option.poiIndoorWd(keyword);
        option.poiPageSize(50);
        if(null!=floor&&!"".equals(floor)&&!"全部".equals(floor)){
            option.poiFloor(floor);
        }

        if (option == null) {
            mAlertUtils.incorrectPoiIndoorOption();
            return;
        }
        navigationPoiSearch.searchPoiIndoor(option);
    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        if (mAlertUtils.getPoiIndoorAlert(poiIndoorResult)){
            return;
        }else{
            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            mBinding.activityNavigationmapRvCommonflightlistview.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());
            poiInfosList = mFormatUtils.poiIndoorResultFormat(poiInfosList,searchFloor);

            mMultiPoiMapAdapter = new MultiPoiMapAdapter(this,poiInfosList,mIndoorData.getCurrentLocation(),requestCodeFrom);
            mBinding.activityNavigationmapRvCommonflightlistview.setAdapter(mMultiPoiMapAdapter);

            mBinding.activityNavigationmapRvCommonflightlistview.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL_LIST));
            mMultiPoiMapAdapter.notifyDataSetChanged();

            poiIndoorResult.setmArrayPoiInfo(poiInfosList);
            mapUtilsHelper.drawIndoorMultiPoi(mBaiduMap,this,poiIndoorResult,mIndoorData.getCurrentLocation(),requestCodeFrom);
        }
    }
}
