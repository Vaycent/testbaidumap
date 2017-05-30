package vaycent.testbaidumap.Location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapView;

/**
 * Created by vaycent on 2017/5/23.
 */

public class LocationManager implements BaiduMap.OnBaseIndoorMapListener {

    private LocationClient mLocationClient;//定位SDK的核心类
    private LocationManagerClientListener mMyLocationListener;//自定义监听类
    private MapView locationManagerMapView;

    private BDLocation resultLocation;

    private volatile static LocationManager locationSingInstance;
    public static LocationManager getLocationSingInstance(Context context, MapView mapView) {
        if (locationSingInstance == null) {
            synchronized (LocationManager.class) {
                if (locationSingInstance == null) {
                    locationSingInstance = new LocationManager(context,mapView);
                }
            }
        }
        return locationSingInstance;
    }

    private LocationManager (Context context,MapView mapView){
        initCurrentLoacation(context,mapView);
        locationManagerMapView = mapView;
    }

    public MapView getMapView(){
        return locationManagerMapView;
    }

    public void setResultLocation(BDLocation resultLocation){
        this.resultLocation = resultLocation;
    }

    public BDLocation getResultLocation(){
        return resultLocation;
    }











    public void startLocationSearch(){
        if(null!=mLocationClient&&!mLocationClient.isStarted())
            mLocationClient.start();
    }

    public void stopLocationSearch(){
        if(null!=mLocationClient&&mLocationClient.isStarted())
            mLocationClient.stop();
    }




    private void initCurrentLoacation(Context context,MapView mapView){
        if(null==mLocationClient)
            mLocationClient = new LocationClient(context.getApplicationContext());
        if(null==mMyLocationListener)
            mMyLocationListener = new LocationManagerClientListener(this);
        mLocationClient.registerLocationListener(mMyLocationListener);

        InitLocation();//初始化
    }

    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(2*1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setNeedDeviceDirect(true); //返回的定位结果包含手机机头方向
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
        mLocationClient.startIndoorMode();
    }

    public void destroy(){
        if(null!=mLocationClient&&mLocationClient.isStarted())
            mLocationClient.stop();
        mLocationClient = null;
        mMyLocationListener = null;
        locationSingInstance = null;
    }


    @Override
    public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {

    }

}
