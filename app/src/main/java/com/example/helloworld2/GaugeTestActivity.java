package com.example.helloworld2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import org.codeandmagic.android.gauge.GaugeView;

import java.util.Random;

/**
 * Created by Zaki on 06-Mar-16.
 */
public class GaugeTestActivity extends Activity {

    private GaugeView mGaugeView1;
    private GaugeView mGaugeView2;
    private final Random RAND = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gauge_test_activity);
        mGaugeView1 = (GaugeView) findViewById(R.id.gauge_P1);
        mGaugeView1.setTargetValue(35.5f);

//        mGaugeView1.SCALE_END_VALUE = 200;
//        mTimer.start();

    }

    private final CountDownTimer mTimer = new CountDownTimer(60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mGaugeView1.setTargetValue(35.5f);
        }

        @Override
        public void onFinish() {

        }
    };

}
