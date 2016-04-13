package com.hoangndang.femo;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by hdang on 3/28/2016.
 */
public class RandomUtilities {

    public static int randomSimilarColor(int color){

        //Def. saturation is 76, B is 100, increment Hua by 20
        float[] colorHSV = new float[3];
        Color.colorToHSV(color, colorHSV);

        //random color - HUE random from 0 to 359
        Random random = new Random();
        colorHSV[0] = random.nextInt(359); //setting saturation to be lighter 40%

        return Color.HSVToColor(colorHSV);
    }

}
