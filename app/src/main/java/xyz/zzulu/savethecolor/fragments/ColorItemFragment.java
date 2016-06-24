package xyz.zzulu.savethecolor.fragments;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.List;

import xyz.zzulu.savethecolor.R;
import xyz.zzulu.savethecolor.data.ColorItem;
import xyz.zzulu.savethecolor.data.ColorItems;
import xyz.zzulu.savethecolor.views.ColorItemFragmentAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ColorItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ColorItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //    private static final String ARG_PARAM1 = "param1";

    private ColorItemFragmentAdapter mColorItemAdapter;
    private ListView mListView;
    private ColorItems.OnColorItemChangeListener mOnColorItemChangeListener;
    private String mClipColorItemLabel;
    private Toast mToast;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    private OnFragmentInteractionListener mListener;


    private ColorDestroyTask mTask = null;


    private String mURL = "http://savethecolor.xyz/api/colors/delete";


    public ColorItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ColorItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorItemFragment newInstance() {
        ColorItemFragment fragment = new ColorItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, Menu.NONE, "삭제");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo menuInfo;

        int index;

        switch (item.getItemId()) {

            case Menu.FIRST:
                menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                index = menuInfo.position;

                final ColorItem colorItem = mColorItemAdapter.getItem(index);

                mTask = new ColorDestroyTask(getContext(), colorItem);
                mTask.execute(mURL);

                return true;

        }

        return false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_color_item, container, false);

        mClipColorItemLabel = getContext().getString(R.string.color_clip_color_label_hex);

        mColorItemAdapter = new ColorItemFragmentAdapter(getContext());
        mColorItemAdapter.addAll(ColorItems.getSavedColorItems(getContext()));
        mListView = (ListView) view.findViewById(R.id.view_color_item_list_page_list_view);
        mListView.setAdapter(mColorItemAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ColorItem colorItem = mColorItemAdapter.getItem(position);
//                ColorDetailActivity.startWithColorItem(view.getContext(), colorItem,
//                        view.findViewById(R.id.row_color_item_preview));
            }
        });


        registerForContextMenu(mListView);

//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                final ColorItem colorItem = mColorItemAdapter.getItem(position);
////                ClipDatas.clipPainText(view.getContext(), mClipColorItemLabel, colorItem.getHexString());
////                showToast(R.string.color_clip_success_copy_message);
//                return true;
//            }
//        });


        mOnColorItemChangeListener = new ColorItems.OnColorItemChangeListener() {
            @Override
            public void onColorItemChanged(List<ColorItem> colorItems) {
                mColorItemAdapter.clear();
                mColorItemAdapter.addAll(colorItems);
                mColorItemAdapter.notifyDataSetChanged();
            }
        };

        // Create an ActivityLifecycleCallbacks to hide the eventual toast that could be displayed when the
        // hosting activity goes on pause.
        mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (getContext() == activity) {
                    hideToast();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        };


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
     * Hide the current {@link android.widget.Toast}.
     */
    private void hideToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    /**
     * Show a toast text message.
     *
     * @param resId The resource id of the string resource to use.
     */
    private void showToast(@StringRes int resId) {
        hideToast();
        mToast = Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT);
        mToast.show();
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


    private class ColorDestroyTask extends UrlJsonAsyncTask {

        private final ColorItem mItem;


        public ColorDestroyTask(Context context, ColorItem item) {
            super(context);

            mItem = item;

        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost delete = new HttpPost(urls[0]);

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
                colorObj.put("id", mItem.getId());
                holder.put("color", colorObj);
                StringEntity se = new StringEntity(holder.toString());
                delete.setEntity(se);

                // setup the request headers
                delete.setHeader("Accept", "application/json");
                delete.setHeader("Content-Type", "application/json");

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = client.execute(delete, responseHandler);
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

                    ColorItems.deleteColorItem(getActivity(), mItem);

                    mColorItemAdapter.clear();
                    mColorItemAdapter.addAll(ColorItems.getSavedColorItems(getContext()));
                    mColorItemAdapter.notifyDataSetChanged();

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