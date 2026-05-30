package com.android.adahi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.android.adahi.R;
import com.android.adahi.activities.AnimalDetailActivity;
import com.android.adahi.databinding.ItemAnimalBinding;
import com.android.adahi.models.Animal;
import com.android.adahi.utils.AnimalUiUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying the list of available Adahi animals.
 * Handles animal display and click events to navigate to order form.
 */
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animals;
    private Context context;
    private OnAnimalClickListener listener;

    /**
     * Interface for handling animal item click events
     */
    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    /**
     * Constructor for AnimalAdapter
     */
    public AnimalAdapter(Context context, OnAnimalClickListener listener) {
        this.context = context;
        this.animals = new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Set animals list and notify changes
     */
    public void setAnimals(List<Animal> animalList) {
        this.animals = animalList;
        notifyDataSetChanged();
    }

    /**
     * Add single animal to list
     */
    public void addAnimal(Animal animal) {
        this.animals.add(animal);
        notifyItemInserted(this.animals.size() - 1);
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use View Binding for better performance and type safety
        ItemAnimalBinding binding = ItemAnimalBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AnimalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        if (animals != null && position < animals.size()) {
            Animal animal = animals.get(position);
            holder.bind(animal);
        }
    }

    @Override
    public int getItemCount() {
        return animals == null ? 0 : animals.size();
    }

    /**
     * ViewHolder class for animal items
     */
    public class AnimalViewHolder extends RecyclerView.ViewHolder {
        private ItemAnimalBinding binding;

        public AnimalViewHolder(ItemAnimalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Bind animal data to views using localized strings
         */
        public void bind(Animal animal) {
            binding.animalName.setText(animal.getName());
            
            // Use localized string formats with correct resource IDs
            binding.animalType.setText(context.getString(R.string.animal_type_label, animal.getType()));
            binding.animalPrice.setText(AnimalUiUtils.formatPrice(animal.getPrice()));
            
            binding.animalDescription.setText(animal.getDescription());
            binding.animalWeightChip.setText(String.format(java.util.Locale.getDefault(), "%.0f كغ", animal.getWeight()));
            binding.animalLocationChip.setText(animal.getSalesPoint() == null || animal.getSalesPoint().isEmpty() ? "نقطة بيع موثقة" : animal.getSalesPoint());
            // Removed the obsolete animal badge binding

            Glide.with(binding.getRoot())
                    .load(AnimalUiUtils.resolveImageUrl(animal))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.animalImage);

            // Set click listener to navigate to order form
            Runnable openDetails = () -> {
                if (listener != null) {
                    listener.onAnimalClick(animal);
                }
                // Navigate to the product detail screen with animal details
                Intent intent = new Intent(context, AnimalDetailActivity.class);
                intent.putExtra("animal_id", animal.getId());
                intent.putExtra("animal_name", animal.getName());
                intent.putExtra("animal_type", animal.getType());
                intent.putExtra("animal_price", animal.getPrice());
                intent.putExtra("animal_weight", animal.getWeight());
                intent.putExtra("animal_description", animal.getDescription());
                intent.putExtra("animal_breed", animal.getBreed());
                intent.putExtra("animal_age", animal.getAge());
                intent.putExtra("animal_gender", animal.getGender());
                intent.putExtra("animal_health_status", animal.getHealthStatus());
                intent.putExtra("animal_sales_point", animal.getSalesPoint());
                context.startActivity(intent);
            };

            binding.getRoot().setOnClickListener(v -> openDetails.run());
            binding.animalActionButton.setOnClickListener(v -> openDetails.run());
        }
    }
}
