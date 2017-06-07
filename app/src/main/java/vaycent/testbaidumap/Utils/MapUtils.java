package vaycent.testbaidumap.Utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;

import vaycent.testbaidumap.InDoor.IndoorRouteOverlay;
import vaycent.testbaidumap.Poi.CanClickPoiOverlay;
import vaycent.testbaidumap.Poi.IndoorPoiOverlay;
import vaycent.testbaidumap.R;

/**
 * Created by vaycent on 2017/2/20.
 */

public class MapUtils {
//  控制镜头移动
    public void mapMoveTo(BaiduMap mBaiduMap,double lat, double lon){
        //控制地图镜头开始位置
        LatLng centerpos = new LatLng(lat,lon);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerpos).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }
//  计算两点间距离
    public int calculateDistance(LatLng currentLatLng,LatLng targetLatLng){
        int distance = (int)(DistanceUtil.getDistance(currentLatLng, targetLatLng));
        return distance;
    }
//  控制是否显示百度log
    public void isShowBaiDuLogo(MapView mapView,boolean isShow){
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            if(!isShow)
                child.setVisibility(View.INVISIBLE);
            else
                child.setVisibility(View.VISIBLE);
        }
    }
//  控制是否显示地图比例尺
    public void isShowMapScale(MapView mapView,boolean isShow){
        mapView.showScaleControl(isShow);
    }
//  设置放大缩小控件位置
    public void setZoomWidgetPosition(MapView mapView,int left, int top, int right, int bottom){
        mapView.getChildAt(2).setPadding(left,top, right,bottom);
    }
//  px与dp转换
    public static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
//  室内路线画图
    public void drawIndoorRoutePlan(BaiduMap mBaiduMap,IndoorRouteResult indoorRouteResult){
        mBaiduMap.clear();
        IndoorRouteOverlay overlay = new IndoorRouteOverlay(mBaiduMap);
        overlay.setData(indoorRouteResult.getRouteLines().get(0));
        overlay.addToMap();
        overlay.zoomToSpan();



    }
//  室内多个Poi结果画图
    public void drawIndoorMultiPoi(BaiduMap mBaiduMap,Context mContext, PoiIndoorResult poiIndoorResult){
        mBaiduMap.clear();
        IndoorPoiOverlay overlay = new CanClickPoiOverlay(mBaiduMap,mContext);
        mBaiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(poiIndoorResult);
        overlay.addToMap();
        overlay.zoomToSpan();
    }
//  根据坐标画图Marker到指定位置
    public void drawMarkerWithLatLng(BaiduMap mBaiduMap,LatLng mLatLng){
        mBaiduMap.clear();
        BitmapDescriptor bitmapBD= BitmapDescriptorFactory
                .fromResource(R.drawable.ic_indoor_pointmarker);
        OverlayOptions optionOnePoi = new MarkerOptions()
                .position(mLatLng)
                .icon(bitmapBD);
        Marker onePoiMarker = (Marker) mBaiduMap.addOverlay(optionOnePoi);
    }
}
