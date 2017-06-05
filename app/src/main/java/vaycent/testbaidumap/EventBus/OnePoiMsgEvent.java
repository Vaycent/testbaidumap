package vaycent.testbaidumap.EventBus;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.poi.PoiIndoorResult;

/**
 * Created by vaycent on 2017/5/31.
 */

public class OnePoiMsgEvent {
    private PoiIndoorResult mPoiIndoorResult;
    private BDLocation mCallBackLocation;

    public OnePoiMsgEvent(PoiIndoorResult mPoiIndoorResult,BDLocation resultCallBackLocation){
        this.mPoiIndoorResult = mPoiIndoorResult;
        this.mCallBackLocation = resultCallBackLocation;
    }

    public PoiIndoorResult getPoiIndoorResult(){
        return mPoiIndoorResult;
    }

    public BDLocation getCallBackLocation(){
        return mCallBackLocation;
    }

}
