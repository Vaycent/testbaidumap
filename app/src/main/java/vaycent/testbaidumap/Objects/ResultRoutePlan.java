package vaycent.testbaidumap.Objects;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by vaycent on 2017/5/31.
 */

public class ResultRoutePlan {
    public LatLng startLatLng;
    public String startFloor;
    public LatLng endLatLng;
    public String endFloor;

    public ResultRoutePlan(LatLng startLatLng, String startFloor, LatLng endLatLng, String endFloor){
        this.startLatLng = startLatLng;
        this.startFloor = startFloor;
        this.endLatLng = endLatLng;
        this.endFloor = endFloor;
    }

}
