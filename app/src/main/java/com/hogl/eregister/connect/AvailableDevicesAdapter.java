package com.hogl.eregister.connect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hogl.eregister.R;

import java.util.ArrayList;

public class AvailableDevicesAdapter extends RecyclerView.Adapter<AvailableDevicesAdapter.ViewHolder>{

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define listener member variable
    private OnItemClickListener listener;

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public Button connectButton;

        public ViewHolder(final View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.device_name);
            connectButton = (Button) itemView.findViewById(R.id.connect_button);
        }

        public void raiseOnClick(){
            Log.d("recycler", "custom onclick running");
            // Triggers click upwards to the adapter on click
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position);
                }
            }
        }
        @Override
        public void onClick(View view) {
            //Dont use
        }
    }

    private ArrayList<WifiP2pDevice> aDevices;

    //Constructor
    public AvailableDevicesAdapter(ArrayList<WifiP2pDevice> devices){
        aDevices = devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View devicesView = inflater.inflate(R.layout.item_device, parent, false);

        ViewHolder viewHolder = new ViewHolder(devicesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WifiP2pDevice device = aDevices.get(position);


        TextView textView = holder.nameTextView;
        textView.setText(device.deviceName);
        Button button = holder.connectButton;
        button.setText("Connect");
        button.setEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.raiseOnClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        if(aDevices != null){
            return aDevices.size();
        }
        else{
            return 0;
        }

    }
}
