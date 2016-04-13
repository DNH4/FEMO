package com.hoangndang.femo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by hdang on 3/28/2016.
 *
 * Credit dialog if user clicked on the floating emo in welcome screen
 */
public class CreditDialog extends AppCompatDialog {

    public CreditDialog(final Context context) {
        super(context);
        setContentView(R.layout.activity_credit);

        ImageButton ibAndroidProfile = (ImageButton) findViewById(R.id.image_button_android_profile);
        ibAndroidProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch market for user to rate the app
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.DNH.moneykeeper"));
                context.startActivity(i);
            }
        });

        ImageButton ibMail = (ImageButton) findViewById(R.id.image_button_mail);
        ibMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch mail app with sendto developer for user to send opinion
                Intent i = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("dnhAndroid@gmail.com")
                        +"?subject=" + Uri.encode("FEMO feedback");
                Uri uri = Uri.parse(uriText);

                i.setData(uri);
                context.startActivity(i);
            }
        });

        ImageButton ibLinkedin = (ImageButton) findViewById(R.id.image_button_linkedin);
        ibLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch developer's linkedin
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("https://ca.linkedin.com/in/hndang"));
                context.startActivity(i);
            }
        });
    }
}