package com.zamashops.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zamashops.R;
import com.zamashops.SQLiteDB;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.CityModel;

import java.util.ArrayList;

public class AllFilterCategoryAdapter extends RecyclerView.Adapter<AllFilterCategoryAdapter.ViewHolder> {

    ArrayList<CategoryModel> items;
    Context context;
    SQLiteDB db;

    public AllFilterCategoryAdapter(Context context, ArrayList<CategoryModel> items) {
        this.context = context;
        this.items = items;
        db = new SQLiteDB(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_select_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final CategoryModel item = items.get(position);
        holder.chb_city.setText(item.getCat_name());
        holder.chb_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.chb_city.isChecked()){
                    ContentValues values = new ContentValues();
                    values.put(SQLiteDB.cat_id,item.getCat_id());
                    values.put(SQLiteDB.cat_name,item.getCat_name());
                    db.insertData(SQLiteDB.category_tbl,values);

                }else{
                    db.deleteCity(item.getCat_id());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox chb_city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chb_city = itemView.findViewById(R.id.chb_city);


        }
    }


}
