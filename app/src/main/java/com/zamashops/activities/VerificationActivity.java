package com.zamashops.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zamashops.MainActivity;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.zamashops.utility.App.preEditor;

public class VerificationActivity extends AppCompatActivity {


    String user_email = "";
    String user_pass = "";

    boolean login_status = false;

    Context context = VerificationActivity.this;

    EditText edt_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        if(getIntent().hasExtra("login_status")){
            login_status = getIntent().getBooleanExtra("status",false);
        }

        if(getIntent().hasExtra("email")){
            user_email = getIntent().getStringExtra("email");
            user_pass = getIntent().getStringExtra("pass");
        }else{
            finish();
        }

        edt_code = findViewById(R.id.edt_code);
    }

    public void verify(View view) {

        String code = edt_code.getText().toString();

        if(code.length() < 5){
            edt_code.setError("Code may be 6 characters!");
            edt_code.requestFocus();
        }else{
            checkData(code);
        }

    }

    void checkData(String code){
        HashMap<String, String> params = new HashMap<>();
        params.put("verification", String.valueOf(true));
        params.put("user_email",user_email);
        params.put("user_password",user_pass);
        params.put("code",code);

        CustomVolley.getInsertPOSTData(context, params, "user/verification_code.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if(error){
                        edt_code.setError("Code does not match!");
                        edt_code.requestFocus();
                    }else{


                        JSONObject user_data = result.getJSONObject("user_data");


                        preEditor.putString("user_id", user_data.getString("user_id"));
                        preEditor.putString("user_email", user_email);
                        preEditor.putString("user_name", user_data.getString("user_name"));
                        preEditor.putString("user_pass", user_pass);
                        preEditor.putString("user_contact", user_data.getString("user_contact"));
                        preEditor.putString("user_address", user_data.getString("user_address"));
                        preEditor.putString("user_account_type", user_data.getString("account_type"));
                        preEditor.putBoolean("login_stauts",true);
                        preEditor.apply();
                        preEditor.commit();

//                        Intent notification_intent = new Intent(context, AllChatActivity. class);
//                        MyNotification.showNotification(context,"Checking for Messages","Click Here You May Have New Messages",88,notification_intent);


                        if(login_status){
                            finish();
                        }else {

                            Intent intent = new Intent(context, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void resend_verification_code(){
        HashMap<String, String> params = new HashMap<>();
        params.put("verification", String.valueOf(true));
        params.put("user_email",user_email);
        params.put("user_password",user_pass);

        CustomVolley.getInsertPOSTData(context, params, "user/resend_verification_code.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if(error){

                        MyDialog dialog = new MyDialog(context, "Success!", result.getString("msg"));
                        dialog.onPositiveClick("OK", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);
                    }else{

                        new MyDialog(context, "success", result.getString("msg"))
                                .onPositiveClick("OK", new myOnClickListener() {
                                    @Override
                                    public void onButtonClick(MyDialog dialog) {
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

    public void sendVerificationCode(View view) {
        resend_verification_code();
    }
}