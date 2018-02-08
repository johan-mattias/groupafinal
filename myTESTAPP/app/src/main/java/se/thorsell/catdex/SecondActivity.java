package se.thorsell.catdex;

import se.thorsell.testapp.R;

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
