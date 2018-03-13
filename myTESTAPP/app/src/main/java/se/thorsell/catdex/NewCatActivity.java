package se.thorsell.catdex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.net.Uri;

public class NewCatActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_VIEW_GALLERY = 1;

    // Progress Dialog
    private ProgressDialog pDialog;
    private ProgressDialog prgDialog;

    private EditText inputName;
    private String imageString = "image_bytes_placeholder";

    private EditText inputTag;

    // url to create new product
    private static final String url_create_cat = "http://178.62.50.61/android_connect/create_cat_and_tag.php";

    // variables to load images from gallery
    private static final int RESULT_LOAD_IMG = 1;

    // Temp image
    private ImageView tmpImage;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_VIEW_GALLERY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    // Create intent to Open Image applications like Gallery, Google Photos
                    Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");

                    // Start the Intent
                    startActivityForResult(gallery, RESULT_LOAD_IMG);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Please provide Gallery permission!", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    // Takes a bitmap, compresses it to a png with quality setting "100" and returns a Base64encode.
    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cat);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);

        // Edit Text
        inputName = findViewById(R.id.inputName);
        inputTag = findViewById(R.id.inputTag);

        // Set temp image
        tmpImage = findViewById(R.id.imgView);
        tmpImage.setImageResource(R.drawable.noimageselected);

        // Create button
        Button btnCreateCat = findViewById(R.id.btnCreateCat);

        Button btnLoadImage = findViewById(R.id.btnLoadImage);

        // button click event
        btnCreateCat.setOnClickListener(view -> {
            // creating new cat in background thread
            new CreateNewCat().execute();
        });

        btnLoadImage.setOnClickListener(view -> {
            // loading image
            loadImageFromGallery();
        });
    }

    public void loadImageFromGallery() {

        // Check if we have the correct permissions.
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_VIEW_GALLERY);

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_VIEW_GALLERY);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            // Create intent to Open Image applications like Gallery, Google Photos
            Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");

            // Start the Intent
            startActivityForResult(gallery, RESULT_LOAD_IMG);
        }
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
            String tag = inputTag.getText().toString();

            // This try-catch section a modification of: https://stackoverflow.com/a/42780501
            try {
                // Connect to the server.
                URL url = new URL(url_create_cat);
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
                jsonParam.put("tag", tag);
                if (Debug.LOG) {
                    Log.i("JSON", jsonParam.toString());
                }

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
            Toast.makeText(NewCatActivity.this, "Your cat has been added!", Toast.LENGTH_LONG).show();
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

                assert selectedImage != null;
                if (Debug.LOG) {
                    Log.d("NewCat image", "Selected image to string: " + selectedImage.toString());
                }

                // Display the chosen image in the app.

                tmpImage = null;
                ImageView imgView = findViewById(R.id.imgView);
                imgView.setImageURI(selectedImage);

                if (Debug.LOG) {
                    Log.d("NewCat image", "Data to string: " + data.toString());
                    Log.d("NewCat image", "Data get data to string: " + data.getData());
                    Log.d("NewCat image", "Selected image get path to string: " + selectedImage.getPath());
                }

                // set bmp as a bitmap of the image that was selected from the gallery.
                InputStream input;
                Bitmap bmp = null;
                try {
                    input = this.getContentResolver().openInputStream(selectedImage);
                    bmp = BitmapFactory.decodeStream(input);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (Debug.LOG) {
                    Log.d("NewCat image", "Bitmap: " + bmp);
                }

                // Convert that bitmap into a string.
                imageString = BitMapToString(bmp);
                if (imageString == null && Debug.LOG) {
                    Log.e("NewCat image", "Image string was null!");
                } else {
                    if (Debug.LOG) {
                        Log.d("NewCat image", "imageString not null!");
                    }
                }
                if (Debug.LOG) {
                    Log.d("NewCat image", "Image string: " + imageString);
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
    }
}