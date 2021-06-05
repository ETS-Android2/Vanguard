package com.sandile.vanguard.Views.Fragmants;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sandile.vanguard.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener{

    private ListView lv_userDetails;
    ArrayList<String> userDetailsList = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";// TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM2 = "param2";// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String mParam1;// TODO: Rename and change types of parameters
    private String mParam2;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        lv_userDetails = view.findViewById(R.id.profile_lv_userDetails);

        listSetup(lv_userDetails);

        return view;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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
    public void onClick(View v) {//What happens when user clicks on..
        switch (v.getId()) {//Sign up button
            case R.id.profile_lv_userDetails://register
                //Do something
                break;
        }

    }



    private void listSetup(ListView inListView){
        userDetailsList.add("name:");
        userDetailsList.add("surname:");
        userDetailsList.add("age:");
        userDetailsList.add("something else:");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, userDetailsList);

        inListView.setAdapter(arrayAdapter);
    }
}