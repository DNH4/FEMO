package com.hoangndang.femo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hdang on 3/11/2016.
 */
public class GameOverActivity extends AppCompatActivity {

    private int mScore;
    private ArrayList<FEMO> mFEMOList;
    private RelativeLayout mViewB;
    private FloatingActionButton fab;
    private FEMO mFEMO;
    private int animateCount=0;
    private int mWidth;
    private int mHeight;
    private int loopDelay=0;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int mHighscore;
    private TextView tvHighScore;

    private GameSound gameSound;

    @Override
    public void onCreate(Bundle savedInstanceState) {

//        //*****Window transition animation******
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//
//            //getWindow().setExitTransition(new Explode().setDuration(750));
//            getWindow().setEnterTransition(new Slide().setDuration(750));
////            getWindow().setAllowEnterTransitionOverlap(false);
//        }
//        //*****FIN Window transition animation******

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        gameSound = new GameSound(this);


        //get data from passed
        Intent i = getIntent();
        mScore = i.getIntExtra(GameFragment.EXTRA_SCORE, 1); //get the score else default to 1
        mFEMOList = i.getParcelableArrayListExtra(GameFragment.EXTRA_EMO_CAUGHT);

        //update score
        TextView tvScore = (TextView) findViewById(R.id.tv_score);
        scoreAnimation(mScore, tvScore); // animate running score

        //check highscore
        tvHighScore = (TextView) findViewById(R.id.tv_highscore);

        // Animate FEMO found (showing them in a list)

        //play again
        Button bPlayAgain = (Button) findViewById(R.id.button_again);
        bPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ********* Animate FEMO found ***********
        mViewB = (RelativeLayout) findViewById(R.id.viewB);
        // Getting screen width and height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mHighscore = sharedPref.getInt(getString(R.string.high_score_key), 0);
        boolean isSound = sharedPref.getBoolean(getString(R.string.flag_sound), true);
        gameSound.mute(isSound);

        // Run runnable
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCount++;
                caughtEmoAnimation();
            }
        },500);



    }

    public void scoreAnimation(int myScore, final TextView textView){


        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0,(int) mScore);
        valueAnimator.setDuration(1000+myScore);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String score = getString(R.string.score, valueAnimator.getAnimatedValue());
                textView.setText(score);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(mScore>mHighscore){
                    tvHighScore.setTextColor(getResources().getColor(R.color.colorNewHighScore));
                    mHighscore = mScore;
                    editor = sharedPref.edit();
                    editor.putInt(getString(R.string.high_score_key),mHighscore);
                    gameSound.play(getString(R.string.sound_new_high_score),1.0f,0);
                }
                String highscoreText = getString(R.string.high_score_text,mHighscore);
                tvHighScore.setText(highscoreText);
                tvHighScore.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void caughtEmoAnimation(){
        // ******** Animate all the FEMO found *******************************
        int xPos=0;
        int yPos=mHeight/4;
        //******* Create Path   API 21
/*        Path path = new Path();
        path.moveTo(xPos, yPos); //move to original position
        path.lineTo((int)(mWidth-(float)mWidth/2- mFEMOList.get(0).getButtonSize()/2),yPos);*/


        //while(animateCount<mFEMOList.size()){
            //adding new button
            fab = new FloatingActionButton(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.leftMargin = xPos;
            params.topMargin = yPos;

            fab.setLayoutParams(params);
            mFEMO = mFEMOList.get(animateCount);

            fab.setImageDrawable(ContextCompat.getDrawable(this, mFEMO.getFEMOFace()));
            //fab.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.colorTextIcon))); // API 21
            fab.setColorFilter(getResources().getColor(R.color.colorTextIcon)); //!@#$
            fab.setBackgroundTintList(ColorStateList.valueOf(mFEMO.getColor()));
            mViewB.addView(fab);
            //fab.show();//do show animation

            //****** Create Animation
/*            ObjectAnimator moveEMO = ObjectAnimator
                    .ofFloat(fab, View.X, View.Y, path);*/ //API 21
            ObjectAnimator moveEMO = ObjectAnimator
                    .ofFloat(fab, "x",xPos,(mWidth-(float)mWidth/2- mFEMOList.get(0).getButtonSize()/2));
            moveEMO.setDuration(700)
                    .setInterpolator(new DecelerateInterpolator());
            //moveEMO.setStartDelay(100);
            moveEMO.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animateCount++;
                    if (animateCount < mFEMOList.size()) {
                        mFEMOList.size();
                        fab.hide();
                        // Run runnable
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                caughtEmoAnimation();
                            }
                        }, 100);
                    } else {
/*                        ObjectAnimator didntCatch1 = ObjectAnimator.ofFloat(fab, "rotation", 0f, -45f)
                                .setDuration(250);
                        //didntCatch1.start();*/

                        ObjectAnimator didntCatch2 = ObjectAnimator.ofFloat(fab, "rotation", 0, 540f)
                                .setDuration(1000);
                        didntCatch2.setInterpolator(new OvershootInterpolator());
                        didntCatch2.setRepeatCount(ValueAnimator.INFINITE);
                        didntCatch2.setRepeatMode(ValueAnimator.REVERSE);
                        didntCatch2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                gameSound.play(getString(R.string.game_over_laugh), 0.7f, 1); //loop 1 time for laughing noise
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                loopDelay++;
                                if (loopDelay > 3) {
                                    gameSound.play(getString(R.string.game_over_laugh), 0.85f, 1); //loop 1 time for laughing noise
                                    loopDelay = 0;
                                }
                            }
                        });
                        didntCatch2.start();

                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            moveEMO.start();
            //animateCount++;
            //animateCount = mFEMOList.size();// TESTING
        //}
        // ******** FIN Animate all the FEMO found *******************************
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSound.release();
    }
}
