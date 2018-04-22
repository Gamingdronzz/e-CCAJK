package com.ccajk.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ccajk.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeHelper;


public class WelcomeScreen extends WelcomeActivity {

    WelcomeHelper welcomeScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        welcomeScreen = new WelcomeHelper(this, WelcomeScreen.class);
        welcomeScreen.show(savedInstanceState);
    }

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorPrimary)
                .page(new TitlePage(R.drawable.logo,
                        "Title")
                )
                .page(new BasicPage(R.drawable.logo,
                        "Header",
                        "More text.")
                        .background(R.color.colorAccent)
                )
                .page(new BasicPage(R.drawable.logo,
                        "Lorem ipsum",
                        "dolor sit amet.")
                )
                .swipeToDismiss(true)
                .build();
    }
}
