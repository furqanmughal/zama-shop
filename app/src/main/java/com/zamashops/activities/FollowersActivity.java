package com.zamashops.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zamashops.R;
import com.zamashops.adapters.AllUserAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.UserModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class FollowersActivity extends AppCompatActivity {
    RecyclerView list_followers;
    LinearLayout lin_empty_list, lin_follower;

    ArrayList<UserModel> userModels = new ArrayList<>();
    boolean shouldExecuteOnResume;

    Context context = FollowersActivity.this;
    AllUserAdapter userAdapter;

    int page_no = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    ProgressBar progress_circular;
    boolean loadmore = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        shouldExecuteOnResume = false;


        list_followers = findViewById(R.id.list_followers);
        lin_empty_list = findViewById(R.id.lin_empty_list);
        lin_follower = findViewById(R.id.lin_follower);
        progress_circular = findViewById(R.id.progress_circular);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_followers.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_followers.getContext(),
                linearLayoutManager.getOrientation());
        list_followers.addItemDecoration(dividerItemDecoration);

        list_followers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();


                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (loadmore) {
                            selectData(20);
                        }
                    }

                }
            }
        });


        userAdapter = new AllUserAdapter(context, userModels);
        list_followers.setAdapter(userAdapter);

        selectData(20);
    }

    void selectData(final int limit) {

        progress_circular.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("limit_count", String.valueOf(page_no++));
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }

        CustomVolley.getInsertPOSTData2(context, params, "user/fetch_follower_user.php", new VolleyCallback() {
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

                        JSONArray user_data = result.getJSONArray("user");


                        if (user_data.length() == 0) {
                            loadmore = false;

                        }

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject user = user_data.getJSONObject(i);

                            UserModel model = new UserModel(
                                    user.getString("user_id"), user.getString("user_name"), user.getString("user_email"),
                                    user.getString("user_contact"), user.getString("user_address"), user.getString("user_password"),
                                    user.getString("account_type"),
                                    user.getString("user_image"), user.getBoolean("follow"), user.getString("reviews")
                            );


                            userModels.add(model);
                        }


                        userAdapter.notifyDataSetChanged();

                        if (userModels.size() == 0) {

                            lin_follower.setVisibility(View.GONE);
                            lin_empty_list.setVisibility(View.VISIBLE);

                        }

                        progress_circular.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    progress_circular.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldExecuteOnResume) {
            userModels.clear();
            int temp = page_no;
            page_no = 0;
            selectData(20 * temp);
            page_no = temp;

        } else {
            shouldExecuteOnResume = true;
        }
    }
}
