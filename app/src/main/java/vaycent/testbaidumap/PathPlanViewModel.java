package vaycent.testbaidumap;

import android.databinding.ObservableField;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by vaycent on 2017/6/2.
 */

public class PathPlanViewModel {
    public ObservableField<String> startName = new ObservableField<>();
    public ObservableField<LatLng> startLatLng = new ObservableField<>();
    public ObservableField<String> startFloor = new ObservableField<>();

    public ObservableField<String> endName = new ObservableField<>();
    public ObservableField<LatLng> endLatLng = new ObservableField<>();
    public ObservableField<String> endFloor = new ObservableField<>();

}
