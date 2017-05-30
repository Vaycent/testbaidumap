package vaycent.testbaidumap.Utils;

import android.view.View;

import java.util.Calendar;

/**
 * Created by vaycent on 2017/5/28.
 */

public abstract class NoMultiClickListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoMultiClick(v);
        }
    }

    protected abstract void onNoMultiClick(View v);


}
