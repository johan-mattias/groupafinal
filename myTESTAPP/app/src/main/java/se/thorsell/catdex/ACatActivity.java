package se.thorsell.catdex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.json.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Henrik on 08/02/
 * Based on/stole from guide on: https://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
 */

public class ACatActivity extends Activity {

    // Image view to display the fetched cat
    public ImageView imgView;

    // Progress dialog
    private ProgressDialog pDialog;

    // url to get all products list
    private static final String url_a_cat = "http://178.62.50.61/android_connect/get_cat.php";

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
        setContentView(R.layout.a_cat);;

        // Loading products in background thread
        new LoadACat().execute();
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

        String data = "";
        try {
            data = HTTPGetCall(url_a_cat);
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

        // Convert the Base64 image string into a bitmap.
        Log.d("ACatActivity", "Image string is: " + imageString);
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Log.d("ACatActivity", "Byte array is: " + Arrays.toString(decodedString));
        catImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Log.d("ACatActivity", "Bitmap is :" + catImage);
        return null;
        }

        // after completing background task dismiss the progress dialog and display the cat
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting the cat
            pDialog.dismiss();
            Log.d("ACatActivity", "Made it this far!");

            // display the cat
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
