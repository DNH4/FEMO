package com.hoangndang.femo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hdang on 3/23/2016.
 */
public class GameSound {
    private static final int MAX_SOUND = 2; // how many sounds can play at a time
    private static final String TAG = "GameSound";
    private static final String SOUNDS_FOLDER = "GameSound";

    private SoundPool mSoundPool;
    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private boolean isMute;


    public GameSound (Context context){
        mAssets = context.getAssets();
        isMute=false;
/*
        //deprecated but need for compatibility
        mSoundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);
*/
        // for API 21
/*        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(MAX_SOUND)
                .build();*/

        mSoundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC,0);
        loadSounds();
    }

    private void loadSounds(){
        String[] soundNames;
        try{
            soundNames = mAssets.list(SOUNDS_FOLDER);//get the String array of all asset in givent path
        }catch (IOException ioe){
//            Log.e(TAG, "Could not find sounds assets", ioe);
            return;
        }
        for (String filename : soundNames) {
            try{
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            }catch (IOException ioe){
//                Log.e(TAG, "Could not load sounds", ioe);
            }
        }

    }

    private void load(Sound sound) throws IOException{
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());//get sound file description
        int soundId = mSoundPool.load(afd,1); //Load the sound from an asset file descriptor return a soundID to be play or unload
        sound.setSoundId(soundId);
    }

/*    void play(Sound sound){
        Integer soundId = sound.getSoundId();
        if(soundId == null){
            return;
        }
        // Specify how sound is play ************
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }*/

    void play(String soundDir, float volume, int loop){

        if (isMute) {
            for(Sound a : mSounds){
                if(a.getName().equalsIgnoreCase(soundDir)){
                    //mSoundPool.play(a.getSoundId(),1.0f, 1.0f, 1 ,0 , 1.0f);
                    mSoundPool.play(a.getSoundId(), volume, volume, 1, loop, 1.0f);

                }
            }
        }
    }


    void stop(int streamID){
        mSoundPool.stop(streamID);
    }


    public void release(){
        mSoundPool.release();
        //mSoundPool.stop();
    }

    public void mute(boolean audible){
        isMute = audible;
    }

    public List<Sound> getSounds(){
        return mSounds;
    }


}
