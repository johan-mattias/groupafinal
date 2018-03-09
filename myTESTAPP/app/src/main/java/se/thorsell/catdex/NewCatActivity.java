package se.thorsell.catdex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.view.View;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.net.Uri;

public class NewCatActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;
    ProgressDialog prgDialog;

    EditText inputName;
    String imageString = "image_bytes";

    // url to create new product
    private static String url_create_cat = "http://178.62.50.61/android_connect/create_cat.php";

    // variables to load images from gallery
    private static int RESULT_LOAD_IMG = 1;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    // Takes a bitmap, compresses it to a png with quality setting "100" and returns a Base64encode.
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        String encodedString = "temp";
       try {
           encodedString = URLEncoder.encode(temp, "UTF-8");
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
        }
        return encodedString;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cat);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);

        // Edit Text
        inputName = (EditText) findViewById(R.id.inputName);

        // Create button
        Button btnCreateCat = (Button) findViewById(R.id.btnCreateCat);

        // button click event
        btnCreateCat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewCat().execute();
            }
        });
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");

        // Start the Intent
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
                jsonParam.put("image", imageString); // This is the encoded image
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
            // todo figure out if this should be in the onPostExecute instead. Maybe ask Johan?
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
                //File absoluteFilePath = new File(new URI(selectedImage.getPath())); this breaks everything?
                Log.d("Trams123", "Selected image to string: " + selectedImage.toString());

                // Display the chosen image in the app.
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                imgView.setImageURI(selectedImage);

                Log.d("Trams123", "Data to string: " + data.toString());
                Log.d("Trams123", "Data get data to string: " + data.getData().toString());
                Log.d("Trams123", "Selected image get path to string: " + selectedImage.getPath().toString());

                // set bmp as a bitmap of the image that was selected from the gallery.
                InputStream input;
                Bitmap bmp = null;
                try {
                    input = this.getContentResolver().openInputStream(selectedImage);
                    bmp = BitmapFactory.decodeStream(input);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                Log.d("Trams123", "Bitmap: " + bmp);

                // Convert that bitmap into a string.
                imageString = BitMapToString(bmp);

                Log.d("Trams123", "Image string: " + imageString);

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
    }
}