package vaycent.testbaidumap.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.BDLocation;

import java.util.ArrayList;

/**
 * Created by vaycent on 2017/6/7.
 */

public class Indoor implements Parcelable {
    public final static String KEY_NAME = "indoorDataKey";

    private BDLocation currentLocation;
    private String indoorMapId;
    private ArrayList<String> floorsList;

    public Indoor(BDLocation currentLocation,String indoorMapId,ArrayList<String> floorsList){
        this.currentLocation = currentLocation;
        this.indoorMapId = indoorMapId;
        this.floorsList = floorsList;

    }

    public Indoor(Parcel in) {
        currentLocation = in.readParcelable(BDLocation.class.getClassLoader());
        indoorMapId = in.readString();
        floorsList = in.readArrayList(ArrayList.class.getClassLoader());
    }

    public BDLocation getCurrentLocation(){return currentLocation;}
    public String getIndoorMapId(){return indoorMapId;}
    public ArrayList<String> getFloorsList(){return floorsList;}


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
    }

    public int describeContents() {
        return 0;
    }







}
