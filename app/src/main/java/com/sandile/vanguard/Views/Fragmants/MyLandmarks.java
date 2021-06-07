package com.sandile.vanguard.Views.Fragmants;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandile.vanguard.PlaceDetails;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;
import com.sandile.vanguard.Views.Activites.MainActivity;
import com.sandile.vanguard.Views.Activites.RegisterActivity;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyLandmarks extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //using map api
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    //Others
    private FloatingActionButton fab_add;
    private ListView lv_landmarks;
    private LinearProgressIndicator pb_loadingLandmarks;

    final ArrayList<PlaceDetails> tempPlaces = new ArrayList<PlaceDetails>();

    public MyLandmarks() {
        // Required empty public constructor
    }

    public static MyLandmarks newInstance(String param1, String param2) {
        MyLandmarks fragment = new MyLandmarks();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_landmarks, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Places.initialize(this.getContext(), "AIzaSyANaY7LDVroXKDyKlUKiIIg6oAUeIOCDbw");

        lv_landmarks = getActivity().findViewById(R.id.landmarks_lv_landmarks);
        lv_landmarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showLandmarkDetailsDialog(tempPlaces.get(i));
            }
        });
        pb_loadingLandmarks = getActivity().findViewById(R.id.landmarks_pb_loadingLandmarks);

        fab_add = getActivity().findViewById(R.id.landmarks_fab_add);
        fab_add.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        getLandmark();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                saveLocation(new PlaceDetails(place.getAddress(), place.getId(), place.getName(), place.getLatLng().latitude, place.getLatLng().longitude));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                new SnackTwo().redSnack(getActivity(), status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                new SnackTwo().orangeSnack(getActivity(), "Click on a place to add it!");
            }
            return;
        }
    }

    private void saveLocation(PlaceDetails inPlace){
        FirebaseDatabase.getInstance().getReference("users")
                .child(UserDetail.currentUserId).child("locations").child(inPlace.getId())
                .setValue(inPlace).addOnCompleteListener(task -> {
            pb_loadingLandmarks.hide();
                    if(task.isSuccessful()){
                        new SnackTwo().greenSnack(getActivity(), "Location has been added to your list!");
                    }
                    else{
                        new SnackTwo().redSnack(getActivity(), "Something went wrong\nCould not save ");
                    }
                }).addOnFailureListener(e -> {
            pb_loadingLandmarks.hide();
            new SnackTwo().redSnack(getActivity(), e.getMessage());
        });
    }

    private void getLandmark(){
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserDetail.currentUserId).child("locations");

        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    Iterable<DataSnapshot> snapshotPlaces = snapshot.getChildren();

                    tempPlaces.clear();
                    for(DataSnapshot child: snapshotPlaces){
                        tempPlaces.add(child.getValue(PlaceDetails.class));
                    }

                    landmarksListSetup(tempPlaces);
                }
                else{
                    new SnackTwo().orangeSnack(getActivity(), "You don't have any landmarks saved!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pb_loadingLandmarks.hide();
                new SnackTwo().redSnack(getActivity(), error.getMessage());
            }
        });
    }

    private void landmarksListSetup(ArrayList<PlaceDetails> inPlaceList){
        ArrayList<String> tempPlaces = new ArrayList<>();

        for(int i = 0; i < inPlaceList.size(); i++){
            tempPlaces.add(inPlaceList.get(i).getName() + "\n" + inPlaceList.get(i).getAddress());
        }


        if(!tempPlaces.isEmpty()){
            pb_loadingLandmarks.hide();
            ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, tempPlaces);
            lv_landmarks.setAdapter(arrayAdapter);
        }
        else if(tempPlaces == null){
            new SnackTwo().orangeSnack(getActivity(), "You have no landmarks");
        }
        else{
            pb_loadingLandmarks.hide();
            new SnackTwo().orangeSnack(getActivity(), "You have no landmarks");
        }
    }

    private void showLandmarkDetailsDialog(PlaceDetails inPlaceDetails){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(inPlaceDetails.getName()+" details");
        builder.setMessage("Address: " + inPlaceDetails.getAddress()+
                "\n\nId: " + inPlaceDetails.getId()+
                "\n\nLatitude: " + inPlaceDetails.getLatitude()+
                "\n\nLongitude: "+inPlaceDetails.getLongitude());
        builder.setNegativeButton("Ok", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}