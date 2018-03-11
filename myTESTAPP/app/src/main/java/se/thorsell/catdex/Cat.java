package se.thorsell.catdex;

import android.graphics.Bitmap;

/**
 * Created by Henrik on 10/03/2018.
 * To hold cat images and names for the grid view.
 */


public class Cat {
    private final String name;
    private final Bitmap image;

    public Cat(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

}
