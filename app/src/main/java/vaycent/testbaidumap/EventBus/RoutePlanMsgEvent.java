package vaycent.testbaidumap.EventBus;

import vaycent.testbaidumap.Objects.ResultRoutePlan;

/**
 * Created by vaycent on 2017/5/31.
 */

public class RoutePlanMsgEvent {
    private ResultRoutePlan mResultRoutePlan;

    public RoutePlanMsgEvent(ResultRoutePlan mResultRoutePlan){
        this.mResultRoutePlan = mResultRoutePlan;
    }

    public ResultRoutePlan getResultRoutePlan(){
        return mResultRoutePlan;
    }
}
