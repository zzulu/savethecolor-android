package xyz.zzulu.savethecolor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by hwangjw on 16. 6. 19.
 */
public class TouchImageView extends ImageView {


    /* onColorSelected */

    /**
     * An array of 3 integers representing the color being selected.
     */
    protected int[] mSelectedColor;
    protected OnColorSelectedListener mOnColorSelectedListener;
    protected int[] mPreviewSize = {0,0};

    protected static final int POINTER_RADIUS = 5;




    private Paint borderPaint = null;
    private Paint backgroundPaint = null;

    private float mPosX = 0f;
    private float mPosY = 0f;

    private float mLastTouchX;
    private float mLastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    public TouchImageView(Context context) {
        this(context, null, 0);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    // Existing code ...
    public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        mSelectedColor = new int[3];

        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        borderPaint = new Paint();
        borderPaint.setARGB(255, 255, 128, 0);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);

        backgroundPaint = new Paint();
        backgroundPaint.setARGB(32, 255, 255, 255);
        backgroundPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#draw(android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, borderPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
//        canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, backgroundPaint);
//        if (this.getDrawable() != null) {
//            canvas.save();
//            canvas.translate(mPosX, mPosY);
//
//            Matrix matrix = new Matrix();
//            matrix.postScale(mScaleFactor, mScaleFactor, pivotPointX,
//                    pivotPointY);
//            // canvas.setMatrix(matrix);
//
//            canvas.drawBitmap(
//                    ((BitmapDrawable) this.getDrawable()).getBitmap(), matrix,
//                    null);
//
//            // this.getDrawable().draw(canvas);
//            canvas.restore();
//        }
//
//        mPreviewSize[0] = canvas.getWidth();
//        mPreviewSize[1] = canvas.getHeight();
//
//        if (mOnColorSelectedListener != null) {
//            final int midX = mPreviewSize[0] / 2;
//            final int midY = mPreviewSize[0] / 2;
//
//            // Reset the selected color.
//            mSelectedColor[0] = 0;
//            mSelectedColor[1] = 0;
//            mSelectedColor[2] = 0;
//
//            // Compute the average selected color.
//            for (int i = 0; i <= POINTER_RADIUS; i++) {
//                for (int j = 0; j <= POINTER_RADIUS; j++) {
//                    addColorFromYUV420(canvas, mSelectedColor, (i * POINTER_RADIUS + j + 1),
//                            (midX - POINTER_RADIUS) + i, (midY - POINTER_RADIUS) + j,
//                            mPreviewSize[0], mPreviewSize[1]);
//                }
//            }
//
//            mOnColorSelectedListener.onColorSelected(Color.rgb(mSelectedColor[0], mSelectedColor[1], mSelectedColor[2]));
//        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable
     * )
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        // Constrain to given size but keep aspect ratio
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        mLastTouchX = mPosX = 0;
        mLastTouchY = mPosY = 0;

        int borderWidth = (int) borderPaint.getStrokeWidth();
        mScaleFactor = Math.min(((float) getLayoutParams().width - borderWidth)
                / width, ((float) getLayoutParams().height - borderWidth)
                / height);
        pivotPointX = (((float) getLayoutParams().width - borderWidth) - (int) (width * mScaleFactor)) / 2;
        pivotPointY = (((float) getLayoutParams().height - borderWidth) - (int) (height * mScaleFactor)) / 2;
        super.setImageDrawable(drawable);
    }

    float pivotPointX = 0f;
    float pivotPointY = 0f;

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            pivotPointX = detector.getFocusX();
            pivotPointY = detector.getFocusY();

            Log.d(LOG_TAG, "mScaleFactor " + mScaleFactor);
            Log.d(LOG_TAG, "pivotPointY " + pivotPointY + ", pivotPointX= "
                    + pivotPointX);
            mScaleFactor = Math.max(0.05f, mScaleFactor);

            invalidate();
            return true;
        }
    }













    protected void addColorFromYUV420(byte[] data, int[] averageColor, int count, int x, int y, int width, int height) {
        // The code converting YUV420 to rgb format is highly inspired from this post http://stackoverflow.com/a/10125048
        final int size = width * height;
        final int Y = data[y * width + x] & 0xff;
        final int xby2 = x / 2;
        final int yby2 = y / 2;

        final float V = (float) (data[size + 2 * xby2 + yby2 * width] & 0xff) - 128.0f;
        final float U = (float) (data[size + 2 * xby2 + 1 + yby2 * width] & 0xff) - 128.0f;

        // Do the YUV -> RGB conversion
        float Yf = 1.164f * ((float) Y) - 16.0f;
        int red = (int) (Yf + 1.596f * V);
        int green = (int) (Yf - 0.813f * V - 0.391f * U);
        int blue = (int) (Yf + 2.018f * U);

        // Clip rgb values to [0-255]
        red = red < 0 ? 0 : red > 255 ? 255 : red;
        green = green < 0 ? 0 : green > 255 ? 255 : green;
        blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;

        averageColor[0] += (red - averageColor[0]) / count;
        averageColor[1] += (green - averageColor[1]) / count;
        averageColor[2] += (blue - averageColor[2]) / count;
    }



    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        mOnColorSelectedListener = onColorSelectedListener;
    }



    /**
     * An interface for callback.
     */
    public interface OnColorSelectedListener {

        /**
         * Called when a new color has just been selected.
         *
         * @param newColor the new color that has just been selected.
         */
        void onColorSelected(int newColor);
    }



}