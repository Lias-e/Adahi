package com.android.adahi.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.adahi.R;
import com.android.adahi.databinding.ItemSheepBreedAvailabilityBinding;
import com.android.adahi.models.SheepBreedStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that renders one card per sheep breed and highlights its availability.
 */
public class SheepBreedAvailabilityAdapter extends RecyclerView.Adapter<SheepBreedAvailabilityAdapter.ViewHolder> {

    private final Context context;
    private final List<SheepBreedStatus> items = new ArrayList<>();

    public SheepBreedAvailabilityAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<SheepBreedStatus> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSheepBreedAvailabilityBinding binding = ItemSheepBreedAvailabilityBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int resolveStatusColor(boolean available) {
        return context.getColor(available ? R.color.adahi_primary_container : R.color.adahi_error_container);
    }

    private int resolveStatusTextColor(boolean available) {
        return context.getColor(available ? R.color.adahi_on_primary_container : R.color.adahi_on_error_container);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSheepBreedAvailabilityBinding binding;

        ViewHolder(ItemSheepBreedAvailabilityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SheepBreedStatus status) {
            binding.breedNameTextView.setText(status.getBreedName());
            binding.availabilityChip.setText(status.getStatusText());
            binding.availabilityChip.setChipBackgroundColor(ColorStateList.valueOf(resolveStatusColor(status.isAvailable())));
            binding.availabilityChip.setTextColor(resolveStatusTextColor(status.isAvailable()));
            binding.breedDetailsTextView.setText(status.getDetails());
            binding.getRoot().setCardBackgroundColor(context.getColor(status.isAvailable() ? R.color.adahi_surface_container_lowest : R.color.adahi_surface_container_low));
        }
    }
}