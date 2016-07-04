package com.answer.waveview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.answer.waveview.lib.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView waveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveView = (WaveView) findViewById(R.id.wave_view);

        findViewById(R.id.button_add_percentage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveView.setPercentage(waveView.getPercentage() + 10);
            }
        });

        findViewById(R.id.button_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waveView.isAnimRunning()) {
                    waveView.stopAnim();
                } else {
                    waveView.startAnim();
                }
            }
        });
    }
}
