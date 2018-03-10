package se.thorsell.catdex;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class CatImagesActivity extends ListActivity{

    // Image view to display the fetched cat
    public ImageView imgView;

    // Progress dialog
    private ProgressDialog pDialog;

    private ArrayList<HashMap<String, String>> catList;

    // url to get all products list
    private static final String url_all_cats = "http://178.62.50.61/android_connect/get_catsTagsMap.php";

    // cats JSONArray
    private String cats = null;

    // Bitmap for the fetched cat.
    public Bitmap catImage;

    // Stolen from https://www.codeguru.com/cpp/misc/article.php/c19583/Download-and-Decode-JSON-Data-with-your-Android-App.htm
    protected String HTTPGetCall(String WebMethodURL) throws IOException
    {
        StringBuilder response = new StringBuilder();

        //Prepare the URL and the connection
        URL u = new URL(WebMethodURL);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            //Get the Stream reader ready
            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()),8192);

            //Loop through the return data and copy it over to the response object to be processed
            String line;

            while ((line = input.readLine()) != null)
            {
                response.append(line);
            }

            input.close();
        }

        return response.toString();
    }

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
            pDialog = new ProgressDialog(CatImagesActivity.this);
            pDialog.setMessage("Loading cat. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        // getting cat from url
        protected String doInBackground(String... args) {

            String data = "";
            try {
                data = HTTPGetCall(url_all_cats);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Data from php: ", data);

            // TODO Fix the HTTPGetCall function so it doesn't return with [] enclosing the data.
            data = data.replace("[","");
            data = data.replace("]","");
            JSONObject obj = null;

            // Convert the raw data to a JSON object.
            try {
                obj = new JSONObject(data.toString());
                Log.d("ACatActivity", "Object as string: " + obj.toString());
                Log.d("ACatActivity", "Cat name: " + obj.getString("name"));
                Log.d("ACatActivity", "Image string: " + obj.getString("image"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON_failed", "JSON object creation failed!");
                Log.e("Malformed string", "Could not parse malformed json: \"" + data + "\"");
            }
            Log.d("ACatActivity", "JSON object: " + (obj != null ? obj.toString() : null));

            String imageString = "";
            try {
                imageString = obj.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ACatActivity", "JSON object was null");
            }

            // Hardcoded car image string.
            String imageStringHC = "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";

            //imageString = imageStringHC;

            // Convert the Base64 image string into a bitmap.
            Log.d("ACatActivity", "Image string is: " + imageString);
            Log.d("ACatActivity", "Hardcoded image: " + imageStringHC);
            Log.d("ACatActivity", "Are they equal? " + (imageString.equals(imageStringHC)));
            Log.d("ACatActivity", "Are they equal2? " + (imageString == imageStringHC));

            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Log.d("ACatActivity", "Byte array is: " + Arrays.toString(decodedString));
            catImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Log.d("ACatActivity", "Bitmap is :" + catImage);

            return null;
        }

        // after completing background task dismiss the progress dialog
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting the cat
            pDialog.dismiss();
            Log.d("ACatActivity", "Made it this far!");
            runOnUiThread(() -> {

                imgView = findViewById(R.id.imgView);
                if (imgView != null) {
                    Log.d("ACatActivity", "Img view not null!");
                    if (catImage != null) {
                        Log.d("ACatActivity", "catImage not null!");
                        imgView.setImageBitmap(catImage);
                    } else {
                        Log.e("ACatActivity", "catImage null!");
                    }
                } else {
                    Log.e("ACatActivity", "imgView null!");
                }
            });
        }
    }
}
