package vaycent.testbaidumap.Poi;

import android.content.Context;
import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import vaycent.testbaidumap.EventBus.OnePoiMsgEvent;
import vaycent.testbaidumap.EventBus.PathPlanResultMsgEvent;
import vaycent.testbaidumap.InDoorActivity;
import vaycent.testbaidumap.PathPlanActivity;

/**
 * Created by vaycent on 2017/6/9.
 */

public class MultiPoiMapOverlay extends IndoorPoiOverlay {

    private Context context;
    private int requestCode;
    private  PoiIndoorResult poiIndoorResult;
    private BDLocation resultCallBackLocation;

    public MultiPoiMapOverlay(BaiduMap baiduMap, Context mContext, PoiIndoorResult poiIndoorResult, BDLocation resultCallBackLocation,int requestCode) {
        super(baiduMap);
        this.context = mContext;
        this.requestCode = requestCode;
        this.poiIndoorResult = poiIndoorResult;
        this.resultCallBackLocation = resultCallBackLocation;
    }

    @Override
    public boolean onPoiClick(int i) {
        PoiIndoorInfo info =  getIndoorPoiResult().getmArrayPoiInfo().get(i);

        if(0==requestCode){
            List<PoiIndoorInfo> mListPoiIndoorInfo = new ArrayList<PoiIndoorInfo>();
            mListPoiIndoorInfo.add(info);
            PoiIndoorResult mPoiIndoorResult = new PoiIndoorResult();
            mPoiIndoorResult.setPageNum(1);
            mPoiIndoorResult.setPoiNum(1);
            mPoiIndoorResult.setmArrayPoiInfo(mListPoiIndoorInfo);
            EventBus.getDefault().post(new OnePoiMsgEvent(mPoiIndoorResult,resultCallBackLocation));
            Intent indoorIntent = new Intent();
            indoorIntent.setClass(context,InDoorActivity.class);
            context.startActivity(indoorIntent);
        }else{
            EventBus.getDefault().post(new PathPlanResultMsgEvent(info,requestCode));
            Intent intent = new Intent();
            intent.setClass(context, PathPlanActivity.class);
            context.startActivity(intent);
        }
        return false;
    }

}