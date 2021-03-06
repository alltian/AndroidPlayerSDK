package com.ont.media.player;

/**
 * 播放器配置类
 * Created by xinyu on 2018/4/3.
 */

public class PlayerConfig {

    public interface PlayType {

        int TYPE_NORMAL = 0x01;
        int TYPE_CYCLE = 0x02;
    }

    public boolean isLooping;//是否循环播放
    public boolean mAutoRotate;//是否旋转屏幕
    public boolean addToPlayerManager;//是否添加到播放管理器
    public boolean usingSurfaceView;//是否使用TextureView
    public boolean enableMediaCodec;//是否启用硬解码
    public boolean savingProgress;//是否保存进度
    public IPlayer mAbstractPlayer = null;//自定义播放核心
    public boolean disableAudioFocus;//关闭AudioFocus监听

    // added by betali on 2018/08/31
    public boolean isLocalVideo; // 是否本地视频流
    public boolean enableMediaPlayerSoftScreenshot; // 是否使用播放器软解码截屏功能（会增加软解码时的开销）
    public String screenshotPath; // 截图保存地址
    public int playType; // 播放类型：普通播放；循环播放
    public int maxCacheDuration; // 追帧最大缓存数
    public boolean isPlayLive;//是否直播(非循环播放时生效)

    private PlayerConfig() {

        this.screenshotPath = "/sdcard";
        this.playType = PlayType.TYPE_NORMAL;
        this.maxCacheDuration = 3000;
    }

    private PlayerConfig(PlayerConfig origin) {
        this.isPlayLive = origin.isPlayLive;
        this.isLooping = origin.isLooping;
        this.mAutoRotate = origin.mAutoRotate;
        this.addToPlayerManager = origin.addToPlayerManager;
        this.usingSurfaceView = origin.usingSurfaceView;
        this.enableMediaCodec = origin.enableMediaCodec;
        this.mAbstractPlayer = origin.mAbstractPlayer;
        this.savingProgress = origin.savingProgress;
        this.disableAudioFocus = origin.disableAudioFocus;
        this.enableMediaPlayerSoftScreenshot = origin.enableMediaPlayerSoftScreenshot;
        this.screenshotPath = origin.screenshotPath;
        this.isLocalVideo = origin.isLocalVideo;
        this.playType = origin.playType;
        this.maxCacheDuration = origin.maxCacheDuration;
    }

    public static class Builder {

        private PlayerConfig target;

        public Builder() {
            target = new PlayerConfig();
        }

        /**
         * 添加到{@link VideoViewManager},如需集成到RecyclerView或ListView请开启此选项
         */
        public Builder addToPlayerManager() {
            target.addToPlayerManager = true;
            return this;
        }

        /**
         * 启用SurfaceView
         */
        public Builder usingSurfaceView() {
            target.usingSurfaceView = true;
            return this;
        }

        /**
         * 设置自动旋转
         */
        public Builder autoRotate() {
            target.mAutoRotate = true;
            return this;
        }

        /**
         * 是否直播
         */
        public Builder setPlayLive( boolean isPlayLive) {
            target.isPlayLive = isPlayLive;
            return this;
        }

        /**
         * 开启循环播放
         */
        public Builder setLooping() {
            target.isLooping = true;
            return this;
        }

        /**
         * 开启硬解码，只对IjkPlayer有效
         */
        public Builder enableMediaCodec() {
            target.enableMediaCodec = true;
            return this;
        }

        /**
         * 设置自定义播放核心
         */
        public Builder setCustomMediaPlayer(IPlayer abstractPlayer) {
            target.mAbstractPlayer = abstractPlayer;
            return this;
        }

        /**
         * 保存播放进度
         */
        public Builder savingProgress() {
            target.savingProgress = true;
            return this;
        }

        /**
         * 关闭AudioFocus监听
         */
        public Builder disableAudioFocus() {
            target.disableAudioFocus = true;
            return this;
        }

        public Builder enableMediaPlayerSoftScreenshot() {
            target.enableMediaPlayerSoftScreenshot = true;
            return this;
        }

        public Builder setScreenshotPath(String screenshotPath) {
            target.screenshotPath = screenshotPath;
            return this;
        }

        public Builder setLocalVideo(boolean isLocalVideo) {
            target.isLocalVideo = isLocalVideo;
            return this;
        }

        public Builder setPlayType(int playType) {
            target.playType = playType;
            return this;
        }

        public Builder setMaxCacheDuration(int maxCacheDuration) {
            target.maxCacheDuration = maxCacheDuration;
            return this;
        }
        public PlayerConfig build() {
            return new PlayerConfig(target);
        }
    }
}
