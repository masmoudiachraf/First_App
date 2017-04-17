package com.example.masmo.first_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by masmo on 08/04/2017.
 */

public class EditRV extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_rv);
    }
}
