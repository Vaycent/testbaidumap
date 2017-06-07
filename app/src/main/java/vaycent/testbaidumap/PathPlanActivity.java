package vaycent.testbaidumap;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import vaycent.testbaidumap.EventBus.RoutePlanMsgEvent;
import vaycent.testbaidumap.Objects.Indoor;
import vaycent.testbaidumap.Objects.ResultRoutePlan;
import vaycent.testbaidumap.Utils.NoMultiClickListener;
import vaycent.testbaidumap.databinding.PathPlanActivityBinding;


/**
 * Created by vaycent on 2017/5/31.
 */

public class PathPlanActivity extends Activity{

    private PathPlanActivityBinding mBinding;
    private PathPlanViewModel viewModel;

    private Indoor mIndoorData;

    private final int START_POI_REQUESTCODE = 0;
    private final int END_POI_REQUESTCODE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_common_list, null, false);
//        setContentPanelView(binding.getRoot());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_path_plan);

        initData();

        mBinding.setModel(buildViewModel());

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
                viewModel.startName.set(name);
                viewModel.startLatLng.set(latlng);
                viewModel.startFloor.set(floor);
                break;
            case END_POI_REQUESTCODE:
                viewModel.endName.set(name);
                viewModel.endLatLng.set(latlng);
                viewModel.endFloor.set(floor);
                break;
            default:
                break;
        }


    }

    private void initData(){
        Intent fromIntent = getIntent();
        if (null != fromIntent && null != fromIntent.getParcelableExtra(Indoor.KEY_NAME))
            mIndoorData = (Indoor)fromIntent.getParcelableExtra(Indoor.KEY_NAME);
    }

    private void initLayout(){
        mBinding.activityPathPlanTvStart.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                jumpToPoiInDoorSearchActivity(START_POI_REQUESTCODE);
            }
        });
        mBinding.activityPathPlanTvEnd.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                jumpToPoiInDoorSearchActivity(END_POI_REQUESTCODE);
            }
        });
        mBinding.activityPathPlanBtnChange.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                switchPosition();

            }
        });
        mBinding.activityPathPlanBtnSearch.setOnClickListener(new NoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                planRouteInDoor(viewModel.startLatLng.get(),viewModel.startFloor.get(),viewModel.endLatLng.get(),viewModel.endFloor.get());
            }
        });

//        setupHistroyData();
    }

    private PathPlanViewModel buildViewModel(){
        viewModel = new PathPlanViewModel();
        viewModel.startName.set("我的位置");
        viewModel.startLatLng.set(new LatLng(mIndoorData.getCurrentLocation().getLatitude(),mIndoorData.getCurrentLocation().getLongitude()));
        viewModel.startFloor.set(mIndoorData.getCurrentLocation().getFloor());

        viewModel.endName.set("");
        viewModel.endLatLng.set(null);
        viewModel.endFloor.set("F1");
        return viewModel;
    }

    private void switchPosition(){
        String tempStartName = viewModel.startName.get();
        LatLng tempStartLatLng= viewModel.startLatLng.get();
        String tempStartFloor = viewModel.startFloor.get();
        viewModel.startName.set(viewModel.endName.get());
        viewModel.startLatLng.set(viewModel.endLatLng.get());
        viewModel.startFloor.set(viewModel.endFloor.get());
        viewModel.endName.set(tempStartName);
        viewModel.endLatLng.set(tempStartLatLng);
        viewModel.endFloor.set(tempStartFloor);
    }

    private void jumpToPoiInDoorSearchActivity(int requestCode){
        Intent startIntent = new Intent();
        startIntent.setClass(PathPlanActivity.this,PoiInDoorSearchActivity.class);
        startIntent.putExtra("indoorData",mIndoorData);
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
