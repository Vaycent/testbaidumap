package vaycent.testbaidumap.EventBus;

import com.baidu.mapapi.search.poi.PoiIndoorResult;

/**
 * Created by vaycent on 2017/5/31.
 */

public class OnePoiMsgEvent {
    private PoiIndoorResult mPoiIndoorResult;

    public OnePoiMsgEvent(PoiIndoorResult mPoiIndoorResult){
        this.mPoiIndoorResult = mPoiIndoorResult;
    }

    public PoiIndoorResult getPoiIndoorResult(){
        return mPoiIndoorResult;
    }
}
