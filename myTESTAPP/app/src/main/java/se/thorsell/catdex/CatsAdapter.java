package se.thorsell.catdex;

/*
  Created by Henrik on 10/03/2018.
  Adapter for the grid view to the cat display
  Based on https://www.raywenderlich.com/127544/android-gridview-getting-started
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class CatsAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Cat> cats;

    public CatsAdapter(Context context, ArrayList<Cat> cats) {
        this.mContext = context;
        this.cats = cats;
    }

    @Override
    public int getCount() {
        return cats.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Cat cat = cats.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_cat, null);
        }

        final ImageView imageView = convertView.findViewById(R.id.imageview_cat);

        imageView.setImageBitmap(cat.getImage());

        return convertView;
    }
}