package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.IncludeSearchBinding;
import vaycent.testbaidumap.databinding.NavigationMapActivityBinding;
import vaycent.testbaidumap.widget.DividerItemDecoration;
import vaycent.testbaidumap.widget.NavigationItemAdapter;


public class NavigationMapActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private BDLocation resultCallBackLocation = null;
    private String indoorId = "";
    private PoiSearch navigationPoiSearch;

    private NavigationMapActivityBinding mBinding;
    private IncludeSearchBinding includeSearchBinding;

    private String currentTab;
    private String lastTab;
    private NavigationItemAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;


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
        if (poiIndoorResult == null || poiIndoorResult.error == PoiIndoorResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(NavigationMapActivity.this, "无室内搜索结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (poiIndoorResult.error == PoiIndoorResult.ERRORNO.NO_ERROR) {
            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            mBinding.activityNavigationmapRvCommonflightlistview.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new NavigationItemAdapter(this,poiInfosList,resultCallBackLocation);
            mBinding.activityNavigationmapRvCommonflightlistview.setAdapter(poiItemAdapter);

            mBinding.activityNavigationmapRvCommonflightlistview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
            poiItemAdapter.notifyDataSetChanged();
        }
    }




    private void initData() {
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra("callbackLocation"))
            resultCallBackLocation = fromIntent.getParcelableExtra("callbackLocation");
        if (null != fromIntent && null != fromIntent.getStringExtra("indoorId"))
            indoorId = fromIntent.getStringExtra("indoorId");

        navigationPoiSearch = navigationPoiSearch.newInstance();
        navigationPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initLayout() {
        initTabLayout();

        includeSearchBinding.includeIndoorSearchEtSearchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!includeSearchBinding.includeIndoorSearchEtSearchtext.getText().toString().trim().equals("")){
                    indoorNearBySearch(1,includeSearchBinding.includeIndoorSearchEtSearchtext.getText().toString().trim());
                    lastTab = null;
                }
            }
        });

        includeSearchBinding.includeIndoorSearchRlytBack.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initTabLayout(){
        mBinding.activityNavigationmapTlContentTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mBinding.activityNavigationmapTlContentTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("餐饮"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("值机口"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("登机口"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("到达口"));

        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("出发厅"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("安全检查"));
        mBinding.activityNavigationmapTlContentTab.addTab(mBinding.activityNavigationmapTlContentTab.newTab().setText("候机厅"));

        indoorNearBySearch(1, "餐饮");
        // 设置Tab的选择监听
        mBinding.activityNavigationmapTlContentTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                includeSearchBinding.includeIndoorSearchEtSearchtext.setText("");
                indoorNearBySearch(1, tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // 构造一个TabLayoutOnPageChangeListener对象
        TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(mBinding.activityNavigationmapTlContentTab);
    }


    private void indoorNearBySearch(int page, String keyword) {
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(indoorId);
        option.poiIndoorWd(keyword);
        option.poiPageSize(50);

        if (option == null) {
            Toast.makeText(NavigationMapActivity.this, "当前无室内POI无法搜索", Toast.LENGTH_LONG).show();
            return;
        }
        navigationPoiSearch.searchPoiIndoor(option);
    }


}

