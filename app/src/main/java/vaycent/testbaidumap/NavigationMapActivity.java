package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.Objects.Indoor;
import vaycent.testbaidumap.Utils.AlertUtils;
import vaycent.testbaidumap.Utils.MapUtils;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.IncludeSearchBinding;
import vaycent.testbaidumap.databinding.NavigationMapActivityBinding;
import vaycent.testbaidumap.widget.DividerItemDecoration;
import vaycent.testbaidumap.widget.NavigationItemAdapter;


public class NavigationMapActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {
    private Indoor mIndoorData;
    private ArrayList<String> floors;
    private PoiSearch navigationPoiSearch;

    private NavigationMapActivityBinding mBinding;
    private IncludeSearchBinding includeSearchBinding;

    private NavigationItemAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;
    private String searchType,searchFloor;

    private AlertUtils mAlertUtils = new AlertUtils(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_navigation_map);
        includeSearchBinding = DataBindingUtil.getBinding(mBinding.activityNavigationmapIncludeSearch.getRoot());

        initData();

        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new NavigationItemAdapter(this,poiInfosList,mIndoorData.getCurrentLocation());
            mBinding.activityNavigationmapRvCommonflightlistview.setAdapter(poiItemAdapter);

            mBinding.activityNavigationmapRvCommonflightlistview.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL_LIST));
            poiItemAdapter.notifyDataSetChanged();
        }
    }




    private void initData() {
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);


        floors = mIndoorData.getFloorsList();
        floors = floorsFormat(floors);

        searchType = "南航";
        searchFloor = "全部";

        navigationPoiSearch = navigationPoiSearch.newInstance();
        navigationPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initLayout() {
        initTabLayout();

//        includeSearchBinding.includeIndoorSearchEtSearchtext.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(!includeSearchBinding.includeIndoorSearchEtSearchtext.getText().toString().trim().equals("")){
//                    indoorNearBySearch(1,includeSearchBinding.includeIndoorSearchEtSearchtext.getText().toString().trim(),searchFloor);
//                    lastTab = null;
//                }
//            }
//        });

        includeSearchBinding.includeIndoorSearchRlytBack.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                onBackPressed();
            }
        });

        includeSearchBinding.includeIndoorSearchEtSearchlayout.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                Intent hotsPoiIntent = new Intent();
                hotsPoiIntent.setClass(NavigationMapActivity.this,HotsPoiActivity.class);
                startActivity(hotsPoiIntent);
            }
        });

    }

    private void initTabLayout(){
        mBinding.activityNavigationmapTlContentTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mBinding.activityNavigationmapTlContentTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("南航"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("餐饮"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("值机口"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("登机口"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("到达口"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("出发厅"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("安全检查"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("候机厅"));

        // 设置Tab的选择监听
        mBinding.activityNavigationmapTlContentTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchType = tab.getText().toString();
                indoorNearBySearch(1, searchType,searchFloor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
//        TabLayout.TabLayoutOnPageChangeListener listener =
//                new TabLayout.TabLayoutOnPageChangeListener(mBinding.activityNavigationmapTlContentTab);


        for(String floor : floors){
            RadioButton radioButton = new RadioButton(this);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(MapUtils.dip2px(this,50), MapUtils.dip2px(this,30));
            layoutParams.setMargins(MapUtils.dip2px(this,10), MapUtils.dip2px(this,10), MapUtils.dip2px(this,10), MapUtils.dip2px(this,10));
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(floor);
//            radioButton.setTextSize(12);
            radioButton.setButtonDrawable(android.R.color.transparent);//隐藏单选圆形按钮
            radioButton.setGravity(Gravity.CENTER);
//            radioButton.setTextColor(getResources().getColor(R.color.MAIN));
//            radioButton.setPadding(10, 10, 10, 10);
            radioButton.setBackgroundResource(R.drawable.btn_cir_selector);//设置按钮选中/未选中的背景
            mBinding.activityNavigationmapRgFloorsTab.addView(radioButton);//将单选按钮添加到RadioGroup中
            if(radioButton.getText().equals("全部")){
                radioButton.setChecked(true);
            }
        }
        mBinding.activityNavigationmapRgFloorsTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                searchFloor = rb.getText().toString();
                indoorNearBySearch(1, searchType,searchFloor);
            }
        });


        indoorNearBySearch(1, searchType,searchFloor);
    }

    private void indoorNearBySearch(int page, String keyword,String floor) {
        if("南航".equals(keyword)){
            keyword = "南方航空";
        }
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

    private ArrayList<String> floorsFormat(ArrayList<String> floors){
        ArrayList<String> resultFloors = floors;
        resultFloors.add(0,"全部");
        return resultFloors;
    }


}

