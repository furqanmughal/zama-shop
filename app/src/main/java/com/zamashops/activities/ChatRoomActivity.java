package com.zamashops.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;
import com.zamashops.R;
import com.zamashops.adapters.ChatRoomAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.MessageModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;

public class ChatRoomActivity extends AppCompatActivity {

    Context context = ChatRoomActivity.this;
    RecyclerView reyclerview_message_list;
    LinearLayout lin_empty_list;

    EditText edittext_chatbox;


    CircleImageView img_product;
    TextView txt_product_name;

    LinearLayout lin_product;

    ArrayList<MessageModel> messageList = new ArrayList<>();
    ChatRoomAdapter adapter;

    String chat_id = "";
    String user_id_1 = "";
    String user_id_2 = "";
    String product_id = "";


    boolean shouldExecuteOnResume;


    LinearLayoutManager linearLayoutManager;


    View mCustomView;


    boolean activity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mCustomView = mInflater.inflate(R.layout.chat_action_bar, null);

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        shouldExecuteOnResume = false;

        img_product = mCustomView.findViewById(R.id.img_product);
        txt_product_name = mCustomView.findViewById(R.id.txt_product_name);
//        lin_product = findViewById(R.id.lin_product);
        reyclerview_message_list = findViewById(R.id.reyclerview_message_list);
        edittext_chatbox = findViewById(R.id.edittext_chatbox);
        lin_empty_list = findViewById(R.id.lin_empty_list);

        if (getIntent().hasExtra("activity")) {
            activity = getIntent().getBooleanExtra("activity", false);
        }

        if (getIntent().hasExtra("user_id_1")) {
            //    chat_id = getIntent().getStringExtra("chat_id");
            user_id_1 = getIntent().getStringExtra("user_id_1");
            user_id_2 = getIntent().getStringExtra("user_id_2");
            product_id = getIntent().getStringExtra("product_id");


        } else {
            //    finish();
            Toast.makeText(context, "some thing went wrong!", Toast.LENGTH_SHORT).show();
        }


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);


        reyclerview_message_list.setLayoutManager(linearLayoutManager);

        adapter = new ChatRoomAdapter(context, messageList);
        reyclerview_message_list.setAdapter(adapter);


        selectChat(50);


    }

    @Override
    protected void onResume() {
        super.onResume();
        selectProduct();

        if (shouldExecuteOnResume) {

            selectChat(50);
        } else {
            shouldExecuteOnResume = true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("chatroom"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("chatroom")) {
                messageList.clear();
                selectChat(50);

            }


        }
    };


    void selectProduct() {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);


        CustomVolley.getInsertPOSTData2(context, params, "fetch_detail_product.php", new VolleyCallback() {
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


                        JSONArray user_data = result.getJSONArray("products");

                        for (int i = 0; i < user_data.length(); i++) {
                            final JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");

                            for (int count = 0; count < images.length(); count++) {
                                Picasso.get().load(context.getResources().getString(R.string.url_image) + images.getJSONObject(count).getString("product_image"))
                                        .placeholder(R.drawable.loding_blue)
                                        .error(R.drawable.image_not_found)
                                        .into(img_product);
                                break;
                            }

                            txt_product_name.setText(p.getString("product_name"));

                            mCustomView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ProductDetailActivity.class);
                                    intent.putExtra("product_id", product_id);
                                    startActivity(intent);
                                }
                            });


                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    void selectChat(final int limit) {


        HashMap<String, String> params = new HashMap<>();

        params.put("chat_id", chat_id);
        params.put("user_id_1", user_id_1.trim());
        params.put("user_id_2", user_id_2.trim());
        params.put("product_id", product_id.trim());


        CustomVolley.getInsertPOSTData2(context, params, "chat/fetch_chat_messages.php", new VolleyCallback() {
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

                        JSONArray user_data = result.getJSONArray("messages");

                        messageList.clear();

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject p = user_data.getJSONObject(i);

                            String user_image = "";
                            if (p.getString("account_type").equals("1")) {
                                user_image = getResources().getString(R.string.url_image) + p.getString("user_image");
                            } else {
                                user_image = p.getString("user_image");
                            }


                            MessageModel model = new MessageModel(
                                    p.getString("message_id"),
                                    p.getString("message"),
                                    p.getString("time"),
                                    p.getString("date"),
                                    p.getString("sender_user_id"),
                                    p.getString("user_name"),
                                    user_image,
                                    p.getString("message_status"));

                            chat_id = p.getString("chat_id");

                            if (p.getString("sender_user_id").equals(logPre.getString("user_id", ""))) {
                                model.setType("sender");
                            }

                            messageList.add(0, model);

                        }


                        adapter.notifyDataSetChanged();
                        reyclerview_message_list.scrollToPosition(messageList.size() - 1);

                        insertStatus();


                        if (messageList.size() == 0) {
                            lin_empty_list.setVisibility(View.VISIBLE);
                            reyclerview_message_list.setVisibility(View.GONE);
                        } else {
                            lin_empty_list.setVisibility(View.GONE);
                            reyclerview_message_list.setVisibility(View.VISIBLE);

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void sendSms(View view) {

        final String sms = edittext_chatbox.getText().toString();

        if (!sms.isEmpty()) {

            MessageModel model = new MessageModel(
                    "-1",
                    sms,
                    "sending",
                    "",
                    "",
                    "",
                    "",
                    "1");

            model.setType("sender");

            messageList.add(model);
            adapter.notifyItemInserted(messageList.size() - 1);


            edittext_chatbox.setText("");



            String strings = context.getResources().getString(R.string.url) + "chat/send_sms.php";
            StringRequest request = new StringRequest(Request.Method.POST, strings, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject result = new JSONObject(response);
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


                                chat_id = result.getString("chat_id");


                                selectChat(50);

//                            linearLayoutManager.scrollToPosition(messageList.size()-1);
//                            reyclerview_message_list.scrollToPosition(0);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                    edittext_chatbox.setText(sms);
//
                    Alerter.create((Activity) context)
                            .setTitle("Connection Problem")
                            .setText("No Internet Connection / Server Problem. Try Again Later")
                            .setBackgroundColorRes(R.color.danger) // or setBackgroundColorInt(Color.CYAN)
                            .show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("message", sms);
                    params.put("user_name", logPre.getString("user_name", ""));
                    params.put("product_id", product_id);
                    if (logPre.getBoolean("login_stauts", false)) {
                        params.put("user_id", logPre.getString("user_id", ""));
                    }
                    if (logPre.getString("user_id", "").equals(user_id_1)) {
                        params.put("user_id_sender", user_id_1);
                        params.put("user_id_reciver", user_id_2);
                    } else {
                        params.put("user_id_sender", user_id_2);
                        params.put("user_id_reciver", user_id_1);
                    }
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MySingleton.getInstance(context).addToRequestQueue(request);


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
        if (activity) {
            finish();
        } else {
            Intent intent = new Intent(context, AllChatActivity.class);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (activity) {
                    finish();
                } else {
                    Intent intent = new Intent(context, AllChatActivity.class);
                    finish();
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////////////////////Back Press//////////////////////////////////////////////////////////////////


    void insertStatus() {
        HashMap<String, String> params = new HashMap<>();
        params.put("update_status", String.valueOf(true));
        params.put("chat_id", chat_id);
        params.put("user_id", logPre.getString("user_id", ""));

        CustomVolley.getInsertPOSTData3(context, params, "chat/update_message_status.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

            }
        });

    }

}
