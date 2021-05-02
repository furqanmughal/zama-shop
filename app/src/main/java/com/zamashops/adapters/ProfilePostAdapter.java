package com.zamashops.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zamashops.R;
import com.zamashops.activities.ProductDetailActivity;
import com.zamashops.activities.UpdateAdsActivity2;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder> {

    ArrayList<ProductModel> items;
    Context context;

    boolean check = true;
    boolean pause_check = false;

    public ProfilePostAdapter(Context context, ArrayList<ProductModel> items, int check) {
        this.context = context;
        this.items = items;

        if (check == 1) {
            this.check = true;
        } else {
            this.check = false;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ProductModel item = items.get(position);
        holder.txt_category.setText(item.getSub_category_id());
        holder.txt_product_name.setText(item.getName());
        holder.txt_price.setText(item.getPrice() + " RS");
        holder.txt_view.setText(item.getViews());


        if (item.getProduct_type().equals("3")) {
            holder.txt_highlighted.setVisibility(View.VISIBLE);
        } else if (item.getProduct_type().equals("2")) {
            holder.txt_highlighted.setVisibility(View.VISIBLE);
            holder.txt_highlighted.setText("Pending");
        } else {
            holder.txt_highlighted.setVisibility(View.GONE);
        }


        if (item.getProduct_images().length > 0) {

            Glide
                    .with(context)
                    .load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
                    .placeholder(R.drawable.placeholder_thumbnail)
                    .error(R.drawable.placeholder_thumbnail)
                    .into(holder.img_product);

//            Picasso.get().load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
//                    .placeholder(R.drawable.loding_blue)
//                    .error(R.drawable.image_not_found)
//                    .into(viewHolder.img_list_product);

        } else {
            holder.img_product.setImageResource(R.drawable.empty_logo);
        }


        holder.txt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.shareurl) + item.getProduct_id());
                ((Activity) context).startActivity(Intent.createChooser(shareIntent, "Share link using"));

            }
        });


        holder.txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.txt_delete);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.product_delete_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem mItem) {
                        if (mItem.getItemId() == R.id.menu_sold_out) {
                            MyDialog dialog = new MyDialog(context, "Sold Out!", "Sold Out On ZamaShops?");
                            dialog.onPositiveClick("Yes", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    dialog.dismiss();
                                    deletePost(item.getProduct_id(), position, "delete");

                                }
                            });
                            dialog.showCancel();
                            dialog.setDialog(MyDialog.DANGER);
                        } else if (mItem.getItemId() == R.id.menu_remove) {
                            MyDialog dialog = new MyDialog(context, "Danger!", "Are you sure to Remove this post!");
                            dialog.onPositiveClick("Yes", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    dialog.dismiss();
                                    deletePost(item.getProduct_id(), position, "removed");

                                }
                            });
                            dialog.showCancel();
                            dialog.setDialog(MyDialog.DANGER);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup

            }
        });

        holder.lin_profile_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", item.getProduct_id());
                context.startActivity(intent);
            }
        });

        holder.txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateAdsActivity2.class);
                intent.putExtra("product_id", item.getProduct_id());
                context.startActivity(intent);
            }
        });

//        if (item.getProduct_status().equals("0")) {
//
//            holder.switch_active.setChecked(false);
//        } else {
//            holder.switch_active.setChecked(true);
//        }

        holder.switch_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(pause_check){
                    pause_check = false;
                    return;
                }
                String message = "Are you sure to pause this post!";
                if (holder.switch_active.isChecked()) {
                    message = "Are you sure to Active this post!";
                } else {
                    message = "Are you sure to pause this post!";
                }

                MyDialog dialog = new MyDialog(context, "Danger!", message);
                dialog.onPositiveClick("Yes", new myOnClickListener() {
                    @Override
                    public void onButtonClick(MyDialog dialog) {
                        dialog.dismiss();

                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id", logPre.getString("user_id", ""));
                        params.put("product_id", item.getProduct_id());

                        CustomVolley.getInsertPOSTData2(context, params, "user/product_status.php", new VolleyCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {


                                try {

                                    boolean error = result.getBoolean("error");
                                    if (error) {
                                        Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
                                    } else {

                                        boolean product_active = result.getBoolean("product_active");

                                        if (product_active) {
                                            Toast.makeText(context, result.getString("product_msg"), Toast.LENGTH_SHORT).show();
                                            //        holder.txt_active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_pause_24,0,0,0);
                                            item.setProduct_status("1");
                                            notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(context, result.getString("product_msg"), Toast.LENGTH_SHORT).show();
                                            //      holder.txt_active.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_play_arrow_24,0,0,0);
                                            item.setProduct_status("0");
                                            notifyDataSetChanged();
                                        }


                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }
                });
               dialog.onCancelClick("cancel", new myOnClickListener() {
                   @Override
                   public void onButtonClick(MyDialog dialog) {
                       dialog.dismiss();
                       pause_check = true;
                       holder.switch_active.setChecked(!holder.switch_active.isChecked());
                   }
               });
                dialog.setDialog(MyDialog.DANGER);

            }

        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        LinearLayout lin_profile_post;
        TextView txt_category, txt_product_name, txt_view, txt_favrouit, txt_edit, txt_delete, txt_share, txt_price, txt_active;
        TextView txt_highlighted;
        Switch switch_active;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_view = itemView.findViewById(R.id.txt_view);
            txt_favrouit = itemView.findViewById(R.id.txt_favrouit);
            txt_edit = itemView.findViewById(R.id.txt_edit);
            txt_delete = itemView.findViewById(R.id.txt_delete);
            lin_profile_post = itemView.findViewById(R.id.lin_profile_post);
            txt_share = itemView.findViewById(R.id.txt_share);

            txt_highlighted = itemView.findViewById(R.id.txt_highlighted);
            switch_active = itemView.findViewById(R.id.switch_active);

            switch_active.setChecked(check);


        }
    }

    void deletePost(String product_id, final int position, final String status) {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);
        params.put("status", status);


        CustomVolley.getInsertPOSTData(context, params, "delete_post.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        MyDialog dialog = new MyDialog(context, "success!", result.getString("msg"));
                        dialog.onPositiveClick("Ok", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                items.remove(position);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);
                    } else {


                        if (status.equals("removed")) {

                            new MyDialog(context, "Congragtulations!", "Your Post Removed Successfull!").onPositiveClick("Ok", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    items.remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });

                        } else {

                            new MyDialog(context, "Congragtulations!", "Your Post Deleted Successfull!").onPositiveClick("Ok", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    items.remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
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
}
