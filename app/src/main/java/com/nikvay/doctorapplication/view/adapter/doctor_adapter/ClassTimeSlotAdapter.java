package com.nikvay.doctorapplication.view.adapter.doctor_adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.doctorapplication.R;
import com.nikvay.doctorapplication.interfaceutils.SelectTimeSlotInterface;
import com.nikvay.doctorapplication.model.AppoinmentListModel;
import com.nikvay.doctorapplication.model.SelectDateTimeModel;
import com.nikvay.doctorapplication.model.ServiceModel;
import com.nikvay.doctorapplication.utils.ErrorMessageDialog;
import com.nikvay.doctorapplication.utils.StaticContent;
import com.nikvay.doctorapplication.view.activity.doctor_activity.AppointmentDetailsActivity;
import com.nikvay.doctorapplication.view.activity.doctor_activity.PatientActivity;

import java.util.ArrayList;

public class ClassTimeSlotAdapter extends RecyclerView.Adapter<ClassTimeSlotAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<SelectDateTimeModel> selectDateTimeModelArrayList;
    private ServiceModel serviceModel;
    private String date;
    private ErrorMessageDialog errorMessageDialog;
    private String reschedule;
    private AppoinmentListModel appoinmentListModel;
    private Boolean isDialog = false;
    private SelectTimeSlotInterface selectTimeSlotInterface;


    public ClassTimeSlotAdapter(Context context, ArrayList<SelectDateTimeModel> selectDateTimeModelArrayList, String date) {
        this.mContext = context;
        this.selectDateTimeModelArrayList = selectDateTimeModelArrayList;
        this.date = date;
        errorMessageDialog = new ErrorMessageDialog(mContext);

    }


    @NonNull
    @Override
    public ClassTimeSlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_time_adapter, parent, false);
        return new ClassTimeSlotAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassTimeSlotAdapter.MyViewHolder holder, final int position) {
        final SelectDateTimeModel selectDateTimeModel = selectDateTimeModelArrayList.get(position);
        
        holder.textTime.setText(selectDateTimeModel.getTime());

        if (selectDateTimeModel.getStatus().equals("1")) {
            holder.cardViewTime.setBackgroundColor(mContext.getResources().getColor(R.color.app_color));
        } else {
            holder.cardViewTime.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


        holder.cardViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectDateTimeModel.getStatus().equals("1")) {
                    errorMessageDialog.showDialog("This Slot Is Already Booked");
                } else {
                  /*  Intent intent = new Intent(mContext, PatientActivity.class);
                    intent.putExtra(StaticContent.IntentKey.APPOINTMENT, StaticContent.IntentValue.APPOINTMENT);
                    intent.putExtra(StaticContent.IntentKey.SERVICE_DETAIL, serviceModel);
                    intent.putExtra(StaticContent.IntentKey.DATE, date);
                    intent.putExtra(StaticContent.IntentKey.TIME, selectDateTimeModel.getTime());
                    mContext.startActivity(intent);*/
                    Toast.makeText(mContext, "Select Class Schedule", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return selectDateTimeModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textTime;
        private CardView cardViewTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.textTime);
            cardViewTime = itemView.findViewById(R.id.cardViewTime);
        }
    }

}
