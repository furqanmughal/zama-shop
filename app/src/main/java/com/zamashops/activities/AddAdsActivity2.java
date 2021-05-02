package com.zamashops.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import androidx.annotation.NonNull;
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
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.yalantis.ucrop.UCrop;
import com.zamashops.MainActivity;
import com.zamashops.MainLoginActivity;
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
import com.zamashops.view.BadgeNavigationDrawable;

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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;


public class AddAdsActivity2 extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<UploadImageModel> uploadImageModels = new ArrayList<>();
    RecyclerView upload_image_lis;


    EditText edt_description, edt_title, edt_price;
    EditText edt_address;
    Button btn_add_post;
    RadioGroup radio_post_type_group;
    RadioButton radio_post;
    TextView txt_category, txt_city;

    FrameLayout frameLayout_image;

    ///////////////////////Category dialog ///////////////////////////////////////////////////////
    ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    ArrayList<String> main_categories = new ArrayList<>();
    ///////////////////////Category dialog ///////////////////////////////////////////////////////

    GalleryAdapter adapter;

    Context context = AddAdsActivity2.this;

    ///////////////////////////// City Dialog///////////////////////////////////////////////////////////
    ArrayList<String> city_ids = new ArrayList<>();
    ArrayList<String> city_names = new ArrayList<>();

    ArrayList<String> temp_city_ids = new ArrayList<>();
    ArrayList<String> temp_city_names = new ArrayList<>();

    String category_id = "";
    String city_id = "";
    ///////////////////////////// City Dialog///////////////////////////////////////////////////////////

    ///////////////////////////// Image///////////////////////////////////////////////////////////
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 0;
    ///////////////////////////// Image///////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ads);

        initNavigation();
        initViews();
        fetchCategory();
        fetchCity();

    }

    void initViews() {

        upload_image_lis = findViewById(R.id.upload_image_lis);
        frameLayout_image = findViewById(R.id.frameLayout_image);
        edt_description = findViewById(R.id.edt_description);
        edt_title = findViewById(R.id.edt_title);
        edt_price = findViewById(R.id.edt_price);
        radio_post_type_group = findViewById(R.id.radio_post_type_group);
        txt_category = findViewById(R.id.txt_category);
        txt_city = findViewById(R.id.txt_city);
        btn_add_post = findViewById(R.id.btn_add_post);
        edt_address = findViewById(R.id.edt_address);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        upload_image_lis.setLayoutManager(linearLayoutManager);
        upload_image_lis.setHasFixedSize(true);
        upload_image_lis.setItemViewCacheSize(20);
        upload_image_lis.setDrawingCacheEnabled(true);
        upload_image_lis.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        /************** CATEGORY RECYCLEVIEW *****************/
        adapter = new GalleryAdapter(context, uploadImageModels, "");
        upload_image_lis.setAdapter(adapter);
        /************** CATEGORY RECYCLEVIEW *****************/

        btn_add_post.setOnClickListener(this);
        txt_category.setOnClickListener(this);
        txt_city.setOnClickListener(this);

        frameLayout_image.setOnClickListener(this);

    }

    boolean validateForm() {
        boolean validate = true;
        if (edt_title.getText().equals("")) {
            edt_title.setError("Title may not  bo Empty!");
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
        } else if (edt_description.getText().toString().length() < 5) {
            edt_description.setError("Descrption it least 5 characters!");
            edt_description.requestFocus();
            validate = false;
        } else if (uploadImageModels.size() < 1) {
            Toast.makeText(context, "Select atleast 1 image!", Toast.LENGTH_LONG).show();
            validate = false;
        }

        return validate;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////

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


//                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");

//                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//                    Uri tempUri = UtilityFunctions.getImageUri(getApplicationContext(), photo);
//

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
                            String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);


                            try {
                                if (file_extn.toLowerCase().equals("jpg") || file_extn.toLowerCase().equals("jpeg") || file_extn.toLowerCase().equals("png")) {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);


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
                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                                bitmap.getWidth(), bitmap.getHeight(), matrix,
                                                true);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    bitmap = getScaledDownBitmap(bitmap, 900, false);
                                    uploadImageModels.add(new UploadImageModel("", file_extn, bitmap));

                                } else {
                                    Toast.makeText(context, "Select Image Only (jpg/jpeg/png) extension", Toast.LENGTH_LONG).show();

                                }
                            } catch (IOException e) {
                                Toast.makeText(context, "Image not selected try again!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }


                        }

                    } else if (imageReturnedIntent.getData() != null) {
                        Uri selectedImage = imageReturnedIntent.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            bitmap = getScaledDownBitmap(bitmap, 900, false);
                            String filePath = UtilityFunctions.getRealPathFromURI(context, selectedImage);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                            if (file_extn.toLowerCase().equals("jpg") || file_extn.toLowerCase().equals("jpeg") || file_extn.toLowerCase().equals("png")) {
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


    ////////////////////////////////IMAGE////////////////////////////////////////////////////////////////

    public void selectImage(View view) {


        if (uploadImageModels.size() <= 9) {

            String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the option:");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (Permission.isStoragePermissionGranted(context)) {

                            dispatchTakePictureIntent();
//                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(takePicture, 0);
                        } else {
                            Toast.makeText(context, "First Grant The Storage Permission!", Toast.LENGTH_SHORT).show();
                        }
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
            Toast.makeText(context, "You can select more then 5 images!", Toast.LENGTH_LONG).show();
        }
    }


    ////////////////////////////////IMAGE////////////////////////////////////////////////////////////////
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

    void insertData(final String title, final String price, final String des, final String type, final String address) {


        /////////////////////   VOLLEY MULTIPART ///////////////////////////////////////////////
        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");
        progressDialog.setCancelable(false);

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getResources().getString(R.string.url) + "add_post.php",
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

                                    new MyDialog(context, "success", "Your Post Added Successfully. Check It In Your Profile!")
                                            .onPositiveClick("OK", new myOnClickListener() {
                                                @Override
                                                public void onButtonClick(MyDialog dialog) {
                                                    finish();
                                                    Intent intent = new Intent(context, MyPostAcitivity.class);
                                                    startActivity(intent);
                                                    dialog.dismiss();
                                                }
                                            });
                                } else {
                                    new MyDialog(context, "success", "Your Post Added Successfully and will be Approved by The Admin within Three working days.")
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
                Map<String, String> params = new HashMap<>();
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
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                for (int i = 0; i < uploadImageModels.size(); i++) {
                    long imagename = System.currentTimeMillis() + i;
                    params.put("image" + i, new DataPart(logPre.getString("user_id", "__") + "_" + imagename + "." + uploadImageModels.get(i).getTag(), getFileDataFromDrawable(uploadImageModels.get(i).getImage())));

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


    public Bitmap getScaledDownBitmap(Bitmap bitmap, int threshold, boolean isNecessaryToKeepOrig) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;

        if (width > height && width > threshold) {
            newWidth = threshold;
            newHeight = (int) (height * (float) newWidth / width);
        }

        if (width > height && width <= threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap;
        }

        if (width < height && height > threshold) {
            newHeight = threshold;
            newWidth = (int) (width * (float) newHeight / height);
        }

        if (width < height && height <= threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap;
        }

        if (width == height && width > threshold) {
            newWidth = threshold;
            newHeight = newWidth;
        }

        if (width == height && width <= threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap;
        }

        return getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (!isNecessaryToKeepOrig) {
            bm.recycle();
        }
        return resizedBitmap;
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

                CustomVolley.getInsertPOSTDataDialog(context, params, "fetch_sub_category.php", new VolleyCallback() {
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

    ///////////////////////////// City Dialog///////////////////////////////////////////////////////////
    void fetchCity() {
        CustomVolley.getInsertPOSTData(context, new HashMap<String, String>(), "fetch_city.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                    } else {
                        JSONArray city = result.getJSONArray("city");


                        for (int i = 0; i < city.length(); i++) {
                            JSONObject c = city.getJSONObject(i);
                            temp_city_ids.add(c.getString("city_id"));
                            city_ids.add(c.getString("city_id"));
                            city_names.add(c.getString("city_name"));
                            temp_city_names.add(c.getString("city_name"));

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    void selectCity() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_city);

        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, temp_city_names);
        final ListView list = dialog.findViewById(R.id.list_city);
        final TextView txt_back = dialog.findViewById(R.id.txt_back);
        final EditText edt_search = dialog.findViewById(R.id.edt_search);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txt_city.setText(temp_city_names.get(i));
                city_id = temp_city_ids.get(i);
                dialog.dismiss();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String serach_text = edt_search.getText().toString().toLowerCase();
                temp_city_names.clear();
                temp_city_ids.clear();

                for (int j = 0; j < city_names.size(); j++) {
                    if (city_names.get(j).toLowerCase().contains(serach_text)) {
                        temp_city_names.add(city_names.get(j));
                        temp_city_ids.add(city_ids.get(j));
                    }
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void afterTextChanged(Editable editable) {

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
    ///////////////////////////// City Dialog///////////////////////////////////////////////////////////


    void cropImage(Uri sourceUri, String destinationName) {

        UCrop.of(sourceUri, Uri.fromFile(new File(getCacheDir(), destinationName)))
                .withAspectRatio(16, 9)
                .start(AddAdsActivity2.this);
        ;
    }


    void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final BadgeNavigationDrawable drawerIcon = new BadgeNavigationDrawable(context);


        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
//        toggle.setDrawerArrowDrawable(drawerIcon);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        TextView txt_navigation_name = headerView.findViewById(R.id.txt_navigation_name);
        TextView txt_navigation_email = headerView.findViewById(R.id.txt_navigation_email);
        CircleImageView img_navigation = headerView.findViewById(R.id.img_navigation);

        final Menu menu = navigationView.getMenu();
        if (!logPre.getBoolean("login_stauts", false)) {
            menu.findItem(R.id.nav_myshop).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(false);
            menu.findItem(R.id.nav_myshop).setVisible(false);
            menu.findItem(R.id.nav_chat).setVisible(false);
            menu.findItem(R.id.nav_follower_post).setVisible(false);
            menu.findItem(R.id.nav_favourite).setVisible(false);
            menu.findItem(R.id.nav_logout).setTitle("Login").setIcon(getResources().getDrawable(R.drawable.ic_vpn_key_black_24dp));
        } else {
            menu.findItem(R.id.nav_myshop).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(true);
            menu.findItem(R.id.nav_myshop).setVisible(true);
            menu.findItem(R.id.nav_chat).setVisible(true);
            menu.findItem(R.id.nav_follower_post).setVisible(true);
            menu.findItem(R.id.nav_favourite).setVisible(true);
            menu.findItem(R.id.nav_logout).setTitle("Logout").setIcon(getResources().getDrawable(R.drawable.ic_baseline_exit_to_app_24));
        }


        menu.findItem(R.id.nav_add_post).setChecked(true);

        final MenuItem inbox = menu.findItem(R.id.nav_chat);

        HashMap<String, String> params = new HashMap<>();
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
            CustomVolley.getInsertPOSTData2(context, params, "chat/fetch_chat_count.php", new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {


                    try {

                        boolean error = result.getBoolean("error");
                        if (error) {
                        } else {
                            int count = result.getInt("count");
                            if (count <= 0) {
                                inbox.setActionView(null);
                            } else {
                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.bage, null, false);
                                inbox.setActionView(view);
                                View inbox_view = inbox.getActionView();
                                final TextView txt_bage = inbox_view.findViewById(R.id.txt_bage);
                                txt_bage.setText(String.valueOf(count));
                                drawerIcon.setCount(String.valueOf(count));
                                toggle.setDrawerArrowDrawable(drawerIcon);
                                toggle.syncState();

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            inbox.setActionView(null);
        }


        txt_navigation_name.setText(logPre.getString("user_name", "Zama Shops"));
        if (logPre.getString("user_email", "info@zamashops.com").contains("@")) {
            txt_navigation_email.setText(logPre.getString("user_email", "info@zamashops.com"));
        } else {
            txt_navigation_email.setText("");
        }

        String image_name = "";
        if (!logPre.getString("user_image", "").isEmpty()) {
            if (logPre.getString("user_image", "").contains("/")) {
                image_name = logPre.getString("user_image", "");
            } else {
                image_name = context.getResources().getString(R.string.url_image) + logPre.getString("user_image", "");
            }
            Glide
                    .with(context)
                    .load(image_name)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(img_navigation);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initNavigation();


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
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favourite) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, FavouriteActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_add_post) {


        } else if (id == R.id.nav_profile) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_shop) {
            Intent intent = new Intent(context, AllUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_follower_post) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, FollowerPostActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }
        } else if (id == R.id.nav_chat) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, AllChatActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }
        } else if (id == R.id.nav_myshop) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, MyPostAcitivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }
        } else if (id == R.id.nav_logout) {
            if (logPre.getBoolean("login_stauts", false)) {
                logout();
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(context, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(context, TermActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.appurl) + context.getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share ZamaShops using"));

        } else if (id == R.id.nav_rateus) {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void logout() {

        /*  Google  */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        /*  Google  */

        LoginManager.getInstance().logOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });


        preEditor.remove("user_id");
        preEditor.remove("user_email");
        preEditor.remove("user_name");
        preEditor.remove("user_pass");
        preEditor.remove("user_image");
        preEditor.remove("user_contact");
        preEditor.remove("user_address");
        preEditor.remove("user_account_type");
        preEditor.remove("login_stauts");
        preEditor.remove("token");
        preEditor.apply();
        preEditor.commit();

        Intent intent = new Intent(context, MainActivity.class);
        finish();
        startActivity(intent);

    }


}
