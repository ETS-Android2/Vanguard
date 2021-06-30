package com.sandile.vanguard.Views.Fragmants;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.model.PlaceType;
import com.sandile.vanguard.CustomPlace;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    //Firebase
    private DatabaseReference firebaseReference;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //Pallets
    private ListView lv_userDetails;
    private LinearProgressIndicator pb_loadingProfile;

    //Objects
    public static UserDetail userDetailGlobal;

    //Other
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        lv_userDetails = view.findViewById(R.id.profile_lv_userDetails);
        lv_userDetails.setOnItemClickListener((adapterView, view1, i, l) -> {
            if(i == 0){
                new SnackTwo().orangeSnack(getActivity(), "You cannot change your email");
            }
            if(i == 1){
                placeTypeListDialog();
            }
            if(i == 2){
                googlePlaceSearch();

                //This brings up dialog to allow users to enter new place
//                changeUserDetailDialog("favouriteLandmark");
            }
            if(i == 3){
                changeIsMetricDialog();
            }
        });

        getUserDetailsFirebase();

        return view;
    }

    private void googlePlaceSearch(){
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    //For now this gets information returned by google search
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                changeUserFavPLace("favouriteLandmark", new CustomPlace(place.getAddress(), place.getId(), place.getName(), place.getLatLng().latitude, place.getLatLng().longitude));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                new SnackTwo().redSnack(getActivity(), status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                new SnackTwo().orangeSnack(getActivity(), "Click on a place to add it!");
            }
            return;
        }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pb_loadingProfile = getActivity().findViewById(R.id.landmarks_pb_loadingProfile);
    }

    public void getUserDetailsFirebase(){

        firebaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserDetail.currentUserId);

        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserDetail userDetail = new UserDetail();
                    Log.e("Profile class", "onDataChange: "+snapshot.child("email").getValue().toString());
                    userDetail.setEmail(snapshot.child("email").getValue().toString());
                    userDetail.setFavouriteLandmark(snapshot.child("favouriteLandmark").getValue().toString());
                    userDetail.setPreferredLandmarkType(snapshot.child("preferredLandmarkType").getValue().toString());
                    userDetail.setIsMetric(Boolean.parseBoolean(snapshot.child("isMetric").getValue().toString()));

                    userDetailGlobal = userDetail;

                    userDetail.setUserSessionDetails(userDetail);

                    listSetup(userDetail);
                }
                else{
                    pb_loadingProfile.hide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pb_loadingProfile.hide();
                new SnackTwo().redSnack(getActivity(), error.getMessage());
            }
        });
    }

    private void listSetup(UserDetail inUserDetail){
        ArrayList<String> userDetailsList = new ArrayList<>();
        userDetailsList.clear();
        userDetailsList.add("Email: " + inUserDetail.getEmail());

        String s1 = inUserDetail.getPreferredLandmarkType();
        String replaceString =s1.replace('_',' ');

        replaceString = replaceString.substring(0,1).toUpperCase() + replaceString.substring(1).toLowerCase();

        userDetailsList.add("Preferred landmark type: " + replaceString);
        userDetailsList.add("Favourite place: " + inUserDetail.getFavouriteLandmark());

        if(inUserDetail.getIsMetric()){
            userDetailsList.add("Measurement system: Metric");
        }
        else if(!inUserDetail.getIsMetric()){
            userDetailsList.add("Measurement system: Imperial");
        }

        if(!userDetailsList.isEmpty()){
            pb_loadingProfile.hide();
            ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, userDetailsList);
            lv_userDetails.setAdapter(arrayAdapter);
        }else{
            pb_loadingProfile.hide();
            new SnackTwo().redSnack(this.getActivity(), "Could not get details.");
            userDetailsList.clear();
            userDetailsList.add("Could not get details!");
            ArrayAdapter arrayAdapter2 = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, userDetailsList);
            lv_userDetails.setAdapter(arrayAdapter2);
        }
    }

    private void changeIsMetricDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Measurement system");
        builder.setMessage("Are you sure you want to change your measurement system?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pb_loadingProfile.show();
                        Boolean tempIsMetric  = userDetailGlobal.getIsMetric();
                        if(tempIsMetric){
                            changeIsMetric(false);
                        }else{
                            changeIsMetric(true);
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SnackTwo().orangeSnack(getActivity(), "You can change anytime!");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeUserDetailDialog(String inToChange){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Enter your new " + inToChange);

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            pb_loadingProfile.setVisibility(View.VISIBLE);
            changeUserDetail(inToChange, input.getText().toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void changeIsMetric(Boolean inIsMetric){
        FirebaseDatabase.getInstance().getReference("users")
                .child(UserDetail.currentUserId).child("isMetric")
                .setValue(inIsMetric).addOnCompleteListener(task -> {
            pb_loadingProfile.hide();
            if(task.isSuccessful()){
                pb_loadingProfile.hide();
                new SnackTwo().greenSnack(getActivity(), "Measurement system has been changed!");
            }
            else{
                pb_loadingProfile.hide();
                new SnackTwo().redSnack(getActivity(), "Something went wrong\nCould not change system!");
            }
        }).addOnFailureListener(e -> {
            pb_loadingProfile.hide();
            new SnackTwo().redSnack(getActivity(), e.getMessage());
        });
    }

    private void changeUserDetail(String inToChange, String inNewValue){
        FirebaseDatabase.getInstance().getReference("users")
                .child(UserDetail.currentUserId).child(inToChange)
                .setValue(inNewValue).addOnCompleteListener(task -> {
            pb_loadingProfile.hide();
            if(task.isSuccessful()){
                new SnackTwo().greenSnack(getActivity(), inToChange+" has been changed!");
            }
            else{
                new SnackTwo().redSnack(getActivity(), "Something went wrong :-(!");
            }
        }).addOnFailureListener(e -> {
            pb_loadingProfile.hide();
            new SnackTwo().redSnack(getActivity(), e.getMessage());
        });
    }

    private void changeUserFavPLace(String inToChange, Object inNewValue){
        FirebaseDatabase.getInstance().getReference("users")
                .child(UserDetail.currentUserId).child(inToChange)
                .setValue(inNewValue).addOnCompleteListener(task -> {
            pb_loadingProfile.hide();
            if(task.isSuccessful()){
                new SnackTwo().greenSnack(getActivity(), inToChange+" has been changed!");
            }
            else{
                new SnackTwo().redSnack(getActivity(), "Something went wrong :-(!");
            }
        }).addOnFailureListener(e -> {
            pb_loadingProfile.hide();
            new SnackTwo().redSnack(getActivity(), e.getMessage());
        });
    }

    private void  placeTypeListDialog(){
        ListView placeTypeListView = new ListView(getContext());

        List<String> placeTypeData = new ArrayList<String>();

        for(int i = 0; i < Arrays.stream(PlaceType.values()).count(); i++){
            String s1 = PlaceType.values()[i].toString();
            String replaceString =s1.replace('_',' ');

            replaceString = replaceString.substring(0,1).toUpperCase() + replaceString.substring(1).toLowerCase();

            placeTypeData.add(replaceString);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, placeTypeData);
        placeTypeListView.setAdapter(arrayAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(placeTypeListView);
        builder.create();
        AlertDialog dialog =  builder.show();
        dialog.show();

        placeTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//When user clicks on item
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeUserDetail("preferredLandmarkType", PlaceType.values()[position].toString());
                dialog.dismiss();
            }
        });

    }

}