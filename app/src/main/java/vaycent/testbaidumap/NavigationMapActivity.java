package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RadioGroup;
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

import vaycent.testbaidumap.databinding.NavigationMapActivityBinding;
import vaycent.testbaidumap.widget.NavigationItemAdapter;


public class NavigationMapActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private BDLocation resultCallBackLocation = null;
    private String indoorId = "";
    private PoiSearch navigationPoiSearch;

    private NavigationMapActivityBinding mBinding;

    private String currentTab;
    private String lastTab;
    private NavigationItemAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_navigation_map);

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

        changeTabMode();

        mBinding.activityNavigationmapEtSearchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!mBinding.activityNavigationmapEtSearchtext.getText().toString().trim().equals("")){
                    indoorNearBySearch(1,mBinding.activityNavigationmapEtSearchtext.getText().toString().trim());
                    mBinding.activityNavigationmapRdContentTab.clearCheck();
                    lastTab = null;
                }
            }
        });
    }

    private void changeTabMode() {
        mBinding.activityNavigationmapRdContentTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                lastTab = currentTab;
                switch (i) {
                    case R.id.activity_navigationmap_rbtn_contentfirst:
                        currentTab = "餐饮";
                        break;
                    case R.id.activity_navigationmap_rbtn_contentsecond:
                        currentTab = "值机口";
                        break;
                    case R.id.activity_navigationmap_rbtn_contentthird:
                        currentTab = "登机口";
                        break;
                    case R.id.activity_navigationmap_rbtn_contentfourth:
                        currentTab = "到达口";
                        break;
                    case R.id.activity_navigationmap_rbtn_contentfifth:
                        currentTab = "出发厅";
                        break;
                }

                if (currentTab != lastTab) {
                    mBinding.activityNavigationmapEtSearchtext.setText("");
                    indoorNearBySearch(1, currentTab);
                }
            }
        });
    }

    private void indoorNearBySearch(int page, String keyword) {
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(indoorId);
        option.poiIndoorWd(keyword);

        if (option == null) {
            Toast.makeText(NavigationMapActivity.this, "当前无室内POI无法搜索", Toast.LENGTH_LONG).show();
            return;
        }
        navigationPoiSearch.searchPoiIndoor(option);
    }


}

