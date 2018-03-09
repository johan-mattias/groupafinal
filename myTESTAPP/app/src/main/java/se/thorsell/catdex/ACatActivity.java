package se.thorsell.catdex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.*;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


/**
 * Created by Henrik on 08/02/
 * Based on/stole from guide on: https://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
 */

public class ACatActivity extends ListActivity{

    // Progress dialog
    private ProgressDialog pDialog;

    // Creating JSON parser object
    private final JSONParser jParser = new JSONParser();

    private ArrayList<HashMap<String, String>> catList;

    // url to get all products list
    private static final String url_a_cat = "http://178.62.50.61/android_connect/get_cat.php";

    // JSON node names
    private static final String TAG_CID = "cid";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMAGE = "image";

    // cats JSONArray
    private String cats = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cat);

        // HashMap for list view
        catList = new ArrayList<>();

        // Loading products in background thread
        new LoadACat().execute();

        // Get ListView
        ListView lv = getListView();

        // on selecting single cat
        // launching edit cat product screen
    }

    // background async task to load a cats by making http request
    class LoadACat extends AsyncTask<String, String, String> {
        // before starting background thread show progress dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ACatActivity.this);
            pDialog.setMessage("Loading cat. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        // getting cat from url
        protected String doInBackground(String... args) {
            // building parameters
            List<org.apache.http.NameValuePair> params = new ArrayList<>();

            String catImageString;

            // getting JSON string from url
            JSONObject json = jParser.makeHttpRequest(url_a_cat, "GET", params);
            try {
                // check your log catalogue for JSON response
                assert json != null;
                //Log.d("A cat: ", json.toString());
                cats = json.getString("name");
                Log.d("The cat name: ", cats);

                // Get cat image string
                catImageString = json.getString("image");

                // create a new HashMap
                HashMap<String, String> map = new HashMap<>();

                // add the cat to the HashMap with a dummy ID.
                map.put(TAG_CID, "1");
                map.put(TAG_NAME, cats);
                map.put(TAG_IMAGE, catImageString);

                // add HashMap to the ArrayList
                catList.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }return (null);
        }

        // after completing background task dismiss the progress dialog
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting the cat
            pDialog.dismiss();
            //updating UI from background thread
            runOnUiThread(() -> {
                // updating parsed JSON data into ListView
                ListAdapter adapter = new SimpleAdapter(
                        ACatActivity.this, catList, R.layout.list_item,
                        new String[] { TAG_CID, TAG_NAME},
                        new int[] { R.id.cid, R.id.name });
                // updating list view
                setListAdapter(adapter);
            });
        }
    }
}
