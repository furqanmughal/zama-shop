package com.zamashops.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zamashops.MainActivity;
import com.zamashops.MainLoginActivity;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;
import static com.zamashops.utility.App.token;

public class LoginActivity extends AppCompatActivity {

    EditText edt_user_email, edt_user_password;
    Button btn_login;

    TextView txt_forgot_password, txt_sign_up;

    Context context = LoginActivity.this;

    boolean login_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        if (getIntent().hasExtra("status")) {
            login_status = getIntent().getBooleanExtra("status", false);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    String email = edt_user_email.getText().toString();
                    String password = edt_user_password.getText().toString();
                    sendData(email, password);
                }
            }
        });

        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignUpActivity.class);
                intent.putExtra("status", login_status);
                startActivity(intent);
            }
        });

    }


    void initViews() {

        edt_user_email = findViewById(R.id.edt_user_email);
        edt_user_password = findViewById(R.id.edt_user_password);

        txt_forgot_password = findViewById(R.id.txt_forgot_password);
        txt_sign_up = findViewById(R.id.txt_sign_up);

        btn_login = findViewById(R.id.btn_login);


        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });


    }

    private void forgotPassword() {

        final EditText email = new EditText(context);


        email.setHint("Enter Email");
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);


        ll.setPadding(20, 20, 20, 20);

        ll.addView(email);


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Forgot Passsword")
                .setIcon(R.drawable.ic_vpn_key_black_24dp)
                .setView(ll)
                .setPositiveButton("Send Email", null)
                .setNegativeButton("Cancel", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString();


                String oldpassworddatabase = logPre.getString("user_pass", "");
                if (e.isEmpty() || !e.contains("@") || !e.contains(".")) {
                    email.setError("Enter Proper Email!");
                    email.requestFocus();
                } else {
                    updatePassword(e);
                    dialog.cancel();
                }
            }
        });
    }

    private void updatePassword(String e) {
        HashMap<String, String> params = new HashMap<>();
        params.put("verification", String.valueOf(true));
        params.put("user_email",e);

        CustomVolley.getInsertPOSTData(context, params, "user/forgot_password.php", new VolleyCallback() {
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

    boolean validateForm() {
        boolean validate = true;
        if (edt_user_email.getText().toString().isEmpty()) {
            edt_user_email.setError("User Email Is Required!");
            edt_user_email.requestFocus();
            validate = false;
        } else if (edt_user_password.getText().toString().isEmpty()) {
            edt_user_password.setError("User Password Is Required!");
            edt_user_password.requestFocus();
            validate = false;
        }

        return validate;
    }

    void sendData(final String email, final String pass) {
        HashMap<String, String> params = new HashMap<>();
        params.put("login", String.valueOf(true));
        params.put("user_email", email);
        params.put("user_password", pass);
        params.put("token", token);

        CustomVolley.getInsertPOSTData(context, params, "login.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {

                        MyDialog dialog = new MyDialog(context, "warning!", result.getString("msg"));
                        dialog.onPositiveClick("OK", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);



                    } else {


                        JSONObject user_data = result.getJSONObject("user_data");


                        preEditor.putString("user_id", user_data.getString("user_id"));
                        preEditor.putString("user_email", email);
                        preEditor.putString("user_name", user_data.getString("user_name"));
                        preEditor.putString("user_pass", pass);
                        preEditor.putString("user_image", user_data.getString("user_image"));
                        preEditor.putString("user_contact", user_data.getString("user_contact"));
                        preEditor.putString("user_address", user_data.getString("user_address"));
                        preEditor.putString("user_account_type", user_data.getString("account_type"));
                        preEditor.putString("token",token);
                        preEditor.putBoolean("login_stauts", true);
                        preEditor.apply();
                        preEditor.commit();

//                        Intent notification_intent = new Intent(context, AllChatActivity.class);
//                        MyNotification.showNotification(context, "Checking for Messages", "Click Here You May Have New Messages", 88, notification_intent);


                        if (login_status) {
                            finish();
                        } else {

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

    public void gotMainLogin(View view) {
        Intent intent = new Intent(context, MainLoginActivity.class);
        intent.putExtra("status", login_status);
        finish();
        startActivity(intent);

    }
}
