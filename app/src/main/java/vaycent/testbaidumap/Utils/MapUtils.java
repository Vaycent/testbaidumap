package vaycent.testbaidumap.Utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * Created by vaycent on 2017/2/20.
 */

public class MapUtils {

    public void mapMoveTo(BaiduMap mBaiduMap,double lat, double lon){
        //控制地图镜头开始位置
        LatLng centerpos = new LatLng(lat,lon);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerpos).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public int calculateDistance(LatLng currentLatLng,LatLng targetLatLng){
        int distance = (int)(DistanceUtil.getDistance(currentLatLng, targetLatLng));
        return distance;
    }

    public void isShowBaiDuLogo(MapView mapView,boolean isShow){
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            if(!isShow)
                child.setVisibility(View.INVISIBLE);
            else
                child.setVisibility(View.VISIBLE);
        }
    }

    public void isShowMapScale(MapView mapView,boolean isShow){
        mapView.showScaleControl(isShow);
    }

    public void setZoomWidgetPosition(MapView mapView,int left, int top, int right, int bottom){
        mapView.getChildAt(2).setPadding(left,top, right,bottom);
    }


}
