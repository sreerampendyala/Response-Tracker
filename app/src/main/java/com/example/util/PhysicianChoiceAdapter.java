package com.example.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.responsecounter.R;
import com.example.util.Models.PhysicianChoiceModel;

import java.util.List;

public class PhysicianChoiceAdapter extends RecyclerView.Adapter<PhysicianChoiceAdapter.ViewHolder> {

    List<PhysicianChoiceModel> physicianChoiceList;
    Context context;

    public PhysicianChoiceAdapter(List<PhysicianChoiceModel> physicianChoiceList) {
        this.physicianChoiceList = physicianChoiceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.physician_choice,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PhysicianChoiceModel physicianChoiceModelObject = physicianChoiceList.get(position);

        holder.textTvShow.setText(physicianChoiceModelObject.getLable());
        holder.selectSwitch.setChecked(physicianChoiceModelObject.isValue());
        holder.selectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    physicianChoiceList.get(position).setValue(true);
                } else physicianChoiceList.get(position).setValue(false);

                EntityClass.getInstance().setPhysicianChoiceList(physicianChoiceList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return physicianChoiceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textTvShow;
        CardView cv;
        Switch selectSwitch;

        public ViewHolder(View itemView)
        {
            super(itemView);
            textTvShow = (TextView)itemView.findViewById(R.id.setupLine_textView_physicianChoice);
            cv = (CardView)itemView.findViewById(R.id.temp_setup_card);
            selectSwitch = (Switch)itemView.findViewById(R.id.switch_select_physicianChoice);
        }

    }


}
