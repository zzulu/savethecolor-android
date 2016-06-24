package xyz.zzulu.savethecolor.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import xyz.zzulu.savethecolor.R;
import xyz.zzulu.savethecolor.data.ColorItems;
import xyz.zzulu.savethecolor.fragments.CameraFragment;
import xyz.zzulu.savethecolor.fragments.ColorItemFragment;

import static android.Manifest.permission.CAMERA;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CameraFragment.OnFragmentInteractionListener, ColorItemFragment.OnFragmentInteractionListener {


    protected static final String TAG = MainActivity.class.getSimpleName();

    private static int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_CAMERA = 0;

    private SharedPreferences mPreferences;

    private NavigationView navigationView;
    private View mLayout;

    private boolean requestCameraPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(CAMERA)) {

            Snackbar.make(mLayout, R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        if (!i.getBooleanExtra("auto",false)){
            IntroActivity mPreActivity = (IntroActivity) IntroActivity.mThisActivity;
            mPreActivity.finish();
        }

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mLayout = findViewById(R.id.root_view);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) header.findViewById(R.id.user_name);
        TextView tvUserEmail = (TextView) header.findViewById(R.id.user_email);

        tvUserName.setText(mPreferences.getString("Email","Save The Color"));
        tvUserEmail.setText(mPreferences.getString("Email","Save The Color"));

        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        // Remove any pending mHideConfirmSaveMessage.

        super.onDestroy();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_sign_out:

                ColorItems.deleteColorItemAll(getApplicationContext());
                mPreferences.edit().clear().commit();
//                SharedPreferences.Editor editor = mPreferences.edit();
//                // save the returned auth_token into
//                // the SharedPreferences
//                editor.putString("AuthToken", "");
//                editor.commit();

                Intent goIntro = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(goIntro);
                finish();

                return true;

            default:

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_camera:

                if (!requestCameraPermission()) {
                    break;
                }

                fragmentManager.beginTransaction().replace(R.id.fragment_container, CameraFragment.newInstance(mPreferences.getString("AuthToken",""))).commit();
                break;
//            case R.id.nav_gallery:
//            // Create intent to Open Image applications like Gallery, Google Photos
//            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            // Start the Intent
//            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
//            break;

            case R.id.nav_slideshow:
                fragmentManager.beginTransaction().replace(R.id.fragment_container, ColorItemFragment.newInstance()).commit();
                break;

            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



//    // When Image is selected from Gallery
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//        try {
//            // When an Image is picked
//            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
//                // Get the Image from data
//
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//                // Get the cursor
//                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
//                // Move to first row
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String imgPath = cursor.getString(columnIndex);
//                cursor.close();
//
//                FragmentManager fragmentManager = getSupportFragmentManager();
//
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, ImageFragment.newInstance(imgPath)).commit();
//
//
//                // Set the Image in ImageView
////                Add_Profile_Img.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(imgPath),360));
////                Add_Profile_Img.setScaleType(ImageView.ScaleType.FIT_XY);
//
//                // Get the Image's file name
//                //String fileNameSegments[] = imgPath.split("/");
//                //dol_img_filename = fileNameSegments[fileNameSegments.length - 1];
//
//                // Put file name in Async Http Post Param which will used in Php
//                // web app
//
//                //params.put("filename", fileName);
//
//            } else {
//                //Toast.makeText(this, "사진 선택 취소",Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
////            Toast.makeText(this, "알 수 없는 오류", Toast.LENGTH_LONG).show();
//        }
//
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (isFlashSupported()) {
//            getMenuInflater().inflate(R.menu.menu_color_picker, menu);
//            final MenuItem flashItem = menu.findItem(R.id.menu_color_picker_action_flash);
//            int flashIcon = mIsFlashOn ? R.drawable.ic_action_flash_off : R.drawable.ic_action_flash_on;
//            flashItem.setIcon(flashIcon);
//        }
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        final int itemId = item.getItemId();
//        boolean handled;
//        switch (itemId) {
//            case android.R.id.home:
//                finish();
//                handled = true;
//                break;
//
//            case R.id.menu_color_picker_action_flash:
//                toggleFlash();
//                handled = true;
//                break;
//
//            default:
//                handled = super.onOptionsItemSelected(item);
//        }
//
//        return handled;
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
