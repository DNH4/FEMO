package com.hoangndang.femo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hdang on 3/4/2016.
 *
 * FEMO object
 */
public class FEMO implements Parcelable{
    //private FloatingActionButton mFab;
    public static final int ADS_CHANCE_PERCENTAGE = 20;

    private int mXpos;
    private int mYpos;
    private int mButtonSize;
    private int mColor;
    private int mFEMOFace;
    private boolean isAd=false;

    Random random;

    //Constructor passing the name of the FEMO and the fab assigned
    public FEMO(int screenHeight, int screenWidth, int ButtonSize, int color) {
    //public FEMO(FloatingActionButton fab, int screenHeight, int screenWidth, int ButtonSize, int color) {
        //mName = name;
        //mFab = fab;
        mButtonSize = ButtonSize;
        mColor = color;

        //fab.setBackgroundTintList(ColorStateList.valueOf(mColor));

        //get a random point in
        random = new Random();
        int YPaddingTOP = ButtonSize+ButtonSize/6;
        int fabX = random.nextInt(screenWidth-ButtonSize); // set so that button have enough space in edge of screen
        int fabY = random.nextInt(screenHeight - YPaddingTOP - ButtonSize) + YPaddingTOP; // set so that button have enough space in edge of screen Y and enough space on TOP for when button move up after found
/*        Log.i("FEMO","Height"+screenHeight);
        Log.i("FEMO","Width"+screenWidth);
        Log.i("FEMO", "MaxY-Pos" + ((screenHeight - YPaddingTOP - ButtonSize) + YPaddingTOP));
        Log.i("FEMO", "MaxX-Pos" + (screenWidth - ButtonSize));
        Log.i("FEMO", "ButtonSize" + (ButtonSize));*/
        //**** test******
        setYpos(fabY);//Make sure that button doesn't get overlapp with the screen
        setXpos(fabX);
        //***Uncomment to test with FEMO locating at bottom right screen
        //setYpos(((screenHeight - YPaddingTOP - ButtonSize) + YPaddingTOP));//test - should be commented
        //setXpos((screenWidth-ButtonSize));//test - should be commented
        //**** fin test******

        //randomly determine if this is an Ads FEMO (20%)

        isAd = random.nextInt(100)<ADS_CHANCE_PERCENTAGE; //ADS_CHANCE_PERCENTAGE of chance that the face is an ads


        // generate random image
        ArrayList<Integer> icons = new ArrayList();
        icons.add(R.drawable.emo_im_angel);
        icons.add(R.drawable.emo_im_cool);
        icons.add(R.drawable.emo_im_crying);
        icons.add(R.drawable.emo_im_embarrassed);
        icons.add(R.drawable.emo_im_foot_in_mouth);
        icons.add(R.drawable.emo_im_happy);
        icons.add(R.drawable.emo_im_kissing);
        icons.add(R.drawable.emo_im_laughing);
        icons.add(R.drawable.emo_im_lips_are_sealed);
        icons.add(R.drawable.emo_im_money_mouth);
        icons.add(R.drawable.emo_im_sad);
        icons.add(R.drawable.emo_im_surprised);
        icons.add(R.drawable.emo_im_tongue_sticking_out);
        icons.add(R.drawable.emo_im_undecided);
        icons.add(R.drawable.emo_im_winking);
        icons.add(R.drawable.emo_im_wtf);
        icons.add(R.drawable.emo_im_yelling);
        mFEMOFace = randomFace(icons);
    }

    public int randomFace( ArrayList<Integer> icons){

        if(isAd){
            return R.drawable.ads_icon;
        }else{
            int size = icons.size();
            random = new Random();
            //add 20% chance that new face is and advertisement face
            return icons.get(random.nextInt(size));
        }

    }


    /*public FloatingActionButton getFab() {
        return mFab;
    }*/

    public int getButtonSize() {
        return mButtonSize;
    }

    public int getXpos() {
        return mXpos;
    }

    public void setXpos(int xpos) {
        mXpos = xpos;
    }

    public int getYpos() {
        return mYpos;
    }

    public void setYpos(int ypos) {
        mYpos = ypos;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getFEMOFace() {
        return mFEMOFace;
    }

    public boolean isAd() {
        return isAd;
    }

    // *********** Add Parcelable data *****
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mXpos);
        dest.writeInt(mYpos);
        dest.writeInt(mButtonSize);
        dest.writeInt(mColor);
        dest.writeInt(mFEMOFace);
    }

    protected FEMO(Parcel in) {
        mXpos = in.readInt();
        mYpos = in.readInt();
        mButtonSize = in.readInt();
        mColor = in.readInt();
        mFEMOFace = in.readInt();
    }

    public static final Creator<FEMO> CREATOR = new Creator<FEMO>() {
        @Override
        public FEMO createFromParcel(Parcel in) {
            return new FEMO(in);
        }

        @Override
        public FEMO[] newArray(int size) {
            return new FEMO[size];
        }
    };
}
