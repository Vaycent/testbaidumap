package vaycent.testbaidumap;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.Objects.ResultPoiSearch;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.PathPlanActivityBinding;

/**
 * Created by vaycent on 2017/5/31.
 */

public class PathPlanActivity extends Activity{

    private PathPlanActivityBinding mBinding;

    private BDLocation callbackLocation = null;
    private String indoorId = "";

    private final int START_POI_REQUESTCODE = 0;
    private final int END_POI_REQUESTCODE = 1;
    private ResultPoiSearch startResultPoiSearch, endResultPoiSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_path_plan);

        initData();

        initLayout();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(null==data)
            return;
        String name = data.getStringExtra("placeName");
        LatLng latlng = data.getParcelableExtra("placeLatLng");
        String floor = data.getStringExtra("placeFloor");
        switch (requestCode) {
            case START_POI_REQUESTCODE:
                startResultPoiSearch.name = name;
                startResultPoiSearch.latlng = latlng;
                startResultPoiSearch.floor = floor;
//                mBinding.activityPathPlanEdStart.setText(name);
                break;
            case END_POI_REQUESTCODE:
                endResultPoiSearch.name = name;
                endResultPoiSearch.latlng = latlng;
                endResultPoiSearch.floor = floor;
//                mBinding.activityPathPlanEdEnd.setText(name);
                break;
            default:
                break;
        }
        mBinding.setStartPoiSearch(startResultPoiSearch);
        mBinding.setEndPoiSearch(endResultPoiSearch);
    }

    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra("callbackLocation"))
            callbackLocation = fromIntent.getParcelableExtra("callbackLocation");
        if (null != fromIntent && null != fromIntent.getStringExtra("indoorId"))
            indoorId = fromIntent.getStringExtra("indoorId");

        startResultPoiSearch = new ResultPoiSearch("我的位置",new LatLng(callbackLocation.getLatitude(),callbackLocation.getLongitude()),callbackLocation.getFloor());
        endResultPoiSearch = new ResultPoiSearch("",null,"");
        mBinding.setStartPoiSearch(startResultPoiSearch);
        mBinding.setEndPoiSearch(endResultPoiSearch);

    }

    private void initLayout(){
        mBinding.activityPathPlanEdStart.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                swithToPoiInDoorSearchActivity(START_POI_REQUESTCODE);
            }
        });
        mBinding.activityPathPlanEdEnd.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                swithToPoiInDoorSearchActivity(END_POI_REQUESTCODE);
            }
        });
        mBinding.activityPathPlanBtnChange.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                String temp = mBinding.activityPathPlanEdStart.getText().toString().trim();
                mBinding.activityPathPlanEdStart.setText(mBinding.activityPathPlanEdEnd.getText().toString().trim());
                mBinding.activityPathPlanEdEnd.setText(temp);
            }
        });
        mBinding.activityPathPlanBtnSearch.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                planRouteInDoor(startResultPoiSearch.latlng,startResultPoiSearch.floor,endResultPoiSearch.latlng,endResultPoiSearch.floor);
            }
        });
    }

    private void swithToPoiInDoorSearchActivity(int requestCode){
        Intent startIntent = new Intent();
        startIntent.setClass(PathPlanActivity.this,PoiInDoorSearchActivity.class);
        startIntent.putExtra("callbackLocation",callbackLocation);
        startIntent.putExtra("indoorId",indoorId);
        startIntent.putExtra("requestcode",requestCode);
        startActivityForResult(startIntent,requestCode);
    }

    private void planRouteInDoor(LatLng startLatLng,String startFloor,LatLng endLatLng,String endFloor){
            ResultRoutePlan mResultRoutePlan = new ResultRoutePlan(startLatLng,startFloor,endLatLng,endFloor);
            EventBus.getDefault().post(new RoutePlanMsgEvent(mResultRoutePlan));
            backToInDoorActivity();
    }

    private void backToInDoorActivity(){
        Intent indoorIntent = new Intent();
        indoorIntent.setClass(this, InDoorActivity.class);
        this.startActivity(indoorIntent);
    }

}
