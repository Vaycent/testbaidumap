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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.Adapter.PathHistroyAdapter;
import vaycent.testbaidumap.Adapter.PoiSearchAdapter;
import vaycent.testbaidumap.EventBus.OnePoiMsgEvent;
import vaycent.testbaidumap.EventBus.PathPlanResultMsgEvent;
import vaycent.testbaidumap.Objects.Indoor;
import vaycent.testbaidumap.Utils.AlertUtils;
import vaycent.testbaidumap.Utils.HistroySharePreference;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.PoiInDoorSearchActivityBinding;
import vaycent.testbaidumap.widget.DividerItemDecoration;

/**
 * Created by vaycent on 2017/5/31.
 */

public class PoiInDoorSearchActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private PoiInDoorSearchActivityBinding mBinding;
    private PoiSearch navigationPoiSearch;

    private PoiSearchAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;
    private PathHistroyAdapter mPathHistroyAdapter;

    private AlertUtils mAlertUtils = new AlertUtils(this);
    private Indoor mIndoorData;
    private int requestCode;


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
            if(null != poiInfosList){
                poiInfosList.clear();
                poiItemAdapter.notifyDataSetChanged();
            }
            return;
        }else{
            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            mBinding.activityPoiIndoorSearchRvMapinfolist.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new PoiSearchAdapter(this,poiInfosList,mIndoorData.getCurrentLocation(),requestCode);
            mBinding.activityPoiIndoorSearchRvMapinfolist.setAdapter(poiItemAdapter);
            poiItemAdapter.notifyDataSetChanged();

        }

    }







    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);
        requestCode = fromIntent.getIntExtra("requestCode",0);

        navigationPoiSearch = navigationPoiSearch.newInstance();
        navigationPoiSearch.setOnGetPoiSearchResultListener(this);

        if(requestCode != 0)
            mBinding.activityPoiIndoorSearchLlytChooseonmap.setVisibility(View.VISIBLE);
    }

    private void initLayout(){
        mBinding.activityPoiIndoorSearchEtSearchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                indoorNearBySearch(1,mBinding.activityPoiIndoorSearchEtSearchtext.getText().toString().trim());

            }
        });

        mBinding.activityPoiIndoorSearchBtnStartsearch.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                if(!"".equals(mBinding.activityPoiIndoorSearchEtSearchtext.getText().toString().trim())){
                    saveToHistory();
                    Intent multipoimapIntent = new Intent();
                    multipoimapIntent.setClass(PoiInDoorSearchActivity.this,MultiPoiMapActivity.class);
                    multipoimapIntent.putExtra("keyWord",mBinding.activityPoiIndoorSearchEtSearchtext.getText().toString());
                    multipoimapIntent.putExtra(mIndoorData.KEY_NAME,mIndoorData);
                    multipoimapIntent.putExtra("requestCode",requestCode);
                    startActivity(multipoimapIntent);
                }
            }
        });

        mBinding.activityPoiIndoorSearchLlytChooseonmap.setOnClickListener(new NoMultiClickListener() {
            @Override
            protected void onNoMultiClick(View v) {
                Intent chooseOnMapIntent = new Intent();
                chooseOnMapIntent.setClass(PoiInDoorSearchActivity.this,ChooseOnMapActivity.class);
                chooseOnMapIntent.putExtra(mIndoorData.KEY_NAME,mIndoorData);
                chooseOnMapIntent.putExtra("requestCode",requestCode);
                startActivity(chooseOnMapIntent);
            }
        });

        setupHistroyData();
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

    private void setupHistroyData(){
        mBinding.activityPoiIndoorSearchRvMapinfolist.setLayoutManager(new LinearLayoutManager(this));
        HistroySharePreference mHistroySharePreference = new HistroySharePreference();
        List<String> mKeyWords = mHistroySharePreference.read();
        mPathHistroyAdapter = new PathHistroyAdapter(this,mKeyWords);
        mBinding.activityPoiIndoorSearchRvMapinfolist.setAdapter(mPathHistroyAdapter);
        mBinding.activityPoiIndoorSearchRvMapinfolist.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mPathHistroyAdapter.notifyDataSetChanged();
    }

    private void saveToHistory(){
        HistroySharePreference mHistroySharePreference = new HistroySharePreference();
        mHistroySharePreference.save(mBinding.activityPoiIndoorSearchEtSearchtext.getText().toString().trim());
    }

    public void returnSearchResult(PoiIndoorInfo mPoiIndoorInfo){
        saveToHistory();
        if(requestCode == 0){
            List<PoiIndoorInfo> mListPoiIndoorInfo = new ArrayList<PoiIndoorInfo>();
            mListPoiIndoorInfo.add(mPoiIndoorInfo);
            PoiIndoorResult mPoiIndoorResult = new PoiIndoorResult();
            mPoiIndoorResult.setPageNum(1);
            mPoiIndoorResult.setPoiNum(1);
            mPoiIndoorResult.setmArrayPoiInfo(mListPoiIndoorInfo);
            EventBus.getDefault().post(new OnePoiMsgEvent(mPoiIndoorResult,mIndoorData.getCurrentLocation()));
            Intent indoorIntent = new Intent();
            indoorIntent.setClass(PoiInDoorSearchActivity.this,InDoorActivity.class);
            startActivity(indoorIntent);
        }else{
//            fromIntent.putExtra("placeName", mPoiIndoorInfo.name);
//            fromIntent.putExtra("placeLatLng", mPoiIndoorInfo.latLng);
//            fromIntent.putExtra("placeFloor", mPoiIndoorInfo.floor);
//            this.setResult(requestCode, fromIntent);
//            this.finish();
            EventBus.getDefault().post(new PathPlanResultMsgEvent(mPoiIndoorInfo,requestCode));
            Intent intent = new Intent();
            intent.setClass(this, PathPlanActivity.class);
            startActivity(intent);
        }
    }

    public void refreshSearchText(String refreshStr){
        mBinding.activityPoiIndoorSearchEtSearchtext.setText(refreshStr);
    }
}
