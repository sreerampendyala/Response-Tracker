package com.example.util;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.responsecounter.R;
import com.example.util.Interfaces.MyStatListener;
import com.squareup.picasso.Picasso;

public class ReportLayoutAdapter extends RecyclerView.Adapter<ReportLayoutAdapter.ViewHolder> {
  Context context;
  private String[] imageNames;
  private Uri myuri;

  public ReportLayoutAdapter(String[] imageNames) {
    this.imageNames = imageNames;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_layout_adapter, parent,false);
    ReportLayoutAdapter.ViewHolder viewHolder = new ReportLayoutAdapter.ViewHolder(view);
    context = parent.getContext();
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    String image_id = imageNames[position];
    holder.textView.setText(image_id);
    new DatabaseConnector().getReportImage(image_id, new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {
        Uri uri = Uri.parse(String.valueOf(obj));
        if (isSuccess) {
//          myuri = Uri.parse(String.valueOf(obj));
//            holder.imgView.setImageURI(uri);
          Picasso.get().load(uri).placeholder(R.drawable.user_picture_24dp)
              .fit()
              .into(holder.imgView);
        }
      }

      @Override
      public void onFailure(String errMessage) {
        //
      }

    });
//    holder.imgView.setImageURI('');
  }

  @Override
  public int getItemCount() {
    return imageNames.length;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView imgView;
    TextView textView;

    public ViewHolder(View itemView)
    {
      super(itemView);
      imgView = itemView.findViewById(R.id.report_layout_adapter_imageView);
      textView = itemView.findViewById(R.id.report_layout_adapter_textView);
    }
  }

}
