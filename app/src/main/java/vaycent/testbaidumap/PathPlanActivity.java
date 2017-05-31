package vaycent.testbaidumap;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.baidu.location.BDLocation;

import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.PathPlanActivityBinding;

/**
 * Created by vaycent on 2017/5/31.
 */

public class PathPlanActivity extends Activity{

    private PathPlanActivityBinding mBinding;

    private BDLocation resultCallBackLocation = null;
    private String indoorId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_path_plan);

        initData();

        initLayout();

    }

    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra("callbackLocation"))
            resultCallBackLocation = fromIntent.getParcelableExtra("callbackLocation");
        if (null != fromIntent && null != fromIntent.getStringExtra("indoorId"))
            indoorId = fromIntent.getStringExtra("indoorId");
    }

    private void initLayout(){
        mBinding.activityPathPlanEdStart.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                //跳转
            }
        });
        mBinding.activityPathPlanEdEnd.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                //跳转
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
//                LatLng startLatLng = new LatLng(resultCallBackLocation.getLatitude(),resultCallBackLocation.getLongitude());
//                String startFloor = resultCallBackLocation.getFloor();
//                LatLng endLatLng = mPoiIndoorInfoDatas.get(position).latLng;
//                String endFloor = mPoiIndoorInfoDatas.get(position).floor;
//
//                ResultRoutePlan mResultRoutePlan = new ResultRoutePlan(startLatLng,startFloor,endLatLng,endFloor);
//                activity.returnResultRoutePlan(mResultRoutePlan);
            }
        });
    }

}
