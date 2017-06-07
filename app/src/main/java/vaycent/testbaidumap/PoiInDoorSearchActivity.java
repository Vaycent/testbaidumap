package vaycent.testbaidumap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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
import vaycent.testbaidumap.Utils.HistroySharePreference;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.PoiInDoorSearchActivityBinding;
import vaycent.testbaidumap.widget.PoiSearchAdapter;

/**
 * Created by vaycent on 2017/5/31.
 */

public class PoiInDoorSearchActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private PoiInDoorSearchActivityBinding mBinding;
    private PoiSearch navigationPoiSearch;

    private PoiSearchAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;

    private AlertUtils mAlertUtils = new AlertUtils(this);

    private Indoor mIndoorData;

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
        if (mAlertUtils.getPoiIndoorAlert(poiIndoorResult)) {
            return;
        }else{
            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            mBinding.activityPoiIndoorSearchRvMapinfolist.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new PoiSearchAdapter(this,poiInfosList,mIndoorData.getCurrentLocation());
            mBinding.activityPoiIndoorSearchRvMapinfolist.setAdapter(poiItemAdapter);
            poiItemAdapter.notifyDataSetChanged();
        }
    }







    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);


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
                multipoimapIntent.putExtra("keyWord",mBinding.activityNavigationmapEtSearchtext.getText().toString());
                startActivity(multipoimapIntent);
            }
        });
    }

    private void indoorNearBySearch(int page, String keyword) {
        PoiIndoorOption option = new PoiIndoorOption();
        option.poiIndoorBid(mIndoorData.getIndoorMapId());
        option.poiIndoorWd(keyword);

        if (option == null) {
            mAlertUtils.incorrectPoiIndoorOption();
            return;
        }
        navigationPoiSearch.searchPoiIndoor(option);
    }

    public void returnPoiIndoorInfo(PoiIndoorInfo mPoiIndoorInfo){
        HistroySharePreference mHistroySharePreference = new HistroySharePreference();
        mHistroySharePreference.save(mBinding.activityNavigationmapEtSearchtext.getText().toString().trim());

        Intent fromIntent = getIntent();
        int requestCode = fromIntent.getIntExtra("requestcode",0);
        fromIntent.putExtra("placeName", mPoiIndoorInfo.name);
        fromIntent.putExtra("placeLatLng", mPoiIndoorInfo.latLng);
        fromIntent.putExtra("placeFloor", mPoiIndoorInfo.floor);
        this.setResult(requestCode, fromIntent);
        this.finish();
    }

    private void setupHistroyData(){
//        mBinding.activityPathPlanRvHistroylist.setLayoutManager(new LinearLayoutManager(this));
//        HistroySharePreference mHistroySharePreference = new HistroySharePreference();
//        List<String> mKeyWords = mHistroySharePreference.read();
//        mPathHistroyAdapter = new PathHistroyAdapter(this,mKeyWords);
//        mBinding.activityPathPlanRvHistroylist.setAdapter(mPathHistroyAdapter);
//        mBinding.activityPathPlanRvHistroylist.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL_LIST));
//        mPathHistroyAdapter.notifyDataSetChanged();
    }
}
