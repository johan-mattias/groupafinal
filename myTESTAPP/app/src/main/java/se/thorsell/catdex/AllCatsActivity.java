package se.thorsell.catdex;

/*
 * Created by Henrik on 08/02/2018.
 * From: https://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import se.thorsell.catdex.R;

public class AllCatsActivity extends ListActivity{
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jParser = new JSONParser();

    private ArrayList<HashMap<String, String>> catList;

    // url to get all products list
    private static final String url_all_cats = "http://178.62.50.61/android_connect/get_cat.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CATS = "cats";
    private static final String TAG_CID = "cid";
    private static final String TAG_NAME = "name";

    // products JSONArray
    private JSONArray cats = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cat);

        // HashMap for ListView
        catList = new ArrayList<>();

        // Loading products in Background Thread
        new LoadAllCats().execute();

        // Get ListView
        ListView lv = getListView();

        // on selecting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener((parent, view, position, id) -> {
            // getting values from selected ListItem
            String pid = ((TextView) view.findViewById(R.id.cid)).getText()
                    .toString();

            // Starting new intent
            Intent in = new Intent(getApplicationContext(),
                    EditCatActivity.class);
            // sending pid to next activity
            in.putExtra(TAG_CID, pid);

            // starting new activity and expecting some response back
            startActivityForResult(in, 100);
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllCats extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllCatsActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_cats, "GET", params);

            // Check your log cat for JSON response
            Log.d("All Cats: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    cats = json.getJSONArray(TAG_CATS);

                    // looping through All Products
                    for (int i = 0; i < cats.length(); i++) {
                        JSONObject c = cats.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_CID);
                        String name = c.getString(TAG_NAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_CID, id);
                        map.put(TAG_NAME, name);

                        // adding HashList to ArrayList
                        catList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewCatActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /*
         * After completing background task Dismiss the progress dialog
         * */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(() -> {
                /*
                 * Updating parsed JSON data into ListView
                 * */
                ListAdapter adapter = new SimpleAdapter(
                        AllCatsActivity.this, catList,
                        R.layout.list_item, new String[] { TAG_CID,
                        TAG_NAME},
                        new int[] { R.id.cid, R.id.name });
                // updating ListView
                setListAdapter(adapter);
            });

        }

    }
}
