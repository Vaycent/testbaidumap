package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import vaycent.testbaidumap.databinding.PoiInDoorSearchActivityBinding;
import vaycent.testbaidumap.widget.PoiSearchAdapter;

/**
 * Created by vaycent on 2017/5/31.
 */

public class PoiInDoorSearchActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private PoiInDoorSearchActivityBinding mBinding;
    private BDLocation resultCallBackLocation = null;
    private String indoorId = "";
    private PoiSearch navigationPoiSearch;

    private PoiSearchAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_poi_indoor_search);

        initData();

        initLayout();

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
            Toast.makeText(PoiInDoorSearchActivity.this, "无室内搜索结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (poiIndoorResult.error == PoiIndoorResult.ERRORNO.NO_ERROR) {
            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            mBinding.activityPoiIndoorSearchRvMapinfolist.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new PoiSearchAdapter(this,poiInfosList,resultCallBackLocation);
            mBinding.activityPoiIndoorSearchRvMapinfolist.setAdapter(poiItemAdapter);
            poiItemAdapter.notifyDataSetChanged();
        }
    }







    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra("callbackLocation"))
            resultCallBackLocation = fromIntent.getParcelableExtra("callbackLocation");
        if (null != fromIntent && null != fromIntent.getStringExtra("indoorId"))
            indoorId = fromIntent.getStringExtra("indoorId");

        navigationPoiSearch = navigationPoiSearch.newInstance();
        navigationPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initLayout(){
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
                }
            }
        });

        mBinding.activityPoiIndoorSearchBtnStartsearch.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                Intent multipoimapIntent = new Intent();
                multipoimapIntent.setClass(PoiInDoorSearchActivity.this,MultiPoiMapActivity.class);
                startActivity(multipoimapIntent);
            }
        });
    }

    private void indoorNearBySearch(int page, String keyword) {
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(indoorId);
        option.poiIndoorWd(keyword);

        if (option == null) {
            Toast.makeText(PoiInDoorSearchActivity.this, "当前无室内POI无法搜索", Toast.LENGTH_LONG).show();
            return;
        }
        navigationPoiSearch.searchPoiIndoor(option);
    }

    public void returnPoiIndoorInfo(PoiIndoorInfo mPoiIndoorInfo){
        Intent fromIntent = getIntent();
        int requestCode = fromIntent.getIntExtra("requestcode",0);
        fromIntent.putExtra("placeName", mPoiIndoorInfo.name);
        fromIntent.putExtra("placeLatLng", mPoiIndoorInfo.latLng);
        fromIntent.putExtra("placeFloor", mPoiIndoorInfo.floor);
        this.setResult(requestCode, fromIntent);
        this.finish();
    }
}
