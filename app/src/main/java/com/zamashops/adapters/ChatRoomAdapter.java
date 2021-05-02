package com.zamashops.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zamashops.R;
import com.zamashops.activities.SellerProfileActivity;
import com.zamashops.models.MessageModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;

public class ChatRoomAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    static private Context mContext;
    private ArrayList<MessageModel> mMessageList;

    public ChatRoomAdapter(Context context, ArrayList<MessageModel> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the Message.
    @Override
    public int getItemViewType(int position) {
        MessageModel message = mMessageList.get(position);

        if (message.getType().equals("sender")) {
            // If the current user is the sender of the Message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the Message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the Message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageModel message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(final MessageModel message) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());


        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, txt_latter;
        CircleImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            txt_latter = (TextView) itemView.findViewById(R.id.txt_latter);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(final MessageModel message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            nameText.setText(message.getUser_name());

            String image_name = "";
            if (!message.getUser_image().equals("")) {
                if (message.getUser_image().contains("/")) {
                    image_name = message.getUser_image();
                } else {
                    image_name = mContext.getResources().getString(R.string.url_image) + message.getUser_image();
                }


                Picasso.get().load(image_name)
                        .placeholder(R.drawable.loding_blue)
                        .error(R.drawable.image_not_found)
                        .into(profileImage);

            } else {
                profileImage.setVisibility(View.GONE);
                txt_latter.setText(String.valueOf(message.getUser_name().toCharArray()[0]));
            }


            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SellerProfileActivity.class);
                    intent.putExtra("user_id", message.getSender_user_id());
                    mContext.startActivity(intent);
                }
            });
            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SellerProfileActivity.class);
                    intent.putExtra("user_id", message.getSender_user_id());
                    mContext.startActivity(intent);
                }
            });


        }
    }


}