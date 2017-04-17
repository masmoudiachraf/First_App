package com.example.masmo.first_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by masmo on 26/03/2017.
 */

public class InscritCoiffeur extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.inscrit_coiffeur);

    }
}

