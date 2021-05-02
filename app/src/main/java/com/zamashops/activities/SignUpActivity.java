package com.zamashops.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;
import com.zamashops.utility.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_user_name, edt_user_email, edt_user_password, edt_user_contact_no, edt_user_city;
    Button btn_sign_up;

    Context context = SignUpActivity.this;

    String token = "";

    boolean login_status = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(getIntent().hasExtra("status")){
            login_status = getIntent().getBooleanExtra("status",false);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();


                        //  Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });

        initViews();


        termAndCondition();





    }

    void initViews() {

        edt_user_name = findViewById(R.id.edt_user_name);
        edt_user_email = findViewById(R.id.edt_user_email);
        edt_user_password = findViewById(R.id.edt_user_password);
        edt_user_contact_no = findViewById(R.id.edt_user_contact_no);
        edt_user_city = findViewById(R.id.edt_user_city);

        btn_sign_up = findViewById(R.id.btn_sign_up);

        btn_sign_up.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_sign_up:
                if (validateForm()) {
                    String name = edt_user_name.getText().toString();
                    String email = edt_user_email.getText().toString();
                    String password = edt_user_password.getText().toString();
                    String contact_no = edt_user_contact_no.getText().toString();
                    String city = edt_user_city.getText().toString();
                    try {
                        sendData(name,email,password,contact_no,city);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    boolean validateForm() {
        boolean validate = true;
        if (edt_user_name.getText().toString().isEmpty()) {
            edt_user_name.setError("User Name Is Required!");
            edt_user_name.requestFocus();
            validate = false;
        } else if (edt_user_email.getText().toString().isEmpty() || !edt_user_email.getText().toString().contains("@")) {
            edt_user_email.setError("User Email Is Invalid!");
            edt_user_email.requestFocus();
            validate = false;
        } else if (edt_user_password.getText().toString().isEmpty()) {
            edt_user_password.setError("User Password Is Required!");
            edt_user_password.requestFocus();
            validate = false;
        } else if (edt_user_contact_no.getText().toString().isEmpty()) {
            edt_user_contact_no.setError("User Contact Is Required!");
            edt_user_contact_no.requestFocus();
            validate = false;
        } else if (edt_user_city.getText().toString().isEmpty()) {
            edt_user_city.setError("User City Is Required!");
            edt_user_city.requestFocus();
            validate = false;
        }

        return validate;
    }


    void sendData(String name, String email, String pass, String contact, String city) throws IOException {





        HashMap<String, String> params = new HashMap<>();
        params.put("create_account", String.valueOf(true));
        params.put("user_name",name);
        params.put("user_image", UtilityFunctions.convertToBase64Png(profileImage(String.valueOf(name.charAt(0)))));
        params.put("user_email",email);
        params.put("user_password",pass);
        params.put("user_contact",contact);
        params.put("user_city",city);
        params.put("token",token);

        CustomVolley.getInsertPOSTData(context, params, "create_user_account.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if(error){

                        MyDialog dialog = new MyDialog(context, "warning!", result.getString("msg"));
                        dialog.onPositiveClick("OK", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);

                    }else{


                        Intent intent = new Intent(context, VerificationActivity.class);
                        intent.putExtra("status",login_status);
                        intent.putExtra("email",result.getString("email"));
                        intent.putExtra("pass",result.getString("pass"));
                        finish();
                        startActivity(intent);

//                        MyDialog dialog = new MyDialog(context, "Success!", result.getString("msg"));
//                        dialog.onPositiveClick("OK", new myOnClickListener() {
//                            @Override
//                            public void onButtonClick(MyDialog dialog) {
//                                finish();
//                                dialog.dismiss();
//                                Intent intent = new Intent(context, LoginActivity.class);
//                                intent.putExtra("status",login_status);
//                                startActivity(intent);
//                            }
//                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    Bitmap profileImage(final String text) throws IOException {
        Bitmap bmp = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        TextPaint tp = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        tp.setColor(Color.WHITE);
        tp.setTextSize(150f);
        StaticLayout sl=new StaticLayout(text,tp,300,
                Layout.Alignment.ALIGN_CENTER, 1f,100f,false);

        canvas.save();
        sl.draw(canvas);
        canvas.restore();


        return  bmp;
    }




    void termAndCondition(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_term_condition, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        String webUrl = "http://zamashops.com/term_and_condition.php";
        final WebView mWebView = (WebView) dialogView.findViewById(R.id.webview);
        final ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        progressBar.setMax(100);
//        final TextView txt_term_condition = dialogView.findViewById(R.id.txt_term_condition);
        final Button txt_cancel = dialogView.findViewById(R.id.txt_cancel);


        Button btn_decline = dialogView.findViewById(R.id.btn_decline);
        Button btn_accept = dialogView.findViewById(R.id.btn_accept);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                progressBar.setProgress(progress); //Make the bar disappear after URL is loaded
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.loadUrl(webUrl);




        final AlertDialog alertDialog = dialogBuilder.create();

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog.show();
    }





}
