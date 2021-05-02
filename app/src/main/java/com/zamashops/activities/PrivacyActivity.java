package com.zamashops.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class PrivacyActivity extends AppCompatActivity {

    Switch swt_contact, swt_email;

    Context context = PrivacyActivity.this;

    boolean email = false;
    boolean contact = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        swt_contact = findViewById(R.id.swt_contact);
        swt_email = findViewById(R.id.swt_email);

        if(getIntent().hasExtra("email")){
            email = getIntent().getBooleanExtra("email",false);
            contact = getIntent().getBooleanExtra("contact",false);
        }

        swt_contact.setChecked(contact);
        swt_email.setChecked(email);


        swt_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int c = 0;
                if(b){
                    c = 1;
                }else{
                    c=0;
                }
                privacy(b,c, "email");
            }
        });

        swt_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int c = 0;
                if(b){
                    c = 1;
                }else{
                    c=0;
                }
                privacy(b,c, "contact");

            }
        });

    }


    void privacy(final boolean s, final int  check, final String type) {


        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", logPre.getString("user_id", ""));
        params.put("check", String.valueOf(check));
        params.put("type", type);

        CustomVolley.getInsertPOSTDataDialog(context, params, "user/privacy.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
                    } else {

                        boolean privacy_error = result.getBoolean("privacy_error");

                        if(type.equals("email")){

                            if(!privacy_error) {
                                //swt_email.setChecked(!s);
                            }
                        }else{

                            if(!privacy_error) {
                                //swt_contact.setChecked(!s);
                            }
                        }



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}