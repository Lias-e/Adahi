package com.android.adahi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.adahi.R;
import com.android.adahi.models.Order;
import com.android.adahi.utils.AnimalUiUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final Context context;
    private final List<Order> orders = new ArrayList<>();
    private final OnOrderClickListener onOrderClickListener;

    public OrderTrackingAdapter(Context context, OnOrderClickListener onOrderClickListener) {
        this.context = context;
        this.onOrderClickListener = onOrderClickListener;
    }

    public void setOrders(List<Order> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_tracking, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView animalNameTextView;
        private final TextView orderPlaceTextView;
        private final TextView orderPriceTextView;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTextView = itemView.findViewById(R.id.animalNameTextView);
            orderPlaceTextView = itemView.findViewById(R.id.orderPlaceTextView);
            orderPriceTextView = itemView.findViewById(R.id.orderPriceTextView);
        }

        void bind(Order order) {
            String animalName = "-";
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                animalName = safeValue(order.getItems().get(0).getAnimalName());
            }
            animalNameTextView.setText(animalName);

            orderPlaceTextView.setText(context.getString(R.string.wilaya_label, safeValue(order.getWilaya())));

            double price = order.getTotalPrice();
            if (price <= 0 && order.getItems() != null && !order.getItems().isEmpty()) {
                price = order.getItems().get(0).getSubtotal();
            }
            orderPriceTextView.setText(AnimalUiUtils.formatPrice(price));

            itemView.setOnClickListener(v -> {
                if (onOrderClickListener != null) {
                    onOrderClickListener.onOrderClick(order);
                }
            });
        }

        private String safeValue(String value) {
            return value == null || value.trim().isEmpty() ? "-" : value;
        }
    }
}
