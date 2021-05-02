package com.zamashops.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zamashops.MainLoginActivity;
import com.zamashops.R;
import com.zamashops.activities.ProfileActivity;
import com.zamashops.activities.SellerProfileActivity;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.UserModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.ViewHolder> {

    ArrayList<UserModel> items;
    ArrayList<Integer> colors;
    Context context;



    public AllUserAdapter(Context context, ArrayList<UserModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_followers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final UserModel item = items.get(position);
        holder.txt_user_name.setText(item.getUser_name());
        holder.ratingBar.setRating(Float.parseFloat(item.getReviews()));

        String image_name = "";
        if (!item.getUser_image().isEmpty()) {
            if (item.getUser_image().contains("/")) {
                image_name = item.getUser_image();
            } else {
                image_name = context.getResources().getString(R.string.url_image) + item.getUser_image();
            }
            Picasso.get().load(image_name)
                    .placeholder(R.drawable.loding_blue)
                    .error(R.drawable.logo)
                    .into(holder.profile_image);
        }

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));


        holder.profile_image.setCircleBackgroundColor(color);


        if (item.getFollow()) {

            holder.btn_follow.setText("UnFollow");
            holder.btn_follow.setTextColor(Color.rgb(255, 255, 255));
            holder.btn_follow.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.btn_follow.setCompoundDrawables(null, null, null, null);


        } else {
            holder.btn_follow.setText("Follow");
            holder.btn_follow.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.btn_follow.setBackground(context.getResources().getDrawable(R.drawable.button_background));
            holder.btn_follow.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_person_add_blue_24dp), null, null, null);
            holder.btn_follow.setCompoundDrawablePadding(10);

        }

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (logPre.getBoolean("login_stauts", false)) {

                    String token = "";
                    if (item.getToken() != null) {
                        token = item.getToken();
                    }

                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", logPre.getString("user_id", ""));
                    params.put("follow_user_id", item.getUser_id());
                    params.put("token", token);


                    CustomVolley.getInsertPOSTData2(context, params, "add_follow.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {


                                boolean error = result.getBoolean("error");
                                if (error) {
                                    Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
                                } else {

                                    boolean fav_insert = result.getBoolean("follow_insert");

                                    if (fav_insert) {
                                        Toast.makeText(context, result.getString("follow_msg"), Toast.LENGTH_SHORT).show();
                                        item.setFollow(true);
                                        notifyDataSetChanged();
//                                        holder.btn_follow.setText("UnFollow");
//                                        holder.btn_follow.setTextColor(Color.rgb(255, 255, 255));
//                                        holder.btn_follow.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
//                                        holder.btn_follow.setCompoundDrawables(null, null, null, null);

                                    } else {
                                        Toast.makeText(context, result.getString("follow_msg"), Toast.LENGTH_SHORT).show();
                                        item.setFollow(false);
                                        notifyDataSetChanged();
//                                        holder.btn_follow.setText("Follow");
//                                        holder.btn_follow.setTextColor(context.getResources().getColor(R.color.colorAccent));
//                                        holder.btn_follow.setBackground(context.getResources().getDrawable(R.drawable.button_background));
//                                        holder.btn_follow.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_person_add_blue_24dp), null, null, null);


                                    }


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(context, MainLoginActivity.class);
                    intent.putExtra("status", true);
                    context.startActivity(intent);
                }


            }
        });

        if (logPre.getString("user_id", "").equals(item.getUser_id())) {
            holder.btn_follow.setVisibility(View.GONE);
        } else {
            holder.btn_follow.setVisibility(View.VISIBLE);
        }


        holder.lin_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (logPre.getString("user_id", "").equals(item.getUser_id())) {

                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, SellerProfileActivity.class);
                    intent.putExtra("user_id", item.getUser_id());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView txt_user_name;
        RatingBar ratingBar;

        Button btn_follow;
        LinearLayout lin_user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btn_follow = itemView.findViewById(R.id.btn_follow);

            lin_user = itemView.findViewById(R.id.lin_user);


        }
    }


}
