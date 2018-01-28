package se.thorsell.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SecondActivity extends NavigationActivity {

    @Override
    int getContentViewId() {
        return R.layout.activity_second;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_notifications;
    }


}
