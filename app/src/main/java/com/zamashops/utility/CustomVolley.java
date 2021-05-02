package com.zamashops.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.tapadoo.alerter.Alerter;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.VolleyImageCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CustomVolley {


    public static void volleyBody(Context context, String URL, JSONObject jsonObject, final VolleyCallback callback) {

        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");
        progressDialog.setCancelable(false);


        JSONObject jsonBody = jsonObject;
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.url) + URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //    progressDialog.dismiss();
                    callback.onSuccess(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                progressDialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        progressDialog.show();

    }


    /**
     * Request to the server for data
     */
    public static void getInsertGETData(final Context context, final Map<String, String> parameters, String url, final VolleyCallback callback) {

        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");
        progressDialog.setCancelable(false);

        String strings = context.getResources().getString(R.string.url) + url;
        StringRequest request = new StringRequest(Request.Method.GET, strings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    progressDialog.dismiss();
                    callback.onSuccess(jsonObject);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
                showCustomDialog(context);
                //Toast.makeText(context, "Connection Problem", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = parameters;
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
        progressDialog.show();

    }

    /**
     * Request to the server for data
     */
    public static void getInsertPOSTData(final Context context, final Map<String, String> parameters, final String file_name, final VolleyCallback callback, final boolean... check_request) {

        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");


        String strings = context.getResources().getString(R.string.url) + file_name;
        StringRequest request = new StringRequest(Request.Method.POST, strings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    if (check_request.length > 0) {
                        showCustomDialog2(context);
                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ex) {

                }
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    callback.onSuccess(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
                if (check_request.length > 0) {

                } else {
                    showCustomDialog(context);
                }
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                getInsertPOSTData3(context, parameters, file_name, callback, true);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = parameters;
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);
        if (!(check_request.length > 0)) {
            progressDialog.show();
        }

    }


    /**
     * Request to the server for data
     */
    public static void getInsertPOSTDataDialog(final Context context, final Map<String, String> parameters, final String file_name, final VolleyCallback callback, final boolean... check_request) {


        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");
        progressDialog.setCancelable(false);


        String strings = context.getResources().getString(R.string.url) + file_name;
        StringRequest request = new StringRequest(Request.Method.POST, strings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    if (check_request.length > 0) {
                        showCustomDialog2(context);
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    progressDialog.dismiss();
                    callback.onSuccess(jsonObject);


                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
                if (check_request.length > 0) {

                } else {
                    showCustomDialog(context);
                }
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                getInsertPOSTData3(context, parameters, file_name, callback, true);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = parameters;
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);
        if (!(check_request.length > 0)) {
            progressDialog.show();
        }

    }


    /**
     * Request to the server for data
     */
    public static void getInsertPOSTData2(final Context context, final Map<String, String> parameters, final String file_name, final VolleyCallback callback, final boolean... check_request) {

        String strings = context.getResources().getString(R.string.url) + file_name;
        StringRequest request = new StringRequest(Request.Method.POST, strings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    if (check_request.length > 0) {
                        showCustomDialog2(context);
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    callback.onSuccess(jsonObject);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                if (check_request.length > 0) {

                } else {
                    showCustomDialog(context);
                }
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                getInsertPOSTData3(context, parameters, file_name, callback, true);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = parameters;
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);

    }


    /**
     * Request to the server for data
     */
    public static void getInsertPOSTDataNotShowError(final Context context, final Map<String, String> parameters, String file_name, final VolleyCallback callback, final boolean... check_request) {

        String strings = context.getResources().getString(R.string.url) + file_name;
        StringRequest request = new StringRequest(Request.Method.POST, strings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    callback.onSuccess(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = parameters;
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);

    }


    /**
     * Request to the server for data
     */
    public static void getInsertPOSTData3(final Context context, final Map<String, String> parameters, final String file_name, final VolleyCallback callback, final boolean... check_request) {
        String strings = context.getResources().getString(R.string.url) + file_name;
        StringRequest request = new StringRequest(Request.Method.POST, strings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    if (check_request.length > 0) {
                        showCustomDialog2(context);
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    callback.onSuccess(jsonObject);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                if (check_request.length > 0) {

                } else {
                    showCustomDialog(context);
                }
//                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                getInsertPOSTData3(context, parameters, file_name, callback, true);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = parameters;
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);

    }


    /**
     * Request to the server for data
     */
    public static void getImage(Context context, String image_name, int width, int height, final VolleyImageCallback callback) {


        String url = context.getResources().getString(R.string.url_image) + image_name;

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                callback.onSuccess(response);
            }
        }, width, height, ImageView.ScaleType.FIT_XY, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);

    }


    /**********************Internet Warning Dialog *****************************************/
    public static void showCustomDialog2(final Context context) {


        Alerter.create((Activity) context)
                .setTitle("Connection Back")
                .setText("")
                .setBackgroundColorRes(R.color.success) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    public static void showCustomDialog(final Context context) {


        Alerter.create((Activity) context)
                .setTitle("Connection Problem")
                .setText("No Internet Connection / Server Problem. Try Again Later")
                .setBackgroundColorRes(R.color.danger) // or setBackgroundColorInt(Color.CYAN)
                .show();


//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
//        dialog.setContentView(R.layout.dialog_warning);
//        dialog.setCancelable(false);
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//
//        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((Activity)context).finish();
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
    }

    /**********************Internet Warning Dialog *****************************************/


}
