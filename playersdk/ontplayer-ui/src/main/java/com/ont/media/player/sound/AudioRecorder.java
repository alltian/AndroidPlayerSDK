package com.ont.media.player.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;

import com.ont.media.player.util.L;

/**
 * Created by betali on 2018/5/25.
 */

public class AudioRecorder {

    private static final String TAG = AudioRecorder.class.getSimpleName();
    private volatile boolean mRunning;
    private Thread mAudioRecordThread;
    private static AudioRecord mAudioRecord;
    private static AcousticEchoCanceler mEchoConceler;
    private static AutomaticGainControl mGainControl;
    private static NoiseSuppressor mNoiseSuppressor;
    private AudioEncoder mAudioEncoder;
    private byte[] mAudioData;

    public AudioRecorder(AudioEncoder audioEncoder) {

        this.mAudioEncoder = audioEncoder;
    }

    public int initChannelConfig() {

        int channelConfig = 0;
        int bufferSize = AudioRecord.getMinBufferSize(IEncodeDef.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, IEncodeDef.AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, IEncodeDef.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, IEncodeDef.AUDIO_FORMAT, bufferSize);
        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {

            bufferSize = AudioRecord.getMinBufferSize(IEncodeDef.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, IEncodeDef.AUDIO_FORMAT);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, IEncodeDef.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, IEncodeDef.AUDIO_FORMAT, bufferSize);

            channelConfig = AudioFormat.CHANNEL_IN_MONO;
        } else {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }

        mAudioData = new byte[bufferSize];
        return channelConfig;
    }

    public boolean start() {

        mRunning = true;
        mAudioRecordThread = new Thread() {

            @Override
            public void run() {

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                if (AcousticEchoCanceler.isAvailable()) {
                    mEchoConceler = AcousticEchoCanceler.create(mAudioRecord.getAudioSessionId());
                    if (mEchoConceler != null) {
                        mEchoConceler.setEnabled(true);
                    }
                }

                if (AutomaticGainControl.isAvailable()) {
                    mGainControl = AutomaticGainControl.create(mAudioRecord.getAudioSessionId());
                    if (mGainControl != null) {
                        mGainControl.setEnabled(true);
                    }
                }

                if (NoiseSuppressor.isAvailable()) {
                    mNoiseSuppressor = NoiseSuppressor.create(mAudioRecord.getAudioSessionId());
                    if (mNoiseSuppressor != null) {
                        mNoiseSuppressor.setEnabled(true);
                    }
                }
                mAudioRecord.startRecording();

                int bufferReadResult = 0;
                while (mRunning && !mAudioRecordThread.isInterrupted() && mAudioRecord != null) {

                    bufferReadResult = mAudioRecord.read(mAudioData, 0, mAudioData.length);
                    if (bufferReadResult <= 0) {

                        continue;
                    }
                    mAudioEncoder.onGetFrame(mAudioData, bufferReadResult);
                }
                L.e(TAG, "audio record quit! running = " + mRunning);
            }
        };
        mAudioRecordThread.start();
        return true;
    }

    public void stop() {

        mRunning = false;

        if (mAudioRecordThread != null) {

            mAudioRecordThread.interrupt();
            try {

                mAudioRecordThread.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
                mAudioRecordThread.interrupt();
            }
            mAudioRecordThread = null;
        }
        release();
    }

    private void release() {

        if (mAudioRecord != null) {

            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }

        if (mEchoConceler != null) {
            mEchoConceler.setEnabled(false);
            mEchoConceler.release();
            mEchoConceler = null;
        }

        if (mGainControl != null) {
            mGainControl.setEnabled(false);
            mGainControl.release();
            mGainControl = null;
        }

        if (mNoiseSuppressor != null) {
            mNoiseSuppressor.setEnabled(false);
            mNoiseSuppressor.release();
            mNoiseSuppressor = null;
        }
    }
}
