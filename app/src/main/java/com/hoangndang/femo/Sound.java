package com.hoangndang.femo;

/**
 * Created by hdang on 3/23/2016.
 */
public class Sound {

    private String mAssetPath;
    private String mName;
    private Integer mSoundId;

    public Sound(String assetPath){
        mAssetPath = assetPath;
        //getting file name
        String[] component = assetPath.split("/");
        String filename = component[component.length-1];//file name is that last part of directory
        //mName = filename.replace(".wav",""); //This is for if file name is .wav
        mName = filename;
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId){mSoundId=soundId;}
}
