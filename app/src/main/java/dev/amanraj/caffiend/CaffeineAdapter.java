package dev.amanraj.caffiend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CaffeineAdapter extends RecyclerView.Adapter<CaffeineAdapter.ViewHolder> {
    private List<String[]> dataList;
    private Context context;

    public CaffeineAdapter(List<String[]> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, timeText;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amountText);
            timeText = itemView.findViewById(R.id.timeText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public CaffeineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CaffeineAdapter.ViewHolder holder, int position) {
        String[] entry = dataList.get(position);
        holder.amountText.setText("Amount: " + entry[0]);
        holder.timeText.setText("Time: " + entry[1]);

        holder.deleteButton.setOnClickListener(v -> {
            dataList.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            notifyItemRangeChanged(holder.getAdapterPosition(), dataList.size());
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
