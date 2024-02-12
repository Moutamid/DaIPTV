package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import com.android.iplayer.controller.VideoController;
import com.android.iplayer.interfaces.IVideoController;
import com.android.iplayer.widget.WidgetFactory;
import com.android.iplayer.widget.controls.ControWindowView;
import com.android.iplayer.widget.controls.ControlCompletionView;
import com.android.iplayer.widget.controls.ControlFunctionBarView;
import com.android.iplayer.widget.controls.ControlGestureView;
import com.android.iplayer.widget.controls.ControlLoadingView;
import com.android.iplayer.widget.controls.ControlStatusView;
import com.android.iplayer.widget.controls.ControlToolBarView;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.databinding.ActivityVideoPlayerBinding;

public class VideoPlayerActivity extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String url = getIntent().getStringExtra("url");
        String name = getIntent().getStringExtra("name");

        Log.d("VideoURLPlayer", ""+url);

        binding.videoPlayer.setAutoChangeOrientation(true);

        VideoController controller = new VideoController(binding.videoPlayer.getContext());
        binding.videoPlayer.setController(controller);
        WidgetFactory.bindDefaultControls(controller);
        controller.setTitle(name);
        // binding.videoPlayer.createController();

        ControlToolBarView toolBarView=new ControlToolBarView(this);
        toolBarView.setTarget(IVideoController.TARGET_CONTROL_TOOL);
        toolBarView.showBack(true);

        toolBarView.showMenus(true,false,false);
        toolBarView.setOnToolBarActionListener(new ControlToolBarView.OnToolBarActionListener() {
            @Override
            public void onBack() {
                //Logger.d(TAG,"onBack");
                onBackPressed();
            }

            @Override
            public void onTv() {
                //Logger.d(TAG,"onTv");
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            }

            @Override
            public void onWindow() {
                //Logger.d(TAG,"onWindow");
                //startGoableWindow(null);
            }

            @Override
            public void onMenu() {
                //Logger.d(TAG,"onMenu");
                //showMenuDialog();
            }
        });

        ControlFunctionBarView functionBarView=new ControlFunctionBarView(this);
        functionBarView.showSoundMute(false,false);
        ControlGestureView gestureView=new ControlGestureView(this);
        ControlCompletionView completionView=new ControlCompletionView(this);
        ControlStatusView statusView=new ControlStatusView(this);
        ControlLoadingView loadingView=new ControlLoadingView(this);
        ControWindowView windowView=new ControWindowView(this);
        controller.addControllerWidget(toolBarView,functionBarView,gestureView,completionView,statusView,loadingView,windowView);

        binding.videoPlayer.setDataSource(url);
        // binding.videoPlayer.setDataSource("https://upload.dongfeng-nissan.com.cn/nissan/video/202204/4cfde6f0-bf80-11ec-95c3-214c38efbbc8.mp4");
        binding.videoPlayer.prepareAsync();

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.videoPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.videoPlayer.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.videoPlayer.isBackPressed()) {
            finish();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.videoPlayer.onDestroy();
    }

}