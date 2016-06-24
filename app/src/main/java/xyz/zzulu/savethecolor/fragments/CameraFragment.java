package xyz.zzulu.savethecolor.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import xyz.zzulu.savethecolor.R;
import xyz.zzulu.savethecolor.data.ColorItem;
import xyz.zzulu.savethecolor.data.ColorItems;
import xyz.zzulu.savethecolor.utils.Cameras;
import xyz.zzulu.savethecolor.views.CameraPreview;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment implements CameraPreview.OnColorSelectedListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Camera Fragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    protected boolean mIsPortrait;

    protected FrameLayout mPreviewContainer;

    protected CameraPreview mCameraPreview;

    protected CameraAsyncTask mCameraAsyncTask;

    private ColorCreateTask mTask = null;

    /**
     * A safe way to get an instance of the back {@link android.hardware.Camera}.
     */
    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return c;
    }

    protected Camera mCamera;


    protected static final String PICKED_COLOR_PROGRESS_PROPERTY_NAME = "pickedColorProgress";
    protected static final String SAVE_COMPLETED_PROGRESS_PROPERTY_NAME = "saveCompletedProgress";
    protected static final long DURATION_CONFIRM_SAVE_MESSAGE = 400;

    /**
     * The delay before the confirm save message is hidden. (in millis).
     * <p/>
     * 1000 + DURATION_CONFIRM_SAVE_MESSAGE = 1400
     * The confirm save message should stay on screen for 1 second.
     */
    protected static final long DELAY_HIDE_CONFIRM_SAVE_MESSAGE = 1400;



    /**
     * The color selected by the user.
     * <p/>
     * The user "selects" a color by pointing a color with the camera.
     */
    protected int mSelectedColor;

    /**
     * The last picked color.
     * <p/>
     * The user "picks" a color by clicking the preview.
     */
    protected int mLastPickedColor;

    /**
     * A simple {@link android.view.View} used for showing the picked color.
     */
    protected View mPickedColorPreview;

    /**
     * A simple {@link android.view.View} used for animating the color being picked.
     */
    protected View mPickedColorPreviewAnimated;

    /**
     * An {@link android.animation.ObjectAnimator} used for animating the color being picked.
     */
    protected ObjectAnimator mPickedColorProgressAnimator;

    /**
     * The delta for the translation on the x-axis of the mPickedColorPreviewAnimated.
     */
    protected float mTranslationDeltaX;

    /**
     * The delta for the translation on the y-axis of the mPickedColorPreviewAnimated.
     */
    protected float mTranslationDeltaY;

    /**
     * A simple {@link android.widget.TextView} used for showing a human readable representation of the picked color.
     */
    protected TextView mColorPreviewText;

    /**
     * A simple {@link android.view.View} used for showing the selected color.
     */
    protected View mPointerRing;

    /**
     * An icon representing the "save completed" state.
     */
    protected View mSaveCompletedIcon;

    /**
     * The save button.
     */
    protected View mSaveButton;

    /**
     * A float representing the progress of the "save completed" state.
     */
    protected float mSaveCompletedProgress;

    /**
     * An {@link android.animation.ObjectAnimator} used for animating the "save completed" state.
     */
    protected ObjectAnimator mSaveCompletedProgressAnimator;

    /**
     * A simple {@link android.widget.TextView} that confirms the user that the color has been saved successfully.
     */
    protected TextView mConfirmSaveMessage;

    /**
     * An {@link android.view.animation.Interpolator} used for showing the mConfirmSaveMessage.
     */
    protected Interpolator mConfirmSaveMessageInterpolator;

    /**
     * A {@link java.lang.Runnable} that hide the confirm save message.
     * <p/>
     * This runnable is posted with some delayed on mConfirmSaveMessage each time a color is successfully saved.
     */
    protected Runnable mHideConfirmSaveMessage;

    /**
     * A simple boolean for keeping track of the device's camera flash state.
     */
    protected boolean mIsFlashOn;


    protected FloatingActionButton mSpoid;

    private OnFragmentInteractionListener mListener;


    private String mURL = "http://savethecolor.xyz/api/colors";
    private String mToken;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String token) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, token);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mToken = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        initPickedColorProgressAnimator();
        initSaveCompletedProgressAnimator();

        mIsPortrait = getResources().getBoolean(R.bool.is_portrait);
        mPreviewContainer = (FrameLayout) view.findViewById(R.id.content_main_camera_preview);

        mPickedColorPreview = view.findViewById(R.id.activity_color_picker_color_preview);
        mPickedColorPreviewAnimated = view.findViewById(R.id.activity_color_picker_animated_preview);
        mPointerRing = view.findViewById(R.id.activity_color_picker_pointer_ring);
//        mSaveCompletedIcon = findViewById(R.id.activity_color_picker_save_completed);
        mSpoid = (FloatingActionButton) view.findViewById(R.id.app_bar_main_spoid);
        mSpoid.setOnClickListener(this);

        mSaveButton = view.findViewById(R.id.app_bar_main_save_button);
        mSaveButton.setOnClickListener(this);
        mConfirmSaveMessage = (TextView) view.findViewById(R.id.activity_color_picker_confirm_save_message);
        mHideConfirmSaveMessage = new Runnable() {
            @Override
            public void run() {
                mConfirmSaveMessage.animate()
                        .translationY(-mConfirmSaveMessage.getMeasuredHeight())
                        .setDuration(DURATION_CONFIRM_SAVE_MESSAGE)
                        .start();
            }
        };
        positionConfirmSaveMessage();
        mConfirmSaveMessageInterpolator = new DecelerateInterpolator();

        mLastPickedColor = ColorItems.getLastPickedColor(getContext());
        applyPreviewColor(mLastPickedColor);

        initTranslationDeltas();


        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume(){
        super.onResume();
        // Setup the camera asynchronously.
        mCameraAsyncTask = new CameraAsyncTask();
        mCameraAsyncTask.execute();
    }

    @Override
    public void onPause(){
        super.onPause();
        // Cancel the Camera AsyncTask.
        mCameraAsyncTask.cancel(true);

        // Release the camera.
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

        // Remove the camera preview
        if (mCameraPreview != null) {
            mPreviewContainer.removeView(mCameraPreview);
        }
    }

    @Override
    public void onDestroy(){
        mConfirmSaveMessage.removeCallbacks(mHideConfirmSaveMessage);
        super.onDestroy();
    }

    @Override
    public void onColorSelected(int color) {
        mSelectedColor = color;
        mPointerRing.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.app_bar_main_spoid:

                animatePickedColor(mSelectedColor);

                break;

            case R.id.app_bar_main_save_button:


                mTask = new ColorCreateTask(getContext(), ColorItem.makeHexString(mLastPickedColor));
                mTask.execute(mURL);


                break;

            default:
                break;

        }

    }


    /**
     * Position mConfirmSaveMessage.
     * <p/>
     * Set the translationY of mConfirmSaveMessage to - mConfirmSaveMessage.getMeasuredHeight() so that it is correctly placed before the first animation.
     */
    protected void positionConfirmSaveMessage() {
        ViewTreeObserver vto = mConfirmSaveMessage.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver vto = mConfirmSaveMessage.getViewTreeObserver();
                    vto.removeOnPreDrawListener(this);
                    mConfirmSaveMessage.setTranslationY(-mConfirmSaveMessage.getMeasuredHeight());
                    return true;
                }
            });
        }
    }

    /**
     * Initialize the deltas used for the translation of the preview of the picked color.
     */
    @SuppressLint("NewApi")
    protected void initTranslationDeltas() {
        ViewTreeObserver vto = mPointerRing.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver vto = mPointerRing.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                        vto.removeGlobalOnLayoutListener(this);
                    } else {
                        vto.removeOnGlobalLayoutListener(this);
                    }

                    final Rect pointerRingRect = new Rect();
                    final Rect colorPreviewAnimatedRect = new Rect();
                    mPointerRing.getGlobalVisibleRect(pointerRingRect);
                    mPickedColorPreviewAnimated.getGlobalVisibleRect(colorPreviewAnimatedRect);

                    mTranslationDeltaX = pointerRingRect.left - colorPreviewAnimatedRect.left;
                    mTranslationDeltaY = pointerRingRect.top - colorPreviewAnimatedRect.top;
                    ;
                }
            });
        }
    }


    /**
     * Initialize the animator used for the progress of the picked color.
     */
    protected void initPickedColorProgressAnimator() {
        mPickedColorProgressAnimator = ObjectAnimator.ofFloat(this, PICKED_COLOR_PROGRESS_PROPERTY_NAME, 1f, 0f);
        mPickedColorProgressAnimator.setDuration(400);
        mPickedColorProgressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mPickedColorPreviewAnimated.setVisibility(View.VISIBLE);
                mPickedColorPreviewAnimated.getBackground().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ColorItems.saveLastPickedColor(getActivity(), mLastPickedColor);
                applyPreviewColor(mLastPickedColor);
                mPickedColorPreviewAnimated.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mPickedColorPreviewAnimated.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

// -------------------- Flash --------------------
    /**
     * Check if the device's camera supports flash.
     *
     * @return Returns true if the device's camera supports flash, false otherwise.
     */
    protected boolean isFlashSupported() {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * Toggle the device's camera flash.
     * {@link CameraFragment#isFlashSupported()} should be called before using this methods.
     */
    protected void toggleFlash() {
        if (mCamera != null) {
            final Camera.Parameters parameters = mCamera.getParameters();
            final String flashParameter = mIsFlashOn ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH;
            parameters.setFlashMode(flashParameter);
            mCamera.stopPreview();
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mIsFlashOn = !mIsFlashOn;
            getActivity().invalidateOptionsMenu();
        }
    }
// -------------------- End --------------------

    /**
     * Initialize the animator used for the progress of the "save completed" state.
     */
    protected void initSaveCompletedProgressAnimator() {
        mSaveCompletedProgressAnimator = ObjectAnimator.ofFloat(this, SAVE_COMPLETED_PROGRESS_PROPERTY_NAME, 1f, 0f);
    }

    /**
     * Apply the preview color.
     * <p/>
     * Display the preview color and its human representation.
     *
     * @param previewColor the preview color to apply.
     */
    protected void applyPreviewColor(int previewColor) {
        setSaveCompleted(false);
        mPickedColorPreview.getBackground().setColorFilter(previewColor, PorterDuff.Mode.SRC_ATOP);
        //mColorPreviewText.setText(ColorItem.makeHexString(previewColor));
    }

    /**
     * Animate the color being picked.
     *
     * @param pickedColor the color being picked.
     */
    protected void animatePickedColor(int pickedColor) {
        mLastPickedColor = pickedColor;
        if (mPickedColorProgressAnimator.isRunning()) {
            mPickedColorProgressAnimator.cancel();
        }
        mPickedColorProgressAnimator.start();
    }

    /**
     * Set the "save completed" state.
     * True means that the save is completed. The preview color should not be saved again.
     *
     * @param isSaveCompleted the "save completed" state.
     */
    protected void setSaveCompleted(boolean isSaveCompleted) {
        mSaveButton.setEnabled(!isSaveCompleted);
//        mSaveCompletedProgressAnimator.cancel();
//        mSaveCompletedProgressAnimator.setFloatValues(mSaveCompletedProgress, isSaveCompleted ? 0f : 1f);
//        mSaveCompletedProgressAnimator.start();

        if (isSaveCompleted) {
            mConfirmSaveMessage.setVisibility(View.VISIBLE);
            mConfirmSaveMessage.animate().translationY(0).setDuration(DURATION_CONFIRM_SAVE_MESSAGE).setInterpolator(mConfirmSaveMessageInterpolator).start();
            mConfirmSaveMessage.removeCallbacks(mHideConfirmSaveMessage);
            mConfirmSaveMessage.postDelayed(mHideConfirmSaveMessage, DELAY_HIDE_CONFIRM_SAVE_MESSAGE);
        }
    }

    /**
     * Set the progress of the picked color animation.
     * <p/>
     * Used by {@link xyz.zzulu.savethecolor.fragments.CameraFragment#mPickedColorProgressAnimator}.
     *
     * @param progress A value in closed range [0,1] representing the progress of the picked color animation.
     */
    protected void setPickedColorProgress(float progress) {
        final float fastOppositeProgress = (float) Math.pow(1 - progress, 0.3f);
        final float translationX = (float) (mTranslationDeltaX * Math.pow(progress, 2f));
        final float translationY = mTranslationDeltaY * progress;

        mPickedColorPreviewAnimated.setTranslationX(translationX);
        mPickedColorPreviewAnimated.setTranslationY(translationY);
        mPickedColorPreviewAnimated.setScaleX(fastOppositeProgress);
        mPickedColorPreviewAnimated.setScaleY(fastOppositeProgress);
    }

    /**
     * Set the progress of the animation of the "save completed" state.
     * <p/>
     * Used by {@link xyz.zzulu.savethecolor.fragments.CameraFragment#mSaveCompletedProgressAnimator}.
     *
     * @param progress A value in closed range [0,1] representing the progress of the animation of the "save completed" state.
     */
    protected void setSaveCompletedProgress(float progress) {
//        mSaveButton.setScaleX(progress);
//        mSaveButton.setRotation(45 * (1 - progress));
//        mSaveCompletedIcon.setScaleX(1 - progress);
//        mSaveCompletedProgress = progress;
    }


    /**
     * Async task used to configure and start the camera preview.
     */
    private class CameraAsyncTask extends AsyncTask<Void, Void, Camera> {

        /**
         * The {@link android.view.ViewGroup.LayoutParams} used for adding the preview to its container.
         */
        protected FrameLayout.LayoutParams mPreviewParams;

        @Override
        protected Camera doInBackground(Void... params) {
            Camera camera = getCameraInstance();
            if (camera == null) {
                getActivity().finish();
            } else {
                //configure Camera parameters
                Camera.Parameters cameraParameters = camera.getParameters();

                //get optimal camera preview size according to the layout used to display it
                Camera.Size bestSize = Cameras.getBestPreviewSize(
                        cameraParameters.getSupportedPreviewSizes()
                        , mPreviewContainer.getWidth()
                        , mPreviewContainer.getHeight()
                        , mIsPortrait);
                //set optimal camera preview
                cameraParameters.setPreviewSize(bestSize.width, bestSize.height);
                if (cameraParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                camera.setParameters(cameraParameters);

                //set camera orientation to match with current device orientation
                Cameras.setCameraDisplayOrientation(getActivity(), camera);

                //get proportional dimension for the layout used to display preview according to the preview size used
                int[] adaptedDimension = Cameras.getProportionalDimension(
                        bestSize
                        , mPreviewContainer.getWidth()
                        , mPreviewContainer.getHeight()
                        , mIsPortrait);

                //set up params for the layout used to display the preview
                mPreviewParams = new FrameLayout.LayoutParams(adaptedDimension[0], adaptedDimension[1]);
                mPreviewParams.gravity = Gravity.CENTER;
            }
            return camera;
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);

            // Check if the task is cancelled before trying to use the camera.
            if (!isCancelled()) {
                mCamera = camera;
                if (mCamera == null) {
                    getActivity().finish();
                } else {
                    //set up camera preview
                    mCameraPreview = new CameraPreview(getActivity(), mCamera);
                    mCameraPreview.setOnColorSelectedListener(CameraFragment.this);

                    //add camera preview
                    mPreviewContainer.addView(mCameraPreview, 0, mPreviewParams);
                }
            }
        }

        @Override
        protected void onCancelled(Camera camera) {
            super.onCancelled(camera);
            if (camera != null) {
                camera.release();
            }
        }
    }



    private class ColorCreateTask extends UrlJsonAsyncTask {

        private final String mHex;

        public ColorCreateTask(Context context, String hex) {
            super(context);

            mHex = hex;
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject colorObj = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            try {
                // setup the returned values in case
                // something goes wrong
                json.put("success", false);
                json.put("info", "Something went wrong.");
                // add the user email and password to
                // the params
                holder.put("auth_token", mToken);
                colorObj.put("origin", mLastPickedColor);
                colorObj.put("hex", mHex);
                holder.put("color", colorObj);
                StringEntity se = new StringEntity(holder.toString());
                post.setEntity(se);

                // setup the request headers
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-Type", "application/json");

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = client.execute(post, responseHandler);
                json = new JSONObject(response);

            } catch (HttpResponseException e) {
                e.printStackTrace();
                Log.e("ClientProtocol", "" + e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IO", "" + e);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("success")) {
                    // everything is ok
                    ColorItems.saveColorItem(getActivity(), new ColorItem(mLastPickedColor, json.getJSONObject("data").getInt("id")));
                    setSaveCompleted(true);

                } else {

                }
                Toast.makeText(context, json.getString("info"), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // something went wrong: show a Toast with the exception message
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }

    }









}
