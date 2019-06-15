package com.example.hp.tiptip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link User_infoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link User_infoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_infoFragment extends Fragment {
    private ImageView head;
    private TextView username;
    private TextView sex;
    private TextView birthday;
    private TextView phone;
    private TextView email;
    private TextView sign;
    private String user_id;
    private String url = Urls.GET_USER_INFO_URL;
    private Button btn_modify;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public User_infoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment User_infoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static User_infoFragment newInstance(String param1, String param2) {
        User_infoFragment fragment = new User_infoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_userinfo, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUser_infoFragmentInteraction(uri);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUser_infoFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        username = getActivity().findViewById(R.id.username);
        sex = getActivity().findViewById(R.id.sex);
        birthday = getActivity().findViewById(R.id.birthday);
        phone = getActivity().findViewById(R.id.phone);
        email = getActivity().findViewById(R.id.email);
        sign = getActivity().findViewById(R.id.sign);
        requestDate();
        init();

        getActivity().findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NIMClient.getService(AuthService.class).logout();
                ACache aCache = ACache.get(getActivity());
                aCache.remove("userId");
                aCache.remove("token");
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        });


    }

    public void init(){
        btn_modify = getActivity().findViewById(R.id.btn_modify);

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),ModifyUser_infoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestDate() {
        ACache aCache = ACache.get(getActivity());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("user_id",aCache.getAsString("userId"))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responsedata = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(responsedata);
                            //  String id = jsonObject.getString("user_id");
                            String  nichen = jsonObject.getString("username");
                            String   xibie = jsonObject.getString("sex");
                            String  shengri = jsonObject.getString("birthday");
                            String  shouji = jsonObject.getString("phone");
                            String  youxiang = jsonObject.getString("email");
                            String qianming = jsonObject.getString("sign");

                            username.setText(nichen);
                            sex.setText(xibie);
                            birthday.setText(shengri);
                            phone.setText(shouji);
                            email.setText(youxiang);
                            sign.setText(qianming);

                        } catch (JSONException e){
                            e.printStackTrace();

                        }

                    }
                });

            }

        });

    }
}
