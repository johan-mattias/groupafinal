package se.thorsell.catdex;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import se.thorsell.catdex.R;

public class NewCatActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;

    // url to create new product
    private static String url_create_cat = "http://178.62.50.61/android_connect/create_cat.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cat);

        // Edit Text
        inputName = (EditText) findViewById(R.id.inputName);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateCat);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewCat().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new cat
     * */
    class CreateNewCat extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewCatActivity.this);
            pDialog.setMessage("Creating Cat..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating cat
         * */
        protected String doInBackground(String... args) {
            String name = inputName.getText().toString();

            // This try-catch section a modification of: https://stackoverflow.com/a/42780501
            try {
                // Connect to the server.
                URL url = new URL("http://178.62.50.61/android_connect/create_cat.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set variables for the connection.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Create the JSON object to be sent, and add the name of the new cat to it.
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("name", name);
                Log.i("JSON", jsonParam.toString());

                // Create a data output stream and write jsonParam.
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                // Flush and close the output stream.
                os.flush();
                os.close();

                // Log the response of the server connection.
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                // Terminate the connection.
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }


            // Move back to main activity.
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

            // Closing this screen.
            finish();

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}