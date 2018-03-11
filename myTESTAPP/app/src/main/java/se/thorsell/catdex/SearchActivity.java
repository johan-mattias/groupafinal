package se.thorsell.catdex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    private ListView SubjectListView;
    private ListView listView;
    private ProgressBar progressBarSubject;
    private final String ServerURL = "http://178.62.50.61/android_connect/get_catsTagsMap.php";
    private EditText editText;
    private final List<String> listStringTag = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> adapter;

    public GridView gridView;

    // for the grid view
    ArrayList<Cat> catArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        SubjectListView = findViewById(R.id.listview1);
        progressBarSubject = findViewById(R.id.progressBar);
        editText = findViewById(R.id.edittext1);

        Log.d("SearchActivity", "In on Create, grid view: " + gridView);

        new GetHttpResponse(SearchActivity.this).execute();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                SearchActivity.this.arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {
        public final Context context;

        String ResultHolder;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpServicesClass httpServiceObject = new HttpServicesClass(ServerURL);
            try {
                httpServiceObject.ExecutePostRequest();

                if (httpServiceObject.getResponseCode() == 200) {
                    ResultHolder = httpServiceObject.getResponse();

                    if (ResultHolder != null) {
                        JSONArray jsonArray;

                        try {
                            jsonArray = new JSONArray(ResultHolder);
                            JSONObject jsonObject;
                            String tag;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                tag = jsonObject.getString("tag");
                                // If the tag isn't in the list of tags, add it.
                                if (!listStringTag.contains(tag)) {
                                    listStringTag.add(tag);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(context, httpServiceObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            progressBarSubject.setVisibility(View.GONE);
            SubjectListView.setVisibility(View.VISIBLE);

            // Create a ArrayAdapter from List
            arrayAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, listStringTag);

            // Populate ListView with items from ArrayAdapter
            SubjectListView.setAdapter(arrayAdapter);

            // Set an item click listener for ListView
            SubjectListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                List<String> catNames = new ArrayList<>();

                listView = findViewById(R.id.listview1);

                listView.setVisibility(View.VISIBLE);

                JSONArray jsonArraySecond;

                try {
                    jsonArraySecond = new JSONArray(ResultHolder);
                    JSONObject jsonObjectSecond;
                    
                    for (int i = 0; i < jsonArraySecond.length(); i++) {
                        jsonObjectSecond = jsonArraySecond.getJSONObject(i);

                        // Check for the corresponding cat name to the tag that has been clicked.
                        if (jsonObjectSecond.getString("tag").equals(selectedItem)) {
                            catNames.add(jsonObjectSecond.getString("name"));
                            String imageString = jsonObjectSecond.getString("image");

                            // Convert image string to bitmap.
                            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                            Bitmap catImage = BitmapFactory.decodeByteArray(decodedString, 0,
                                    decodedString.length);

                            // Add the converted image to the list.
                            Cat tempCat = new Cat(jsonObjectSecond.getString("name"), catImage);
                            catArray.add(tempCat);
                        }
                    }
                    if (catNames.isEmpty()) {
                        Log.e("Search filter", "CatNames empty");
                    } else {
                        adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, catNames);
                        if(adapter.getCount()==0) {
                            Log.e("Search filter", "adapter null");
                        }
                        else {
                            //Remove the search bar from the view with cat names
                            editText.setVisibility(View.GONE);

                            //Populate listView with items from adapter (the catNames)
                            listView.setAdapter(adapter);

                            //Remove the onClickListener when the cat names are showing
                            SubjectListView.setOnItemClickListener(null);

                            // Grid view for the cats
                            setContentView(R.layout.grid);
                            gridView = findViewById(R.id.gridview);

                            CatsAdapter catsAdapter = new CatsAdapter(getApplicationContext(), catArray);
                            if (gridView == null) {
                                Log.e("SearchActivity", "Grid view is null: " + gridView);
                            } else {
                                Log.d("SearchActivity", "Grid view isn't null!" + gridView);
                            }

                            if (catsAdapter == null) {
                                Log.e("SearchActivity", "cats adapter is null: " + catsAdapter);
                                } else {
                                Log.d("SearchActivity", "cats adapter isn't null!: " + catsAdapter);
                            }
                            gridView.setAdapter(catsAdapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
