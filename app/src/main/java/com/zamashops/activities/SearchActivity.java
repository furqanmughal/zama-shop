package com.zamashops.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    EditText edt_search;
    Context context = SearchActivity.this;

    ArrayList<String> items = new ArrayList<>();
    ListView list_search;

    ArrayAdapter adapter;

    boolean checkRquest = false;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edt_search = findViewById(R.id.edt_search);
        list_search = findViewById(R.id.list_search);

        edt_search.requestFocus();

        adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,items);
        list_search.setAdapter(adapter);

        edt_search.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {



            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if(checkRquest){
                            handler.postDelayed(this, 0);
                        }else{
                            if(!edt_search.getText().toString().equals("") ) {
                                fetchSearchResult(edt_search.getText().toString());
                            }else{
                                items.clear();
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                };

                handler.postDelayed(r, 0);
            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Intent intent = new Intent(context,AllProductActivity.class);
                    intent.putExtra("search",edt_search.getText().toString());
                    startActivity(intent);
                }
                return false;
            }
        });


        list_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context,AllProductActivity.class);
                intent.putExtra("search",items.get(i));
                startActivity(intent);
            }
        });



    }



    void fetchSearchResult(String text) {

        checkRquest = true;
        items.clear();
        HashMap<String, String> params = new HashMap<>();
        params.put("search",text);
        CustomVolley.getInsertPOSTData2(context, params, "fetch_search_result.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                    } else {

                        JSONArray user_data = result.getJSONArray("result");


                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject p = user_data.getJSONObject(i);
                            items.add(p.getString("product_name"));
                        }

                        adapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                checkRquest = false;

            }
        });
    }

    public void closeSearch(View view) {
        finish();
    }
}
