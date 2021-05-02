package com.zamashops.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zamashops.R;
import com.zamashops.activities.ChatRoomActivity;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.models.ChatModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;

public class AllChatAdapter extends RecyclerView.Adapter<AllChatAdapter.ViewHolder> {

    ArrayList<ChatModel> items;
    Context context;

    public AllChatAdapter(Context context, ArrayList<ChatModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ChatModel item = items.get(position);
        holder.txt_sender_name.setText(item.getUser_name());
        holder.txt_date.setText(item.getDate());
        holder.txt_msg.setText(item.getFirst_message());
        holder.txt_count.setText(item.getCount());
        if (holder.txt_count.getText().toString().equals("0")) {
            holder.txt_count.setVisibility(View.GONE);
        }else{
            holder.txt_count.setVisibility(View.VISIBLE);
        }


        Random rnd = new Random();
        final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


        holder.sender_image.setCircleBackgroundColor(color);


        if (item.getUser_image().equals("")) {
            holder.sender_image.setVisibility(View.GONE);
            holder.txt_latter.setText(String.valueOf(item.getUser_name().toCharArray()[0]));
        } else {
            holder.sender_image.setVisibility(View.VISIBLE);
            holder.txt_latter.setVisibility(View.GONE);
                Picasso.get().load(context.getResources().getString(R.string.url_image) + item.getUser_image())
                        .placeholder(R.drawable.loding_blue)
                        .error(R.drawable.image_not_found)
                        .into(holder.sender_image);
        }

        holder.lin_chat.setBackgroundColor(context.getResources().getColor(R.color.white));

        holder.lin_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chat_id", item.getChat_id());
                intent.putExtra("user_id_1", item.getUser_id_1());
                intent.putExtra("user_id_2", item.getUser_id_2());
                intent.putExtra("product_id", item.getProduct_id());
                intent.putExtra("activity", true);
                context.startActivity(intent);
            }
        });

        holder.lin_chat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.lin_chat);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.chat_delete_menu, popup.getMenu());

                holder.lin_chat.setBackgroundColor(context.getResources().getColor(R.color.lightsilver2));

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem mItem) {
                        if (mItem.getItemId() == R.id.menu_delete) {
                            MyDialog dialog = new MyDialog(context, "Delete Chat!", "Are You Sure To Delete This Chat?");
                            dialog.onPositiveClick("Yes", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    dialog.dismiss();
                                    deleteChat(item.getProduct_id(), position,item.getChat_id());

                                }
                            });
                            dialog.showCancel();
                            dialog.setDialog(MyDialog.DANGER);
                        }
                        return true;
                    }
                });

                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        notifyDataSetChanged();

                    }
                });

                popup.show(); //showing popup
                return true;
            }
        });


    }

    private void deleteChat(String product_id, final int position,String chat_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("delete_status", "");
        params.put("chat_id", chat_id);
        params.put("product_id", product_id);
        params.put("delete_user_id", logPre.getString("user_id",""));


        CustomVolley.getInsertPOSTData(context, params, "chat/delete_chat.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        MyDialog dialog = new MyDialog(context, "Danger!", result.getString("msg"));
                        dialog.onPositiveClick("Ok", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);
                    } else {
                        new MyDialog(context, "success!",  result.getString("msg")).onPositiveClick("Ok", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                items.remove(position);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView sender_image;
        TextView txt_sender_name;
        TextView txt_latter;
        TextView txt_date;
        TextView txt_msg;
        TextView txt_count;


        LinearLayout lin_chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_latter = itemView.findViewById(R.id.txt_latter);
            sender_image = itemView.findViewById(R.id.sender_image);
            txt_sender_name = itemView.findViewById(R.id.txt_sender_name);
            txt_msg = itemView.findViewById(R.id.txt_msg);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_count = itemView.findViewById(R.id.txt_count);

            lin_chat = itemView.findViewById(R.id.lin_chat);


        }
    }


}
