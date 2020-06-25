package com.example.proximitynotifier;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private ArrayList<ReminderObject> reminderObjects;
    private Context context;



    public MyAdapter(Context context,ArrayList<ReminderObject> reminderObjects) {
        this.context=context;
        this.reminderObjects = reminderObjects;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ReminderObject m=reminderObjects.get(position);
        holder.title_textview.setText(m.getTitle_rem());
        holder.time_textview.setText(m.getTime_rem());
        holder.date_textview.setText(m.getDate_rem());
        holder.address_textview.setText(m.getPlace_rem());
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color1 = generator.getRandomColor();
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .round();
        TextDrawable ic1 = builder.build(m.getTitle_rem().charAt(0)+"", color1);
        holder.imageView.setImageDrawable(ic1);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AddReminderActivity.class);
                intent.putExtra("Position",position);
                ReminderObject m=reminderObjects.get(position);
                intent.putExtra("Title",m.getTitle_rem());
                intent.putExtra("Details",m.getDetails_rem());
                intent.putExtra("Place",m.getPlace_rem());
                intent.putExtra("Time",m.getTime_rem());
                intent.putExtra("Date",m.getDate_rem());
                intent.putExtra("Longitude",m.getLongitude_rem());
                intent.putExtra("Latitude",m.getLatitude_rem());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reminderObjects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView title_textview,date_textview,time_textview,address_textview;
        private ImageView imageView;
        private ConstraintLayout mainLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_textview=itemView.findViewById(R.id.title_textview);
            date_textview=itemView.findViewById(R.id.date_textview);
            imageView=itemView.findViewById(R.id.imageView);
            time_textview=itemView.findViewById(R.id.time_textview);
            address_textview=itemView.findViewById(R.id.address_textview);
            mainLayout=itemView.findViewById(R.id.mainLayout);

        }
    }
}
