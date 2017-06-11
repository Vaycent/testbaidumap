package vaycent.testbaidumap.EventBus;

import com.baidu.mapapi.search.poi.PoiIndoorInfo;

/**
 * Created by vaycent on 2017/6/9.
 */

public class PathPlanResultMsgEvent {
    private PoiIndoorInfo mPoiIndoorInfo;
    private int requestCode;

    public PathPlanResultMsgEvent(PoiIndoorInfo mPoiIndoorInfo,int requestCode){
        this.mPoiIndoorInfo = mPoiIndoorInfo;
        this.requestCode = requestCode;
    }

    public PoiIndoorInfo getPoiIndoorInfo(){
        return this.mPoiIndoorInfo;
    }

    public int getRequestCode(){
        return this.requestCode;
    }
}
