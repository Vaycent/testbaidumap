package vaycent.testbaidumap.Location;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import vaycent.testbaidumap.EventBus.LocationMsgEvent;

/**
 * Created by vaycent on 2017/5/24.
 */

public class LocationManagerClientListener implements BDLocationListener {

//    private MapView mMapView;
//    private BaiduMap listenerBaiduMap;
//    private BDLocation resultLocation;
//    private LocationClient mLocationClient;

    private LocationManager mLocationManager;

    public LocationManagerClientListener(LocationManager mLocationManager){
//        this.mMapView = mMapView;
//        this.listenerBaiduMap = mMapView.getMap();
//        this.mLocationClient = mLocationClient;
        this.mLocationManager = mLocationManager;
    }

    @Override
    public void onReceiveLocation(BDLocation callbackLocation) {

//        resultLocation = location;

        //获取定位结果
        StringBuffer sb = new StringBuffer(256);

        sb.append("time : ");
        sb.append(callbackLocation.getTime());    //获取定位时间

        sb.append("\nerror code : ");
        sb.append(callbackLocation.getLocType());    //获取类型类型

        sb.append("\nlatitude : ");
        sb.append(callbackLocation.getLatitude());    //获取纬度信息

        sb.append("\nlontitude : ");
        sb.append(callbackLocation.getLongitude());    //获取经度信息

        sb.append("\nradius : ");
        sb.append(callbackLocation.getRadius());    //获取定位精准度

        sb.append("\nfloor : ");
        sb.append(callbackLocation.getFloor());    //获取定位楼层


        if (callbackLocation.getLocType() == BDLocation.TypeGpsLocation){

            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(callbackLocation.getSpeed());    // 单位：公里每小时

            sb.append("\nsatellite : ");
            sb.append(callbackLocation.getSatelliteNumber());    //获取卫星数

            sb.append("\nheight : ");
            sb.append(callbackLocation.getAltitude());    //获取海拔高度信息，单位米

            sb.append("\ndirection : ");
            sb.append(callbackLocation.getDirection());    //获取方向信息，单位度

            sb.append("\naddr : ");
            sb.append(callbackLocation.getAddrStr());    //获取地址信息

            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

            sb.append("\nfloor is support : ");
            sb.append(callbackLocation.getIndoorLocationSurpport());    //获取是否支持定位楼层

            sb.append("\nfloor : ");
            sb.append(callbackLocation.getFloor());    //获取定位楼层

        } else if (callbackLocation.getLocType() == BDLocation.TypeNetWorkLocation){

            // 网络定位结果
            sb.append("\naddr : ");
            sb.append(callbackLocation.getAddrStr());    //获取地址信息

            sb.append("\noperationers : ");
            sb.append(callbackLocation.getOperators());    //获取运营商信息

            sb.append("\nfloor is support : ");
            sb.append(callbackLocation.getIndoorLocationSurpport());    //获取是否支持定位楼层

            sb.append("\nfloor : ");
            sb.append(callbackLocation.getFloor());    //获取定位楼层

            sb.append("\ndescribe : ");
            sb.append("网络定位成功");



        } else if (callbackLocation.getLocType() == BDLocation.TypeOffLineLocation) {

            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (callbackLocation.getLocType() == BDLocation.TypeServerError) {

            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

        } else if (callbackLocation.getLocType() == BDLocation.TypeNetWorkException) {

            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");

        } else if (callbackLocation.getLocType() == BDLocation.TypeCriteriaException) {

            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

        }

        sb.append("\nlocationdescribe : ");
        sb.append(callbackLocation.getLocationDescribe());    //位置语义化信息

        List<Poi> list = callbackLocation.getPoiList();    // POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }

        Log.i("BaiduLocationApiDem", sb.toString());



        //Vaycent
        if(4.9E-324D!=callbackLocation.getLatitude()&&4.9E-324D!=callbackLocation.getLongitude()) {
            mLocationManager.stopLocationSearch();
            mLocationManager.setResultLocation(callbackLocation);
            EventBus.getDefault().post(new LocationMsgEvent(callbackLocation));
        }

    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

//    public BDLocation getResultLocation(){
//        return this.resultLocation;
//    }
}
