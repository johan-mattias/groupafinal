package se.thorsell.testapp;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;

 import org.apache.http.NameValuePair;

 import org.json.*;

 import android.app.ListActivity;
 import android.app.ProgressDialog;
 import android.content.Intent;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import android.widget.AdapterView;
 import android.widget.AdapterView.OnItemClickListener;
 import android.widget.ListAdapter;
 import android.widget.ListView;
 import android.widget.SimpleAdapter;
 import android.widget.TextView;



/**
 * Created by Henrik on 08/02/2018.
 */

public class ACatActivity extends ListActivity{

    // Progress dialog
    private ProgressDialog pDialog;

    // Creating JSON parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> catList;

    // url to get all products list
    private static String url_all_cats = "http://178.62.50.61/android_connect/get_cat.php";

    // JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CATS = "cats";
    private static final String TAG_CID = "cid";
    private static final String TAG_NAME = "name";

    // cats JSONArray
    JSONArray cats = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cat);

        // Hashmap for list view
        catList = new ArrayList<HashMap<String, String>>();

        // Loading products in background thread
        new LoadAllCats().execute();

        // Get listview
        ListView lv = getListView();

        // on selecting single cat
        // launching edit cat product screen
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected list item
                String cid = ((TextView) view.findViewById(R.id.cid)).getText().toString();

                // start new intent
                Intent in = new Intent(getApplicationContext(),
                           EditCatActivity.class);
                // sending cid to next activity
                in.putExtra(TAG_CID, cid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
    }
    // response from edit cat activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 recieved means user edited/deleted cat
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
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
            List<org.apache.http.NameValuePair> params = new ArrayList<NameValuePair>();

            // getting JSON string from url
            JSONObject json = jParser.makeHttpRequest(url_all_cats, "GET", params);

            // check your log catalogue for JSON response
            Log.d("All cats: ", json.toString());

            try {
                // checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // cat found
                    // getting array of cats
                    cats = json.getJSONArray(TAG_CATS);

                    // looping through all cats
                    for (int i = 0; i < cats.length(); i++) {
                        JSONObject c = cats.getJSONObject(i);

                        // storing each json object in variable
                        String id = c.getString(TAG_CID);
                        String name = c.getString(TAG_NAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to a HashMap key=>value
                        map.put(TAG_CID, id);
                        map.put(TAG_NAME, name);

                        // adding hashlist to ArrayList
                        catList.add(map);
                    }
                } else {
                    // no cats found
                    // launch add new cat activity
                    Intent i = new Intent(getApplicationContext(), NewCatActivity.class);
                    // closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } return (null);
        }
    }

    // after completing background task dismiss the progress dialog
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        //updating UI from background thread
        runOnUiThread(new Runnable() {
            public void run() {
                // updating parsed JSON data into ListView
                ListAdapter adapter = new SimpleAdapter(
                        ACatActivity.this, catList, R.layout.list_item,
                        new String[] { TAG_CID, TAG_NAME}, new int[] { R.id.cid, R.id.name}   );
                // updating list view
                setListAdapter(adapter);

            }
                
    });
    }
}
