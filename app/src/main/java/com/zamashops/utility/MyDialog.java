package com.zamashops.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.zamashops.R;
import com.zamashops.interfaces.myOnClickListener;

public class MyDialog extends AlertDialog {

    Button btn_ok, btn_cancel;
    TextView txt_title, txt_msg;
    String title;
    String msg;

    ImageView img_dialog;
    RelativeLayout dialog_background;

    public static int SUCCESS = 1;
    public static int DANGER = 2;
    public static int INFO = 3;

    int dialog_type = SUCCESS;

    Context context;

    public MyDialog(Context context, String title, String msg) {
        super(context);
        this.title = title;
        this.msg = msg;
        this.context = context;
        this.show();
    }

    public MyDialog(Context context) {
        super(context);
        this.title = "";
        this.msg = "";
        this.show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_dialog);

        txt_title = findViewById(R.id.txt_title);
        txt_msg = findViewById(R.id.txt_msg);
        img_dialog = findViewById(R.id.img_dialog);
        dialog_background = findViewById(R.id.dialog_background);

        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        if (!title.isEmpty())
            txt_title.setText(title);
        if (!msg.isEmpty())
            txt_msg.setText(msg);

        btn_ok = findViewById(R.id.buttonOk);
        btn_cancel = findViewById(R.id.buttonCancel);

        this.setCancelable(false);



    }


    public void onPositiveClick(String text, final myOnClickListener listener) {
        btn_ok.setText(text);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonClick(MyDialog.this);
            }
        });
    }

    public  void setTitle(String title){
        this.title = title;
        txt_title.setText(title);
    }
    public  void setDialog(int type){
        this.dialog_type = type;
        if(dialog_type == 1){

        }else if(dialog_type == 2){
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.dialog_button_background);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, context.getResources().getColor(R.color.danger));
            img_dialog.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delete_circle_white_24dp));
            dialog_background.setBackgroundColor( context.getResources().getColor(R.color.danger));
            btn_cancel.setTextColor( context.getResources().getColor(R.color.danger));
            btn_ok.setBackground(unwrappedDrawable);
        }else if(dialog_type == 3){
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.dialog_button_background);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable,  context.getResources().getColor(R.color.warning));
            img_dialog.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_info_outline_white_24dp));
            dialog_background.setBackgroundColor( context.getResources().getColor(R.color.warning));
            btn_cancel.setTextColor( context.getResources().getColor(R.color.warning));
            btn_ok.setBackground(unwrappedDrawable);
        }


    }

    public  void setMessage(String msg){
        this.msg = msg;
        txt_msg.setText(msg);

    }

    public void onCancelClick(String text, final myOnClickListener listener) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        btn_ok.setLayoutParams(param);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setText(text);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonClick(MyDialog.this);
            }
        });
    }

    public void showCancel() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        btn_ok.setLayoutParams(param);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.this.dismiss();
            }
        });
    }

    public void showCancel(String text) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        btn_ok.setLayoutParams(param);
        btn_cancel.setText(text);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.this.dismiss();
            }
        });
    }

}