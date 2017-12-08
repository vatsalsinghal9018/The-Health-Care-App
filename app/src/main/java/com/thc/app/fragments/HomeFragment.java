package com.thc.app.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.gson.Gson;
import com.thc.app.BaseFragment;
import com.thc.app.R;
import com.thc.app.activities.HealthStatusActivity;
import com.thc.app.activities.MapActivity;
import com.thc.app.activities.SelectFoodDialogActivity;
import com.thc.app.models.OneDayData;
import com.thc.app.utils.AppConstants;
import com.thc.app.utils.AppSharedPreference;
import com.thc.app.utils.DataPreference;
import com.thc.app.utils.Urls;
import com.thc.app.utils.Utils;
import com.thc.app.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @Bind(R.id.switch1)
    Switch switch1;
    @Bind(R.id.runningValue)
    TextView runningValue;
    @Bind(R.id.walkingValue)
    TextView walkingValue;
    @Bind(R.id.idleValue)
    TextView idleValue;
    @Bind(R.id.mealBreakfast)
    TextView mealBreakfast;
    @Bind(R.id.mealBreakfastCal)
    TextView mealBreakfastCal;
    @Bind(R.id.mealLunch)
    TextView mealLunch;
    @Bind(R.id.mealLunchCal)
    TextView mealLunchCal;
    @Bind(R.id.mealDinner)
    TextView mealDinner;
    @Bind(R.id.mealDinnerCal)
    TextView mealDinnerCal;
    @Bind(R.id.calarieCount)
    TextView calarieCount;
    @Bind(R.id.milesKeyValue)
    TextView milesKeyValue;
    @Bind(R.id.viewMapButton)
    Button viewMapButton;
    @Bind(R.id.analyseButton)
    Button analyseButton;
    @Bind(R.id.analyseHeart)
    Button analyseHeart;
    @Bind(R.id.burntCalarieCount)
    TextView burntCalarieCount;

    View view;
    String bp="",chol="";
    String bs="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        analyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HealthStatusActivity.class));
            }
        });

        analyseHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d=new Dialog(getActivity());
                d.setContentView(R.layout.heart_diag);
                ((Spinner)d.findViewById(R.id.bs)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        bs=position+"";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                d.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bp=((EditText)d.findViewById(R.id.bp)).getText().toString();
                        chol=((EditText)d.findViewById(R.id.chol)).getText().toString();

                        if(bs.equals("") || bp.equals("") || chol.equals(""))
                            Toast.makeText(getActivity(),"Field(s) missing",Toast.LENGTH_SHORT).show();
                        else
                        {
                            StringRequest stringRequest=new StringRequest(Request.Method.POST, Urls.heart
                                    , new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try
                                    {
                                        Log.e("heart res",response);
                                        JSONObject jsonObject=new JSONObject(response);
                                        if(jsonObject.getString("status").equals("success"))
                                        {
                                            String cond=jsonObject.getString("heart");
                                            if(cond.equals("0"))
                                                Toast.makeText(getActivity(),"Your heart condition is very good",Toast.LENGTH_LONG).show();
                                            else if(cond.equals("1"))
                                                Toast.makeText(getActivity(),"Your heart condition is good",Toast.LENGTH_LONG).show();
                                            else if(cond.equals("2"))
                                                Toast.makeText(getActivity(),"Your heart condition is fine",Toast.LENGTH_LONG).show();
                                            else if(cond.equals("3"))
                                                Toast.makeText(getActivity(),"Your heart condition is  bad",Toast.LENGTH_LONG).show();
                                            else if(cond.equals("4"))
                                                Toast.makeText(getActivity(),"Your heart condition is very bad",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(),"Some error occurred",Toast.LENGTH_SHORT).show();
                                        }
                                        d.dismiss();
                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(getActivity(),"Some error occurred",Toast.LENGTH_SHORT).show();
                                        Log.e("heart exc",e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("heart err",error.getMessage());
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> map=new HashMap<>();
                                    map.put("age",new AppSharedPreference(getActivity()).getAge());
                                    map.put("gen",new AppSharedPreference(getActivity()).getGender());
                                    map.put("bp",bp);
                                    map.put("chol",chol);
                                    map.put("sugar",bs);
                                    return map;
                                }
                            };
                            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                        }
                    }
                });
                d.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final DataPreference dataPreference = new DataPreference(getActivity());

        switch1.setChecked(dataPreference.isTrackingON());

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataPreference.setTrackingON(switch1.isChecked());
            }
        });


//        mealBreakfast.setText(dataPreference.getBreakFast() == null ? "NOT SET" : dataPreference.getBreakFast());
//        mealBreakfastCal.setText(dataPreference.getBreakFastCarbs() + " (cal)");
//
//        mealLunch.setText(dataPreference.getLunch() == null ? "NOT SET" : dataPreference.getLunch());
//        mealLunchCal.setText(dataPreference.getLunchCarbs() + " (cal)");

//        mealDinner.setText(dataPreference.getDinner() == null ? "NOT SET" : dataPreference.getDinner());
//        mealDinnerCal.setText(dataPreference.getDinnerCarbs() + " (cal)");

        ArcProgress arcProgress=(ArcProgress)view.findViewById(R.id.arc_progress1);
        arcProgress.setMax((int)(dataPreference.getKilometersGoal()));
        arcProgress.setProgress((int)((dataPreference.getTodaysWalkingValue()*0.0013)+(dataPreference.getTodaysRunningValue()*0.0106)));
        arcProgress.setBottomText(""+(int)(dataPreference.getKilometersGoal()));

        ArcProgress arcProgress2=(ArcProgress)view.findViewById(R.id.arc_progress2);
        arcProgress2.setMax((int)(dataPreference.getStepsGoal()));
        arcProgress2.setProgress((int)(1250*((dataPreference.getTodaysWalkingValue()*0.0013)+(dataPreference.getTodaysRunningValue()*0.0106))));
        arcProgress2.setBottomText(""+(int)(dataPreference.getStepsGoal()));

        walkingValue.setText(AppConstants.getWalking(dataPreference.getTodaysWalkingValue()) + "");
        runningValue.setText(AppConstants.getRunningValue(new DataPreference(getActivity()).showInKiloMeter(), dataPreference.getTodaysRunningValue()) + "");

        if (new DataPreference(getActivity()).showInKiloMeter()) {
            milesKeyValue.setText("(Kilometers)\nRUNNING");
        } else {
            milesKeyValue.setText("(Miles)\nRUNNING");
        }

        calarieCount.setText("Total Consumed: "
                + (dataPreference.getDinnerCarbs() + dataPreference.getLunchCarbs() + dataPreference.getBreakFastCarbs())
                + "kCal");

        long value = dataPreference.getDinnerCarbs() + dataPreference.getLunchCarbs() + dataPreference.getBreakFastCarbs();

        burntCalarieValue(value, dataPreference.getTodaysRunningValue(), dataPreference.getTodaysWalkingValue());
    }

    private void burntCalarieValue(long calarieValue, long todaysRunningValue, long todaysWalkingValue) {
        AppSharedPreference dataPreference = new AppSharedPreference(getActivity());

        int age = Integer.parseInt(dataPreference.getAge());
        int weight = Integer.parseInt(dataPreference.getWeight());
        double height = Double.parseDouble(dataPreference.getHeight());
        double valueCalarie = 0;

        double totalWalkBurn=0,totalRunBurn=0;
        totalWalkBurn= weight*0.30*todaysWalkingValue*0.0013*0.621371;
        totalRunBurn= weight* 0.63*todaysRunningValue*0.0106*0.621371;


        burntCalarieCount.setText("Total Burned: " + ((totalRunBurn+totalWalkBurn)/1000)+" kCal");
    }


    @Override
    public String getFragmentTitle() {
        return "Home";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.cardBreakfast, R.id.cardLunch, R.id.cardDinner})
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), SelectFoodDialogActivity.class);

//        Toast.makeText(getActivity(),""+new Date(System.currentTimeMillis()).getHours(),Toast.LENGTH_SHORT).show();

        if(new Date(System.currentTimeMillis()).getHours()< Integer.parseInt(new DataPreference(getActivity()).getBreakfastTime()))
            intent.putExtra("meal", "BREAKFAST");
        else if(new Date(System.currentTimeMillis()).getHours()< Integer.parseInt(new DataPreference(getActivity()).getLunchTime()))
            intent.putExtra("meal", "LUNCH");
        else if(new Date(System.currentTimeMillis()).getHours()< Integer.parseInt(new DataPreference(getActivity()).getDinnerTime()))
            intent.putExtra("meal", "DINNER");
        else
            intent.putExtra("meal", "DINNER");
        startActivityForResult(intent, 22);
    }

    @OnClick(R.id.viewMapButton)
    public void onClick() {
        startActivity(new Intent(getActivity(), MapActivity.class));
    }
}
