package vaycent.testbaidumap.Utils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * Created by vaycent on 2017/2/20.
 */

public class MapUtils {

    public void mapMoveTo(BaiduMap mBaiduMap,double lat, double lon){
        //控制地图镜头开始位置
        LatLng centerpos = new LatLng(lat,lon); // 西单大悦城
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerpos).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public int calculateDistance(LatLng currentLatLng,LatLng targetLatLng){
        int distance = (int)(DistanceUtil.getDistance(currentLatLng, targetLatLng));
        return distance;
    }


}
