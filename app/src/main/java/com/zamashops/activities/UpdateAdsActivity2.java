package com.zamashops.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.zamashops.MainActivity;
import com.zamashops.R;
import com.zamashops.adapters.GalleryAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.UploadImageModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;
import com.zamashops.utility.Permission;
import com.zamashops.utility.UtilityFunctions;
import com.zamashops.utility.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.UtilityFunctions.getScaledDownBitmap;


public class UpdateAdsActivity2 extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<UploadImageModel> uploadImageModels = new ArrayList<>();
    RecyclerView upload_image_lis;


    FrameLayout frameLayout_image;
    EditText edt_description, edt_title, edt_price;
    EditText edt_address;
    Button btn_add_post;
    RadioGroup radio_post_type_group;
    RadioButton radio_post;
    TextView txt_category, txt_city;

    ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    ArrayList<String> main_categories = new ArrayList<>();


    GalleryAdapter adapter;

    Context context = UpdateAdsActivity2.this;

    String[] city_ids;
    String[] city_names;

    String category_id = "";
    String city_id = "";

    String product_id = "";

    ///////////////////////////// Image///////////////////////////////////////////////////////////
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 0;
    ///////////////////////////// Image///////////////////////////////////////////////////////////

    boolean check_cites = false;
    boolean check_categories = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ads);


        if (getIntent().hasExtra("product_id")) {
            product_id = getIntent().getStringExtra("product_id");
        } else {
            Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
            finish();
        }


        fetchCity();
        initNavigation();
        initViews();
        fetchCategory();


    }


    void initViews() {

        edt_address = findViewById(R.id.edt_address);
        upload_image_lis = findViewById(R.id.upload_image_lis);
        edt_description = findViewById(R.id.edt_description);
        edt_title = findViewById(R.id.edt_title);
        edt_price = findViewById(R.id.edt_price);
        radio_post_type_group = findViewById(R.id.radio_post_type_group);
        txt_category = findViewById(R.id.txt_category);
        txt_city = findViewById(R.id.txt_city);
        btn_add_post = findViewById(R.id.btn_add_post);
        frameLayout_image = findViewById(R.id.frameLayout_image);

        btn_add_post.setText("Update Post");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        upload_image_lis.setLayoutManager(linearLayoutManager);
        upload_image_lis.setHasFixedSize(true);
        upload_image_lis.setItemViewCacheSize(20);
        upload_image_lis.setDrawingCacheEnabled(true);
        upload_image_lis.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        /************** CATEGORY RECYCLEVIEW *****************/
        adapter = new GalleryAdapter(context, uploadImageModels, product_id);
        upload_image_lis.setAdapter(adapter);
        /************** CATEGORY RECYCLEVIEW *****************/

        frameLayout_image.setOnClickListener(this);

        btn_add_post.setOnClickListener(this);
        txt_category.setOnClickListener(this);
        txt_city.setOnClickListener(this);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (check_cites && check_categories) {
                    fetchProductDetail();
                } else {
                    handler.postDelayed(this, 100);
                }

            }
        };

        handler.postDelayed(r, 100);


    }

    void fetchProductDetail() {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);


        CustomVolley.getInsertPOSTData(context, params, "fetch_detail_product.php", new VolleyCallback() {
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


                        JSONArray user_data = result.getJSONArray("products");

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");
                            for (int count = 0; count < images.length(); count++) {
                                uploadImageModels.add(new UploadImageModel(images.getJSONObject(count).getString("product_image")));

                            }

                            adapter.notifyDataSetChanged();

                            edt_title.setText(p.getString("product_name"));
                            edt_price.setText(p.getString("product_price"));
                            edt_description.setText(p.getString("product_description"));
                            edt_address.setText(p.getString("address"));

                            for (int count = 0; count < city_ids.length; count++) {
                                if (p.getString("city").equals(city_ids[count])) {
                                    txt_city.setText(city_names[count]);
                                    city_id = city_ids[count];
                                    break;
                                }
                            }

                            txt_category.setText(p.getString("category"));
                            category_id = p.getString("sub_category_id");

                            if (p.getString("product_type").equals("1")) {
                                radio_post = findViewById(R.id.radio_normal);
                                radio_post.setChecked(true);
                            } else {
                                radio_post = findViewById(R.id.radio_highlight);
                                radio_post.setChecked(true);
                            }

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    boolean validateForm() {
        boolean validate = true;
        if (edt_title.getText().toString().length() < 5) {
            edt_title.setError("Title it least 5 characters!");
            edt_title.requestFocus();
            validate = false;
        } else if (txt_category.getText().toString().isEmpty()) {
            txt_category.setError("Category May not be Empty!");
            txt_category.setHintTextColor(Color.rgb(255, 0, 0));
            validate = false;
        } else if (txt_city.getText().toString().isEmpty()) {
            txt_city.setError("City May not be Empty!");
            txt_city.setHintTextColor(Color.rgb(255, 0, 0));
            validate = false;
        } else if (edt_price.getText().toString().isEmpty()) {
            edt_price.setError("Price May not be Empty!");
            edt_price.requestFocus();
            validate = false;
        } else if (edt_description.getText().toString().length() < 10) {
            edt_description.setError("Descrption it least 10 characters!");
            edt_description.requestFocus();
            validate = false;
        } else if (uploadImageModels.size() < 1) {
            edt_description.setError("Select Product Image!");
            edt_description.requestFocus();
            validate = false;
        }

        return validate;
    }

    ////////////////////////////////IMAGE////////////////////////////////////////////////////////////////
    public Bitmap compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return scaledBitmap;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    ////////////////////////////////IMAGE////////////////////////////////////////////////////////////////


    protected void onActivityResult(int requestCode, int resultCode, final Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media
                                .getBitmap(context.getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {

                            String filePath = mCurrentPhotoPath;
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();

                            if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
//                                rotatedBitmap = getScaledDownBitmap(rotatedBitmap, 900, false);
                                uploadImageModels.add(new UploadImageModel("", file_extn, compressImage(mCurrentPhotoPath)));
                            } else {
                                Toast.makeText(context, "Select Image Only (jpg/jpeg/png) extension", Toast.LENGTH_LONG).show();
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                break;
            case 1:
                if (resultCode == RESULT_OK) {


                    if (imageReturnedIntent.getClipData() != null) {
                        int count = imageReturnedIntent.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        if (count > 4) {
                            count = 5;
                        }
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = imageReturnedIntent.getClipData().getItemAt(i).getUri();
                            String filePath = UtilityFunctions.getRealPathFromURI(context, imageUri);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                            if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                    bitmap = getScaledDownBitmap(bitmap, 900, false);
                                    uploadImageModels.add(new UploadImageModel("", file_extn, bitmap));
                                } catch (IOException e) {
                                    Toast.makeText(context, "Image not selected try again!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(context, "Select Image Only (jpg/jpeg/png) extension", Toast.LENGTH_LONG).show();

                            }


                        }

                    } else if (imageReturnedIntent.getData() != null) {
                        Uri selectedImage = imageReturnedIntent.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            String filePath = UtilityFunctions.getRealPathFromURI(context, selectedImage);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                            if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                bitmap = getScaledDownBitmap(bitmap, 900, false);
                                uploadImageModels.add(new UploadImageModel("", file_extn, bitmap));
                            } else {
                                Toast.makeText(context, "Select Image Only (jpg/jpeg/png) extension", Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException e) {
                            Toast.makeText(context, "Image not selected try again!", Toast.LENGTH_SHORT).show();

                        }

                    }

                }
                break;
        }

        adapter.notifyDataSetChanged();
    }

    ///////////////////////////////////////////////////////////////////////
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.zamashops.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////


    public void selectImage(View view) {


        if (uploadImageModels.size() < 10) {
            String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the option:");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else if (which == 1) {
                        if (Permission.isStoragePermissionGranted(context)) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                            startActivityForResult(i, 1);

                        } else {
                            Toast.makeText(context, "First Grant The Storage Permission!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } else {
            Toast.makeText(context, "You Can Only Upload 10 Images", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_post:
                if (validateForm()) {
                    String title = edt_title.getText().toString();
                    String price = edt_price.getText().toString();
                    String des = edt_description.getText().toString();
                    String address = edt_address.getText().toString();
                    String type = "1";

                    if (radio_post_type_group.getCheckedRadioButtonId() == R.id.radio_normal) {
                        type = "1";
                    } else if (radio_post_type_group.getCheckedRadioButtonId() == R.id.radio_highlight) {
                        type = "2";
                    }

                    insertData(title, price, des, type, address);

                }


                break;
            case R.id.txt_category:
                selectCategory();
                break;

            case R.id.txt_city:
                selectCity();
                break;

            case R.id.frameLayout_image:
                selectImage(view);
                break;

        }
    }

    void insertData(final String title, final String price, final String des,
                    final String type, final String address) {

        /////////////////////   VOLLEY MULTIPART ///////////////////////////////////////////////
        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");
        progressDialog.setCancelable(false);

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getResources().getString(R.string.url) + "update_post.php",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject result = new JSONObject(new String(response.data));


                            //   Toast.makeText(context,response.toString(),Toast.LENGTH_LONG).show();

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

                                if (type.equals("1")) {

                                    new MyDialog(context, "success", "Your Post Updated Successfully. Check It In Your Profile!")
                                            .onPositiveClick("OK", new myOnClickListener() {
                                                @Override
                                                public void onButtonClick(MyDialog dialog) {
                                                    finish();
                                                    dialog.dismiss();
                                                }
                                            });
                                } else {
                                    new MyDialog(context, "success", "Your Post Updated Successfully and will be Approved by The Admin within Three working days.")
                                            .onPositiveClick("OK", new myOnClickListener() {
                                                @Override
                                                public void onButtonClick(MyDialog dialog) {
                                                    finish();
                                                    Intent intent = new Intent(context, MyPostAcitivity.class);
                                                    startActivity(intent);
                                                    dialog.dismiss();
                                                }
                                            });
                                }


//

                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("product", String.valueOf(true));
                params.put("title", title);
                params.put("price", price);
                params.put("description", des);
                params.put("category", category_id);
                params.put("user_id", logPre.getString("user_id", ""));
                params.put("city", city_id);
                params.put("product_type", type);
                params.put("address", address);
                params.put("no_of_images", String.valueOf(uploadImageModels.size()));
                params.put("product_id", product_id);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                for (int i = 0; i < uploadImageModels.size(); i++) {
                    if (uploadImageModels.get(i).isCheck()) {
                        long imagename = System.currentTimeMillis() + i;
                        params.put("image" + i, new DataPart(logPre.getString("user_name", "__") + "_" + imagename + "." + uploadImageModels.get(i).getTag(), getFileDataFromDrawable(uploadImageModels.get(i).getImage())));
                    }
                }


                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        progressDialog.show();


    }


    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    void fetchCategory() {
        categoryModels.clear();
        main_categories.clear();
        CustomVolley.getInsertPOSTData(context, new HashMap<String, String>(), "fetch_main_category.php", new VolleyCallback() {
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
                        JSONArray main_category = result.getJSONArray("main_category");
                        for (int i = 0; i < main_category.length(); i++) {
                            JSONObject category = main_category.getJSONObject(i);
                            main_categories.add(category.getString("main_category_name"));
                            categoryModels.add(new CategoryModel(category.getString("main_category_id"), category.getString("main_category_name"),
                                    category.getString("image")));
                        }

                        check_categories = true;

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    void fetchCity() {
        CustomVolley.getInsertPOSTData(context, new HashMap<String, String>(), "fetch_city.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                    } else {
                        JSONArray city = result.getJSONArray("city");
                        city_ids = new String[city.length()];
                        city_names = new String[city.length()];

                        for (int i = 0; i < city.length(); i++) {
                            JSONObject c = city.getJSONObject(i);
                            city_ids[i] = c.getString("city_id");
                            city_names[i] = c.getString("city_name");

                        }

                        check_cites = true;

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    void selectCategory() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_main_category);

        final ArrayAdapter adapter2 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, main_categories);
        final ListView list = dialog.findViewById(R.id.list_main_category);
        list.setAdapter(adapter2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ArrayList<String> sub_category = new ArrayList<>();
                final ArrayList<CategoryModel> sub_categorylist = new ArrayList<>();
                final ArrayAdapter adapter3 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, sub_category);


                ////////////FETCH SUB CATEGORY///////////////////////
                HashMap<String, String> params = new HashMap<>();
                params.put("cat_id", categoryModels.get(i).getCat_id());

                CustomVolley.getInsertPOSTData(context, params, "fetch_sub_category.php", new VolleyCallback() {
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
                                JSONArray main_category = result.getJSONArray("sub_category");
                                for (int i = 0; i < main_category.length(); i++) {
                                    JSONObject category = main_category.getJSONObject(i);
                                    sub_category.add(category.getString("sub_category_name"));
                                    sub_categorylist.add(new CategoryModel(category.getString("sub_category_id"), category.getString("sub_category_name"),
                                            category.getString("image")));
                                }

                                adapter3.notifyDataSetChanged();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                final Dialog dialog2 = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_sub_category);
                dialog2.setCancelable(false);
                TextView txt_back = dialog2.findViewById(R.id.txt_back);
                txt_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });

                final ListView list = dialog2.findViewById(R.id.list_main_category);
                list.setAdapter(adapter3);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        category_id = sub_categorylist.get(i).getCat_id();
                        txt_category.setText(sub_categorylist.get(i).getCat_name());
                        dialog.dismiss();
                        dialog2.dismiss();
                    }
                });


                dialog2.show();
            }
        });


        dialog.show();
    }


    void selectCity() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_city);

        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, city_names);
        final ListView list = dialog.findViewById(R.id.list_city);
        final TextView txt_back = dialog.findViewById(R.id.txt_back);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txt_city.setText(city_names[i]);
                city_id = city_ids[i];
                dialog.dismiss();
            }
        });

        txt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_favourite) {


        } else if (id == R.id.nav_add_post) {

        } else if (id == R.id.nav_profile) {
            if (logPre.getBoolean("login_stauts", false)) {

            } else {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
