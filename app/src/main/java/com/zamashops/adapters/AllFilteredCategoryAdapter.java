package com.zamashops.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zamashops.R;
import com.zamashops.SQLiteDB;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.CityModel;

import java.util.ArrayList;

public class AllFilteredCategoryAdapter extends RecyclerView.Adapter<AllFilteredCategoryAdapter.ViewHolder> {

    ArrayList<CategoryModel> items;
    Context context;
    SQLiteDB db;

    public AllFilteredCategoryAdapter(Context context, ArrayList<CategoryModel> items) {
        this.context = context;
        this.items = items;
        db = new SQLiteDB(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_filter_city_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final CategoryModel item = items.get(position);
        holder.txt_cat_city.setText(item.getCat_name());
        holder.img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.deleteCategory(item.getCat_id())){
                    items.remove(position);
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context,"Item Not Removed!",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_cancel;
        TextView txt_cat_city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_cancel = itemView.findViewById(R.id.img_cancel);
            txt_cat_city = itemView.findViewById(R.id.txt_cat_city);


        }
    }


}
