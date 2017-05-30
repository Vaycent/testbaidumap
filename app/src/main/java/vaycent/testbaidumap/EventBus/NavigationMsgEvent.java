package vaycent.testbaidumap.EventBus;

import com.baidu.location.BDLocation;

/**
 * Created by vaycent on 2017/5/28.
 */

public class NavigationMsgEvent {
    private BDLocation callbackLocation;

    public NavigationMsgEvent(BDLocation callbackLocation){
        this.callbackLocation = callbackLocation;
    }

    public BDLocation getCallbackLocation(){
        return callbackLocation;
    }
}
