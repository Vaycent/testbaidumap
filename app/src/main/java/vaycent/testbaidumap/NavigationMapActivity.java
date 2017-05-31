package vaycent.testbaidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.EventBus.OnePoiMsgEvent;
import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.widget.PoiItemAdapter;


public class NavigationMapActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private BDLocation resultCallBackLocation = null;
    private String indoorId = "";

    private RadioGroup contentRadioGroup;
    private String currentTab;
    private String lastTab;
    private RecyclerView poiListView;
    private PoiItemAdapter poiItemAdapter;
    private List<PoiIndoorInfo> poiInfosList;
    //Poi Search
    private PoiSearch navigationPoiSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_map);

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

        Log.e("Vaycent", "result.getmArrayPoiInfo().size:" + poiIndoorResult.getmArrayPoiInfo().size());
        for (int i = 0; i < poiIndoorResult.getmArrayPoiInfo().size(); i++) {
            Log.e("Vaycent", "name:" + poiIndoorResult.getmArrayPoiInfo().get(i).name);
        }

        if (poiIndoorResult.error == PoiIndoorResult.ERRORNO.NO_ERROR) {
            if(null == poiInfosList)
                poiInfosList= new ArrayList<PoiIndoorInfo>();

            poiListView.setLayoutManager(new LinearLayoutManager(this));
            poiInfosList.clear();
            poiInfosList.addAll(poiIndoorResult.getmArrayPoiInfo());//每个点的超详细信息

            poiItemAdapter = new PoiItemAdapter(this,poiInfosList,resultCallBackLocation);
            poiListView.setAdapter(poiItemAdapter);
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
        contentRadioGroup = (RadioGroup) findViewById(R.id.activity_navigationmap_rd_content_tab);
        changeTabMode();
        poiListView = (RecyclerView) findViewById(R.id.activity_navigationmap_rv_commonflightlistview);

    }

    private void changeTabMode() {
        contentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

                //根据选择的tab进行查询
                if (currentTab != lastTab) {
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

    public void returnResultOnePoi(PoiIndoorResult resultPoiIndoorInfo){
        EventBus.getDefault().post(new OnePoiMsgEvent(resultPoiIndoorInfo));
        this.finish();
    }

    public void returnResultRoutePlan(ResultRoutePlan mResultRoutePlan){
        EventBus.getDefault().post(new RoutePlanMsgEvent(mResultRoutePlan));
        this.finish();
    }


}

