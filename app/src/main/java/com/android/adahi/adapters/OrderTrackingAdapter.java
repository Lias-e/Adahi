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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orders = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("ar"));

    public OrderTrackingAdapter(Context context) {
        this.context = context;
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
        private final TextView orderIdTextView;
        private final TextView animalNameTextView;
        private final TextView orderTypeTextView;
        private final TextView orderDateTextView;
        private final TextView orderFeeTextView;
        private final TextView orderStatusTextView;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            animalNameTextView = itemView.findViewById(R.id.animalNameTextView);
            orderTypeTextView = itemView.findViewById(R.id.orderTypeTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderFeeTextView = itemView.findViewById(R.id.orderFeeTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
        }

        void bind(Order order) {
            orderIdTextView.setText(context.getString(R.string.order_id_label, safeValue(order.getOrderId())));

            String animalName = "-";
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                animalName = safeValue(order.getItems().get(0).getAnimalName());
            }
            animalNameTextView.setText(animalName);

            String typeLabel = "reserve".equals(order.getOrderType())
                    ? context.getString(R.string.order_type_reserve)
                    : context.getString(R.string.order_type_buy);
            orderTypeTextView.setText(context.getString(R.string.order_type_label, typeLabel));

            orderDateTextView.setText(dateFormat.format(new Date(order.getOrderDate())));
            orderFeeTextView.setText(context.getString(R.string.fee_label, AnimalUiUtils.formatPrice(order.getFeeAmount())));
            orderStatusTextView.setText(context.getString(R.string.status_label, safeValue(order.getStatus())));
        }

        private String safeValue(String value) {
            return value == null || value.trim().isEmpty() ? "-" : value;
        }
    }
}
