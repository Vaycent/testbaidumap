package vaycent.testbaidumap.EventBus;

import com.baidu.location.BDLocation;

/**
 * Created by vaycent on 2017/5/27.
 */

public class LocationMsgEvent {

    private BDLocation callbackLocation;

    public LocationMsgEvent(BDLocation callbackLocation){
        this.callbackLocation = callbackLocation;
    }

    public BDLocation getCallbackLocation(){
        return callbackLocation;
    }
}
