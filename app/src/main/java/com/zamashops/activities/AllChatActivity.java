package com.zamashops.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zamashops.MainActivity;
import com.zamashops.R;
import com.zamashops.adapters.AllChatAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.ChatModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class AllChatActivity extends AppCompatActivity {


    boolean shouldExecuteOnResume;
    Context context = AllChatActivity.this;
    RecyclerView list_chat;
    LinearLayout lin_empty_list;

    ArrayList<ChatModel> items = new ArrayList<>();
    AllChatAdapter adapter;

    int limit_count = 0;
    ProgressBar progress_circular;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loadmore = true;

    boolean activity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chat);

        shouldExecuteOnResume = false;

        list_chat = findViewById(R.id.list_chat);
        lin_empty_list = findViewById(R.id.lin_empty_list);
        progress_circular = findViewById(R.id.progress_circular);

        if(getIntent().hasExtra("activity")){
            activity = getIntent().getBooleanExtra("activity",false);
        }



        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_chat.setLayoutManager(linearLayoutManager);
        selectChat(30);
        adapter = new AllChatAdapter(context, items);
        list_chat.setAdapter(adapter);

//        list_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) //check for scroll down
//                {
//                    visibleItemCount = linearLayoutManager.getChildCount();
//                    totalItemCount = linearLayoutManager.getItemCount();
//                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//
//                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                        if (loadmore) {
//                            selectChat(30);
//                        }
//                    }
//
//                }
//            }
//        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("allchat"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            if (action.equals("allchat")) {

                int temp = limit_count;
                limit_count = 0;
                selectChat(30 * temp);
                limit_count = temp;
            }


        }
    };


    void selectChat(final int limit) {




        progress_circular.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
//        params.put("limit", String.valueOf(limit));
//        params.put("limit_count", String.valueOf(limit_count++));
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }



        CustomVolley.getInsertPOSTData2(context, params, "chat/fetch_all_chat.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage(result.getString("msg"))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  finish();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        JSONArray user_data = result.getJSONArray("chats");

                        if (user_data.length() == 0) {
                            loadmore = false;
                        }
                        items.clear();

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject p = user_data.getJSONObject(i);


                            ChatModel model = new ChatModel(
                                    p.getString("chat_id"), p.getString("product_name"), p.getString("product_image"),
                                    p.getString("product_id"),
                                    p.getString("message"), p.getString("date"), p.getString("count"),p.getString("account_type")
                            );

                            model.setUser_id_1(p.getString("user_id_1"));
                            model.setUser_id_2(p.getString("user_id_2"));




                            items.add(model);


                        }


                        if (items.size() == 0) {
                            lin_empty_list.setVisibility(View.VISIBLE);
                            list_chat.setVisibility(View.GONE);
                        } else {
                            lin_empty_list.setVisibility(View.GONE);
                            list_chat.setVisibility(View.VISIBLE);
                        }


                        adapter.notifyDataSetChanged();

                    }

                    progress_circular.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){

            int temp = limit_count;
            limit_count = 0;
            selectChat(30 * temp);
            limit_count = temp;

        } else{
            shouldExecuteOnResume = true;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);

    }


    ////////////////////Back Press//////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(activity){
            finish();
        }else {
            Intent intent = new Intent(context, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if(activity){
                    finish();
                }else {
                    Intent intent = new Intent(context,MainActivity.class);
                    finish();
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////////////////////Back Press//////////////////////////////////////////////////////////////////



}
