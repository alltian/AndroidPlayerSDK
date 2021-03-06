package com.ont.media.player.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ont.media.player.IjkVideoView;
import com.ont.media.player.IVideoView;
import com.ont.media.player.R;
import com.ont.media.player.TimeBarView;
import com.ont.media.player.util.PlayerConstants;
import com.ont.media.player.util.WindowUtil;
import com.ont.media.player.widget.StatusView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseVideoController extends FrameLayout {

    protected View controllerView;//控制器视图
    protected IVideoView mVideoView;//播放器
    protected TimeBarView mTimeBarView; // 时间轴
    protected boolean mShowing;//控制器是否处于显示状态
    protected boolean mTimeBarShowing;
    protected boolean isLocked;
    protected int sDefaultTimeout = 4000;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected int currentPlayState;
    protected StatusView mStatusView;


    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mStatusView = new StatusView(getContext());
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 显示
     */
    public void show() {
    }

    /**
     * 隐藏
     */
    public void hide() {
    }

    public void setPlayState(int playState) {
        currentPlayState = playState;
        hideStatusView();
        switch (playState) {
            case IjkVideoView.STATE_ERROR:
                mStatusView.setMessage(getResources().getString(R.string.dkplayer_error_message));
                mStatusView.setButtonTextAndAction(getResources().getString(R.string.dkplayer_retry), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideStatusView();
                        mVideoView.retry(true, false);
                    }
                });
                this.addView(mStatusView, 0);
                break;
        }
    }

    public void showStatusView() {
        this.removeView(mStatusView);
        mStatusView.setMessage(getResources().getString(R.string.dkplayer_wifi_tip));
        mStatusView.setButtonTextAndAction(getResources().getString(R.string.dkplayer_continue_play), new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatusView();
                PlayerConstants.IS_PLAY_ON_MOBILE_NETWORK = true;
                mVideoView.start();
            }
        });
        this.addView(mStatusView);
    }

    public void hideStatusView() {
        this.removeView(mStatusView);
    }

    public void setPlayerState(int playerState) {
    }

    protected void doPauseResume() {
        if (currentPlayState == IjkVideoView.STATE_BUFFERING) return;
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void doStartStopFullScreen() {
        if (mVideoView.isFullScreen()) {
            WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mVideoView.stopFullScreen();
        } else {
            WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mVideoView.startFullScreen();
        }
    }


    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mVideoView.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    protected int setProgress() {
        return 0;
    }

    /**
     * 获取当前系统时间
     */
    protected String getCurrentSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }



    protected String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowProgress);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

    public void setVideoView(IVideoView videoView) {
        this.mVideoView = videoView;
    }

    public void setTimeBarView(TimeBarView timeBarView) {

        this.mTimeBarView = timeBarView;
        mTimeBarShowing = true;
    }

    // added by betali on 2018/08/31
    /**
     * 语音推送启停
     */
    public void opPushAudio(boolean start) {}

    /**
     * 截屏
     */
    public void doScreenshot() {}
}
