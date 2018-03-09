package se.thorsell.catdex;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    ListView SubjectListView;
    ListView listView;
    ProgressBar progressBarSubject;
    String ServerURL = "http://178.62.50.61/android_connect/get_catsTagsMap.php";
    EditText editText;
    List<String> listStringTag = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        SubjectListView = findViewById(R.id.listview1);
        progressBarSubject = findViewById(R.id.progressBar);
        editText = findViewById(R.id.edittext1);

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
        public Context context;

        String ResultHolder;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                listStringTag.add(jsonObject.getString("tag"));
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
            arrayAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, listStringTag);

            // Populate ListView with items from ArrayAdapter
            SubjectListView.setAdapter(arrayAdapter);

            // Set an item click listener for ListView
            SubjectListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                List<String> catNames = new ArrayList<String>();

                listView = findViewById(R.id.listview1);

                listView.setVisibility(View.VISIBLE);

                JSONArray jsonArraySecond;

                try {
                    jsonArraySecond = new JSONArray(ResultHolder);
                    JSONObject jsonObjectSecond;
                    
                    for (int i = 0; i < jsonArraySecond.length(); i++) {
                        jsonObjectSecond = jsonArraySecond.getJSONObject(i);

                        //Check for the corresponding cat name to the tag that has been clicked
                        if (jsonObjectSecond.getString("tag").equals(selectedItem)) {
                            catNames.add(jsonObjectSecond.getString("name"));
                        }
                    }
                    //catNames.add("Hej");
                    if (catNames.isEmpty()) {
                        Log.e("Search filter", "CatNames empty");
                    } else {
                        adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, catNames);
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
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
