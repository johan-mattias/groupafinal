package se.thorsell.testapp;

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
