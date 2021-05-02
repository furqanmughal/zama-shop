package com.zamashops.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;

public class EditProfileActivity extends AppCompatActivity {

    EditText edt_user_name, edt_user_email, edt_user_contact_no, edt_user_city;

    Context context = EditProfileActivity.this;

    String token = "";

    String user_account_type  = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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

        user_account_type = logPre.getString("user_account_type","");

        initViews();
    }

    void initViews() {

        edt_user_name = findViewById(R.id.edt_user_name);
        edt_user_email = findViewById(R.id.edt_user_email);
        edt_user_contact_no = findViewById(R.id.edt_user_contact_no);
        edt_user_city = findViewById(R.id.edt_user_city);

        edt_user_name.setText(logPre.getString("user_name",""));
        edt_user_email.setText(logPre.getString("user_email",""));
        edt_user_city.setText(logPre.getString("user_address",""));
        edt_user_contact_no.setText(logPre.getString("user_contact",""));

//        if(!user_account_type.equals("1")){
//            edt_user_email.clearFocus();
//            edt_user_email.setFocusable(false);
            edt_user_email.setVisibility(View.GONE);
//        }

    }

    public void updateProfile(View view) {
        if (validateForm()) {
            String name = edt_user_name.getText().toString();
            String email = edt_user_email.getText().toString();
            String contact_no = edt_user_contact_no.getText().toString();
            String city = edt_user_city.getText().toString();
            sendData(name, email, contact_no, city);
        }
    }


    boolean validateForm() {
        boolean validate = true;
        if (edt_user_name.getText().toString().isEmpty()) {
            edt_user_name.setError("User Name Is Required!");
            edt_user_name.requestFocus();
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


    void sendData(final String name, final String email, final String contact, final String city) {


        HashMap<String, String> params = new HashMap<>();
        params.put("update_account", String.valueOf(true));
        params.put("user_id", logPre.getString("user_id", ""));
        params.put("user_name", name);
        params.put("user_email", email);
        params.put("user_contact", contact);
        params.put("user_city", city);
        params.put("token", token);

        CustomVolley.getInsertPOSTData(context, params, "user/update_user_account.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage(result.getString("msg"))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  finish();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    } else {


                        preEditor.putString("user_email", email);
                        preEditor.putString("user_name", name);
                        preEditor.putString("user_contact", contact);
                        preEditor.putString("user_address", city);
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage(result.getString("msg"))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
