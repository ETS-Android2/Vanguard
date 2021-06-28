package com.sandile.vanguard.Views.Fragmants;

import android.content.DialogInterface;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandile.vanguard.Plan;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PlannerFragment extends Fragment {

    private LinearProgressIndicator pb_loadingPlans;
    private FloatingActionButton fab_addPlan;

    //Get trips from firebase and store here temp
    final ArrayList<Plan> tempPlans = new ArrayList<Plan>();
    private ListView lv_plans;


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

        getPlans();
    }

    private void palletsSetup(View view) {
        pb_loadingPlans = view.findViewById(R.id.planner_pb_loadingProfile);
        // F_A_B to add plans
        fab_addPlan = view.findViewById(R.id.planner_fab_addPlan);
        fab_addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 addPlan();
            }
        });

        lv_plans = view.findViewById(R.id.planner_lv_plans);
        lv_plans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDetailsDetailsDialog(tempPlans.get(i));
            }
        });

    }

    private void addPlan(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Enter plan name");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String tempPlanName = input.getText().toString().trim();

                if(tempPlanName.equals(null)){
                    new SnackTwo().redSnack(getActivity(), "You didn't enter plan name!");
                    return;
                }

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Enter plan details");

                // Set up the input
                final EditText input = new EditText(getContext());
                // Specify the type of input expected
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHorizontalScrollBarEnabled(false);
                input.setLines(4);
                input.setSingleLine(false);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                        LocalDateTime now = LocalDateTime.now();

                        if(input.getText().toString().trim().equals(null)){
                            new SnackTwo().redSnack(getActivity(), "You didn't enter plan details.");
                            return;
                        }

                        Plan tempPlan = new Plan(tempPlanName, input.getText().toString(), dtf.format(now));

                        savePlan(tempPlan);
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
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    //Works, can save trip
    private void savePlan(Plan inPlan) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(UserDetail.currentUserId).child("plans").child(inPlan.getPlanDate())
                .setValue(inPlan).addOnCompleteListener(task -> {
            pb_loadingPlans.hide();

            if(task.isSuccessful()){
                new SnackTwo().greenSnack(getActivity(), "Plan created");
            }
            else{
                new SnackTwo().redSnack(getActivity(), task.getException().toString());
            }
        }).addOnFailureListener(e -> {
            pb_loadingPlans.hide();
            new SnackTwo().redSnack(getActivity(), e.getMessage());
        });
    }

    //Getting all plans from firebase
    private void getPlans(){
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserDetail.currentUserId).child("plans");

        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    Iterable<DataSnapshot> snapshotPlaces = snapshot.getChildren();

                    tempPlans.clear();
                    for(DataSnapshot child: snapshotPlaces){
                        tempPlans.add(child.getValue(Plan.class));
                    }

                    plansListSetup(tempPlans);
                }
                else{
                    pb_loadingPlans.hide();
                    new SnackTwo().orangeSnack(getActivity(), "You don't have any plans saved!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pb_loadingPlans.hide();
                new SnackTwo().redSnack(getActivity(), error.getMessage());
            }
        });
    }

    private void plansListSetup(ArrayList<Plan> inPlans){
        ArrayList<String> tempPlans = new ArrayList<>();

        for(int i = 0; i < inPlans.size(); i++){
            tempPlans.add(inPlans.get(i).getPlanName() + "\n" + inPlans.get(i).getPlanDate());
        }

        if(!tempPlans.isEmpty()){
            pb_loadingPlans.hide();
            ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, tempPlans);
            lv_plans.setAdapter(arrayAdapter);
        }
        else if(tempPlans == null){
            new SnackTwo().orangeSnack(getActivity(), "You have no plans");
        }
        else{
            pb_loadingPlans.hide();
            new SnackTwo().orangeSnack(getActivity(), "You have no plans");
        }
    }

    private void showDetailsDetailsDialog(Plan inPlanDetails){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(inPlanDetails.getPlanName()+" details");
        builder.setMessage("Date: " + inPlanDetails.getPlanDate()+
                "\n\nDetails: " + inPlanDetails.getPlanDetails());
        builder.setNegativeButton("Ok", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }























    public PlannerFragment() {
        // Required empty public constructor
    }
}