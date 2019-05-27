package com.nikvay.doctorapplication.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.doctorapplication.MainActivity;
import com.nikvay.doctorapplication.R;
import com.nikvay.doctorapplication.apicallcommon.ApiClient;
import com.nikvay.doctorapplication.apicallcommon.ApiInterface;
import com.nikvay.doctorapplication.model.DoctorModel;
import com.nikvay.doctorapplication.model.PatientModel;
import com.nikvay.doctorapplication.model.SuccessModel;
import com.nikvay.doctorapplication.utils.ErrorMessageDialog;
import com.nikvay.doctorapplication.utils.NetworkUtils;
import com.nikvay.doctorapplication.utils.SharedUtils;
import com.nikvay.doctorapplication.view.activity.LoginActivity;
import com.nikvay.doctorapplication.view.activity.NewPatientActivity;
import com.nikvay.doctorapplication.view.activity.ServiceListActivity;
import com.nikvay.doctorapplication.view.adapter.PatientAdapter;
import com.nikvay.doctorapplication.view.adapter.ServiceListAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PatientFragment extends Fragment {

    Context mContext;
    private RecyclerView recyclerPatientList;
    ArrayList<PatientModel> patientModelArrayList=new ArrayList<>();
    private PatientAdapter patientAdapter;
    private FloatingActionButton fabAddPatient;
    ArrayList<DoctorModel> doctorModelArrayList = new ArrayList<>();
    private ApiInterface apiInterface;
    private ErrorMessageDialog errorMessageDialog;
    private String doctor_id,TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient, container, false);
        mContext = getActivity();

        find_All_IDs(view);
        events();
        return view;
    }

    private void find_All_IDs(View view) {
        recyclerPatientList=view.findViewById(R.id.recyclerPatientList);
        fabAddPatient=view.findViewById(R.id.fabAddPatient);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        errorMessageDialog= new ErrorMessageDialog(mContext);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        recyclerPatientList.setLayoutManager(linearLayoutManager);

        doctorModelArrayList= SharedUtils.getUserDetails(mContext);
        doctor_id=doctorModelArrayList.get(0).getDoctor_id();


        if (NetworkUtils.isNetworkAvailable(mContext))
            callListPatient(doctor_id);
        else
            NetworkUtils.isNetworkNotAvailable(mContext);

    }

    private void callListPatient(String doctor_id) {

        Call<SuccessModel> call = apiInterface.patientList(doctor_id);


        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModel = response.body();

                        String message = null, code = null;
                        if (successModel != null) {
                            message = successModel.getMsg();
                            code = successModel.getError_code();


                            if (code.equalsIgnoreCase("1")) {

                                patientModelArrayList=successModel.getPatientModelArrayList();

                                if(doctorModelArrayList.size()!=0) {

                                    patientAdapter=new PatientAdapter(mContext,patientModelArrayList);
                                    recyclerPatientList.setAdapter(patientAdapter);
                                    recyclerPatientList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                                    recyclerPatientList.setHasFixedSize(true);
                                }
                                else
                                {
                                    errorMessageDialog.showDialog("List Not found");
                                }

                            } else {
                                errorMessageDialog.showDialog("List Not found");
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                errorMessageDialog.showDialog(t.getMessage());
            }
        });
    }

    private void events() {
        fabAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(mContext, "Add Patient", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext, NewPatientActivity.class);
                startActivity(intent);

            }
        });
    }


}
