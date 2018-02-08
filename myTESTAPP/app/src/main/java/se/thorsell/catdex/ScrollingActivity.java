package se.thorsell.catdex;

import se.thorsell.testapp.R;

public class ScrollingActivity extends NavigationActivity {


    @Override
    int getContentViewId() {
        return R.layout.activity_scrolling;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_dashboard;
    }

}
