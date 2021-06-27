package com.sandile.vanguard.Views.Fragmants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

import java.util.ArrayList;

public class PlannerFragment extends Fragment {

    private LinearProgressIndicator pb_loadingPlans;
    private FloatingActionButton fab_addPlan;

    final ArrayList<PlaceDetails> tempPlanner = new ArrayList<PlaceDetails>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        palletsSetup(view);

    }

    private void palletsSetup(View view) {
        pb_loadingPlans = getActivity().findViewById(R.id.planner_pb_loadingProfile);
        // F_A_B to add plans
        fab_addPlan = getActivity().findViewById(R.id.planner_fab_addPlan);
        fab_addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 addTripDialog();
            }
        });


    }

    private void addTripDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Enter email you registered with");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SnackTwo().orangeSnack(getActivity(), "You pressed ok");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

//    private void getPlans(){
//        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserDetail.currentUserId).child("locations");
//
//        firebaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//
//                    Iterable<DataSnapshot> snapshotPlaces = snapshot.getChildren();
//
//                    tempPlaces.clear();
//                    for(DataSnapshot child: snapshotPlaces){
//                        tempPlaces.add(child.getValue(PlaceDetails.class));
//                    }
//
//                    landmarksListSetup(tempPlaces);
//                }
//                else{
//                    new SnackTwo().orangeSnack(getActivity(), "You don't have any landmarks saved!");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                pb_loadingLandmarks.hide();
//                new SnackTwo().redSnack(getActivity(), error.getMessage());
//            }
//        });
//    }

    private void getPlanDetails(){

    }























    public PlannerFragment() {
        // Required empty public constructor
    }
}