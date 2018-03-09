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

import se.thorsell.catdex.R;


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
    private static final String url_all_cats = "http://178.62.50.61/android_connect/test.php";

    // JSON node names
    private static final String TAG_CID = "cid";
    private static final String TAG_NAME = "name";

    // cats JSONArray
    private String cats = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cat);

        // HashMap for list view
        catList = new ArrayList<>();

        // Loading products in background thread
        new LoadAllCats().execute();

        // Get ListView
        ListView lv = getListView();

        // on selecting single cat
        // launching edit cat product screen
    }

    // background async task to load all cats by making http request
    class LoadAllCats extends AsyncTask<String, String, String> {
        // before starting background thread show progress dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ACatActivity.this);
            pDialog.setMessage("Loading cats. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        // getting all cats from url
        protected String doInBackground(String... args) {
            // building parameters
            List<org.apache.http.NameValuePair> params = new ArrayList<>();

            // getting JSON string from url
            JSONObject json = jParser.makeHttpRequest(url_all_cats, "GET", params);
            try {
                // check your log catalogue for JSON response
                Log.d("All cats: ", json.toString());
                cats = json.getString("name");
                Log.d("The cat name: ", cats);

                // create a new HashMap
                HashMap<String, String> map = new HashMap<>();

                // add the cat to the HashMap with a dummy ID.
                map.put(TAG_CID, "1");
                map.put(TAG_NAME, cats);

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
