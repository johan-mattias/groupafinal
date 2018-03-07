package se.thorsell.catdex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import se.thorsell.catdex.R;


public class MainActivity extends Activity {

    //private TextView mTextMessage;

    Button btnViewCats;
    Button btnNewCat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Buttons
        btnViewCats = (Button) findViewById(R.id.btnViewCats);
        btnNewCat = (Button) findViewById(R.id.btnCreateCat);

        // view cats click event
        btnViewCats.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching a cat activity
                Intent i = new Intent(getApplicationContext(), ACatActivity.class); //set ACatActivity to AllCatsActivity to use full project
                startActivity(i);
            }
        });

        // view cats click event
        btnNewCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching  create new cat activity
                Intent i = new Intent(getApplicationContext(), NewCatActivity.class);
                startActivity(i);
            }
        });

    }
}