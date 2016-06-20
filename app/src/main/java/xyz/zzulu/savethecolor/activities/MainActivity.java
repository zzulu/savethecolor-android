package xyz.zzulu.savethecolor.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import xyz.zzulu.savethecolor.R;
import xyz.zzulu.savethecolor.fragments.CameraFragment;
import xyz.zzulu.savethecolor.fragments.ImageFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CameraFragment.OnFragmentInteractionListener, ImageFragment.OnImageFragmentInteractionListener {


    protected static final String TAG = MainActivity.class.getSimpleName();

    private static int RESULT_LOAD_IMG = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_camera) {
            // Handle the camera action

            fragmentManager.beginTransaction().replace(R.id.fragment_container, CameraFragment.newInstance()).commit();





        } else if (id == R.id.nav_gallery) {


            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        } else if (id == R.id.nav_slideshow) {

            //fragmentManager.beginTransaction().replace(R.id.container, FirstFragment.newInstance()).commit();

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgPath = cursor.getString(columnIndex);
                cursor.close();

                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.fragment_container, ImageFragment.newInstance(imgPath)).commit();


                // Set the Image in ImageView
//                Add_Profile_Img.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(imgPath),360));
//                Add_Profile_Img.setScaleType(ImageView.ScaleType.FIT_XY);

                // Get the Image's file name
                //String fileNameSegments[] = imgPath.split("/");
                //dol_img_filename = fileNameSegments[fileNameSegments.length - 1];

                // Put file name in Async Http Post Param which will used in Php
                // web app

                //params.put("filename", fileName);

            } else {
                //Toast.makeText(this, "사진 선택 취소",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
//            Toast.makeText(this, "알 수 없는 오류", Toast.LENGTH_LONG).show();
        }

    }


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

    @Override
    public void onImageFragmentInteraction(Uri uri) {

    }
}
