package com.example.weahen.wstest.Activity;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;

public class SettingActivity extends BaseActivity {

    private RadioGroup mRg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initTitle("设置");
//
//        mRg1=findViewById(R.id.rg_1);
//
//
//        mRg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                switch (checkedId) {
//
//                    case R.id.rb_1:
//                        openSpeaker();
//                        Toast.makeText(SettingActivity.this,"扬声器已打开",Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case R.id.rb_2:
//                        closeSpeaker();
//                        Toast.makeText(SettingActivity.this,"扬声器已关闭",Toast.LENGTH_SHORT).show();
//                        break;
//
//                }
//
//            }
//        });


    }

    private int currVolume = 0;
    /**
     * 打开扬声器
     */
//    private void openSpeaker() {
////        try{
////            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
////            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
////            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
////            if(!audioManager.isSpeakerphoneOn()) {
////                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
////                audioManager.setMode(AudioManager.MODE_IN_CALL);
////                audioManager.setSpeakerphoneOn(true);
////                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
////                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
////                        AudioManager.STREAM_VOICE_CALL);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////    /**
////     * 关闭扬声器
////     */
////    public void closeSpeaker() {
////        try {
////            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
////            if(audioManager != null) {
////                if(audioManager.isSpeakerphoneOn()) {
////                    audioManager.setSpeakerphoneOn(false);
////                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
////                            AudioManager.STREAM_VOICE_CALL);
////                }
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
}
