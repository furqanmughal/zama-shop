package com.zamashops.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.UploadImageModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.CategoryListViewHolder> {

    Context context;
    ArrayList<UploadImageModel> items;
    String product_id;

    public GalleryAdapter(Context context, ArrayList<UploadImageModel> items,String id) {
        this.context = context;
        this.items = items;
        product_id = id;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_uploadimage, viewGroup, false);

        return new GalleryAdapter.CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryListViewHolder viewHolder, final int i) {
        final UploadImageModel item = items.get(i);

        if(item.isCheck()) {
            viewHolder.img_gallery_image.setImageBitmap(item.getImage());
            viewHolder.btn_cancel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(i);
                    notifyDataSetChanged();
                }
            });
        }else{
            Picasso.get().load(context.getResources().getString(R.string.url_image)  + item.getStringImage())
                    .placeholder(R.drawable.loding_blue)
                    .error(R.drawable.image_not_found)
                    .into( viewHolder.img_gallery_image);

            viewHolder.btn_cancel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                            deletePost(item.getStringImage(),i);


                }
            });

        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CategoryListViewHolder extends RecyclerView.ViewHolder {

        ImageView img_gallery_image;
        ImageView btn_cancel_image;

        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);

            img_gallery_image =  itemView.findViewById(R.id.img_gallery_image);
            btn_cancel_image =  itemView.findViewById(R.id.btn_cancel_image);


        }
    }

    void deletePost(String image, final int position) {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);
        params.put("product_image", image);


        CustomVolley.getInsertPOSTData(context, params, "delete_post_image.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        Toast.makeText(context,"Not Removed",Toast.LENGTH_SHORT).show();
                    } else {

                        items.remove(position);
                        notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }




}