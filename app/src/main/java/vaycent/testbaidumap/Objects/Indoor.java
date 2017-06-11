package vaycent.testbaidumap.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

/**
 * Created by vaycent on 2017/6/7.
 */

public class Indoor implements Parcelable {
    public final static String KEY_NAME = "indoorDataKey";

    private BDLocation currentLocation;
    private String indoorMapId;
    private ArrayList<String> floorsList;
    private LatLng airportLatLng;

    public Indoor(BDLocation currentLocation,String indoorMapId,ArrayList<String> floorsList,LatLng airportLatLng){
        this.currentLocation = currentLocation;
        this.indoorMapId = indoorMapId;
        this.floorsList = floorsList;
        this.airportLatLng = airportLatLng;
    }

    public Indoor(Parcel in) {
        currentLocation = in.readParcelable(BDLocation.class.getClassLoader());
        indoorMapId = in.readString();
        floorsList = in.readArrayList(ArrayList.class.getClassLoader());
        airportLatLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public BDLocation getCurrentLocation(){return currentLocation;}
    public String getIndoorMapId(){return indoorMapId;}
    public ArrayList<String> getFloorsList(){return floorsList;}
    public LatLng getAirportLatLng(){return airportLatLng;}


    public static final Parcelable.Creator<Indoor> CREATOR = new Creator<Indoor>() {
        public Indoor createFromParcel(Parcel in) {
            return new Indoor(in);
        }
        public Indoor[] newArray(int size) {
            return new Indoor[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(currentLocation,flags);
        dest.writeString(indoorMapId);
        dest.writeList(floorsList);
        dest.writeParcelable(airportLatLng,flags);
    }

    public int describeContents() {
        return 0;
    }







}
