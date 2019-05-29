package com.nikvay.doctorapplication.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nikvay.doctorapplication.R;
import com.nikvay.doctorapplication.apicallcommon.ApiClient;
import com.nikvay.doctorapplication.apicallcommon.ApiInterface;
import com.nikvay.doctorapplication.model.DoctorModel;
import com.nikvay.doctorapplication.model.PatientModel;
import com.nikvay.doctorapplication.model.ServiceModel;
import com.nikvay.doctorapplication.model.SuccessModel;
import com.nikvay.doctorapplication.utils.ErrorMessageDialog;
import com.nikvay.doctorapplication.utils.NetworkUtils;
import com.nikvay.doctorapplication.utils.SharedUtils;
import com.nikvay.doctorapplication.utils.StaticContent;
import com.nikvay.doctorapplication.utils.SuccessMessageDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAddServiceActivity extends AppCompatActivity {


    private TextView serviceTitle_name, textName, textDuration, textBuffer, textCost, textSave, textDescription;
    private String serviceTitle, name, Duration, BufferTime, Cost, Description, mTitle = "Add Customer", doctor_id, user_id, service_id;
    ImageView rel_img_back;
    private ErrorMessageDialog errorMessageDialog;
    private SuccessMessageDialog successMessageDialog;
    ApiInterface apiInterface;
    ServiceModel serviceModel;

    private ArrayList<DoctorModel> doctorModelArrayList = new ArrayList<>();
    String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_service);

        Find_All_ID();
        event();
    }

    private void Find_All_ID() {

        serviceTitle_name = findViewById(R.id.serviceTitle_name);
        textName = findViewById(R.id.textName);
        textDuration = findViewById(R.id.textDuration);
        textBuffer = findViewById(R.id.textBuffer);
        textSave = findViewById(R.id.textSave);
        textCost = findViewById(R.id.textCost);
        textDescription = findViewById(R.id.textDescription);
        rel_img_back = findViewById(R.id.rel_img_back);


        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        doctorModelArrayList = SharedUtils.getUserDetails(NewAddServiceActivity.this);
        doctor_id = doctorModelArrayList.get(0).getDoctor_id();
        user_id = doctorModelArrayList.get(0).getUser_id();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            serviceModel = (ServiceModel) bundle.getSerializable(StaticContent.IntentKey.SERVICE_DETAIL);
            mTitle = bundle.getString(StaticContent.IntentKey.ACTIVITY_TYPE);
            serviceTitle_name.setText(mTitle);
        }

        if (mTitle.equals(StaticContent.IntentValue.ACTIVITY_SERVICE_DETAILS)) {
            textName.setText(serviceModel.getS_name());
            textDuration.setText(serviceModel.getService_time());
            textCost.setText(serviceModel.getService_cost());
            textDescription.setText(serviceModel.getDescription());
            textBuffer.setText(serviceModel.getService_time());
            service_id = serviceModel.getService_id();
            textSave.setText(StaticContent.ButtonContent.UPDATE);
            //  disableFields();

        }

        errorMessageDialog = new ErrorMessageDialog(NewAddServiceActivity.this);
        successMessageDialog = new SuccessMessageDialog(NewAddServiceActivity.this);
    }


    private void event() {
        rel_img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doValidation()) {
                    if (textSave.getText().equals(StaticContent.ButtonContent.UPDATE)) {
                        if (NetworkUtils.isNetworkAvailable(NewAddServiceActivity.this)) {
                            callUpdateServiceList(doctor_id, name, Duration, BufferTime, Cost, Description);
                        } else
                            NetworkUtils.isNetworkNotAvailable(NewAddServiceActivity.this);
                    } else {
                        if (NetworkUtils.isNetworkAvailable(NewAddServiceActivity.this)) {
                            callAddServiceList(name, Duration, BufferTime, Cost, Description);
                        } else
                            NetworkUtils.isNetworkNotAvailable(NewAddServiceActivity.this);
                    }
                }
            }
        });
    }


    private boolean doValidation() {
        name = textName.getText().toString().trim();
        //  serviceTitle = serviceTitle_name.getText().toString().trim();
        Duration = textDuration.getText().toString().trim();
        BufferTime = textBuffer.getText().toString().trim();
        Cost = textCost.getText().toString().trim();
        Description = textDescription.getText().toString().trim();

        if (name.equalsIgnoreCase("")) {
            errorMessageDialog.showDialog("name  Can't Be Empty");
            return false;
        } else if (Duration.equalsIgnoreCase("")) {
            errorMessageDialog.showDialog("Duration Can't Be Empty");
            return false;
        } else if (Cost.equalsIgnoreCase("")) {
            errorMessageDialog.showDialog("Cost Can't Be Empty");
            return false;
        }

        return true;
    }

    private void callAddServiceList(String name, String duration, String bufferTime,
                                    String cost, String description) {
        Call<SuccessModel> call = apiInterface.addNewService(doctor_id, user_id, name, duration, bufferTime, cost, description);

        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {

                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>>>>>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel serviceModel = response.body();
                        String message = null, errorCode = null;

                        if (serviceModel != null) {
                            message = serviceModel.getMsg();
                            errorCode = serviceModel.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {
                                successMessageDialog.showDialog("Service Add succesfully ");
                                //finish();
                            } else if (errorCode.equalsIgnoreCase("3")) {
                                errorMessageDialog.showDialog("Service is Already Sucessfully ");
                            } else {
                                errorMessageDialog.showDialog("Parameter is missing");
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {

            }
        });
    }

    private void callUpdateServiceList(String doctor_id, String name, String duration, String bufferTime,
                                       String cost, String description) {
        Call<SuccessModel> call = apiInterface.updateServiceList(doctor_id, user_id, service_id, name, duration, bufferTime, cost, description);

        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {

                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>>>>>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel serviceModel = response.body();
                        String message = null, errorCode = null;

                        if (serviceModel != null) {
                            message = serviceModel.getMsg();
                            errorCode = serviceModel.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {
                                successMessageDialog.showDialog("Service Update succesfully ");
                                finish();
                            } else if (errorCode.equalsIgnoreCase("3")) {
                                errorMessageDialog.showDialog("Service is Already Sucessfully ");
                            } else {
                                errorMessageDialog.showDialog("Parameter is missing");
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {

            }
        });
    }
}
