package se.thorsell.catdex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import se.thorsell.testapp.R;


public class MainActivity extends NavigationActivity {

    //private TextView mTextMessage;

    Button btnViewCats;
    Button btnNewCat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buttons
        btnViewCats = (Button) findViewById(R.id.btnViewCats);
        btnNewCat = (Button) findViewById(R.id.btnCreateCat);

        // view cats click event
        btnViewCats.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Launching a cat activity
                    Intent i = new Intent(getApplicationContext(), ACatActivity.class);
                    startActivity(i);
                }
        });

    }

    //When pressing the button on first page
    public void goToActivity(View view) {
        Intent Intent = new Intent(this, SecondActivity.class);
        startActivity(Intent);
    }


    @Override
    int getContentViewId() {

        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }



}
