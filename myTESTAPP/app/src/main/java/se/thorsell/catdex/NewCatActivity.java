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

import android.app.ProgressDialog;

import android.widget.Toast;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.provider.MediaStore;
import android.net.Uri;
import android.database.Cursor;





import se.thorsell.catdex.R;

public class NewCatActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;
    ProgressDialog prgDialog;

    String encodedString;
    String imgPath, fileName;
    Bitmap bitmap;



    JSONParser jsonParser = new JSONParser();
    EditText inputName;

    // url to create new product
    private static String url_create_cat = "http://178.62.50.61/android_connect/create_cat.php";

    // variables to load images from gallery
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cat);
        setContentView(R.layout.add_cat);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);



        // Edit Text
        inputName = (EditText) findViewById(R.id.inputName);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateCat);

        ImageView imgView = (ImageView) findViewById(R.id.imgView);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewCat().execute();

                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, RESULT_LOAD_IMG);
            }
        });
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        //Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
       // startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMG);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When we choose an image from Gallery
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {

                // Get the image from data
                Uri selectedImage = data.getData();
                Log.d("uri", selectedImage.toString());
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();


                // Display the chosen image in the app.
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                imgView.setImageURI(selectedImage);

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

    }
}