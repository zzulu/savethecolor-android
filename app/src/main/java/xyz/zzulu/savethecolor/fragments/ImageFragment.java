package xyz.zzulu.savethecolor.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.zzulu.savethecolor.R;
import xyz.zzulu.savethecolor.views.CameraPreview;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnImageFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "imagePath";

    // TODO: Rename and change types of parameters
    private String mImgPath;

    private OnImageFragmentInteractionListener mListener;

    private ImageView mImageView;



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






    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(String param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgPath = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image, container, false);

        mImageView = (ImageView) view.findViewById(R.id.image_container);

        mImageView.setImageBitmap(BitmapFactory.decodeFile(mImgPath));
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);



        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onImageFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImageFragmentInteractionListener) {
            mListener = (OnImageFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnImageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDestroy(){
        //mConfirmSaveMessage.removeCallbacks(mHideConfirmSaveMessage);
        super.onDestroy();
    }
//
//    @Override
//    public void onColorSelected(int color) {
//        mSelectedColor = color;
//        mPointerRing.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.app_bar_main_spoid:

                //animatePickedColor(mSelectedColor);

                break;

            case R.id.app_bar_main_save_button:

//            ColorItems.saveColorItem(this, new ColorItem(mLastPickedColor));
//            setSaveCompleted(true);

                break;

            default:
                break;

        }

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
    public interface OnImageFragmentInteractionListener {
        // TODO: Update argument type and name
        void onImageFragmentInteraction(Uri uri);
    }
}
