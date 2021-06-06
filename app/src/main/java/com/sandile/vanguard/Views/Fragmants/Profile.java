package com.sandile.vanguard.Views.Fragmants;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener{

    private ListView lv_userDetails;

    private DatabaseReference firebaseReference;

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

        getUserDetailsFirebase(lv_userDetails);

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



    public void getUserDetailsFirebase(ListView inListView){
        firebaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("bv1lgaYegqbimEts9LcbLqUAwai1");

        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserDetail userDetailSnapshot = new UserDetail();
                    userDetailSnapshot.setEmail(snapshot.child("email").getValue().toString());
                    userDetailSnapshot.setFavouriteLandmark(snapshot.child("favouriteLandmark").getValue().toString());
                    userDetailSnapshot.setPreferredLandmarkType(snapshot.child("preferredLandmarkType").getValue().toString());
                    userDetailSnapshot.setIsMetric(Boolean.parseBoolean(snapshot.child("isMetric").getValue().toString()));

                    listSetup(userDetailSnapshot, inListView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SnackTwo().redSnack(getActivity(), error.getMessage());
            }

        });


    }

    private void listSetup(UserDetail inUserDetail, ListView inListView){
        ArrayList<String> userDetailsList = new ArrayList<>();
        userDetailsList.clear();
        userDetailsList.add("Email: " + inUserDetail.getEmail());
        userDetailsList.add("PreferredLandmarkType: " + inUserDetail.getPreferredLandmarkType());
        userDetailsList.add("FavouriteLandmark: " + inUserDetail.getFavouriteLandmark());
        userDetailsList.add("IsMetric: " + inUserDetail.getIsMetric().toString());

        ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, userDetailsList);

        inListView.setAdapter(arrayAdapter);


    }

}