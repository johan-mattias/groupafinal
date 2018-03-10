package se.thorsell.catdex;

/**
 * Created by Henrik on 10/03/2018.
 * Grid view for the cat display
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.thorsell.catdex.R;

public class CatsAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Cat> cats;

    // 1
    public CatsAdapter(Context context, ArrayList<Cat> cats) {
        this.mContext = context;
        this.cats = cats;
    }

    // 2
    @Override
    public int getCount() {
        return cats.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final Cat cat = cats.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_cat, null);
        }

        // 3
        final ImageView imageView = convertView.findViewById(R.id.imageview_cover_art);

        // 4
        imageView.setImageBitmap(cat.getImage());

        return convertView;
    }

}