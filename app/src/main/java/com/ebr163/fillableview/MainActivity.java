package com.ebr163.fillableview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.ebr163.view.FillableView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        final FillableView fillableViewLeft = (FillableView) findViewById(R.id.fillable_view_left);
        fillableViewLeft.setProgress(seekbar.getProgress() / 100F);

        final FillableView fillableViewTop = (FillableView) findViewById(R.id.fillable_view_top);
        fillableViewTop.setProgress(seekbar.getProgress() / 100F);

        final FillableView fillableViewRight= (FillableView) findViewById(R.id.fillable_view_right);
        fillableViewLeft.setProgress(seekbar.getProgress() / 100F);

        final FillableView fillableViewBottom = (FillableView) findViewById(R.id.fillable_view_bottom);
        fillableViewBottom.setProgress(seekbar.getProgress() / 100F);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fillableViewLeft.setProgress(progress / 100F);
                fillableViewTop.setProgress(progress / 100F);
                fillableViewRight.setProgress(progress / 100F);
                fillableViewBottom.setProgress(progress / 100F);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
