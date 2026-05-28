package com.example.expensetracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.expensetracker.MonthDetailActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ExpenseViewModel viewModel;
    private TextView tvTotalAmount;
    private LinearLayout monthsContainer;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        monthsContainer = view.findViewById(R.id.months_container);
        emptyState = view.findViewById(R.id.empty_state);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Overall total
        viewModel.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                tvTotalAmount.setText("₹" + String.format("%.2f", total));
            } else {
                tvTotalAmount.setText("₹0.00");
            }
        });

        // Distinct months
        viewModel.getDistinctMonths().observe(getViewLifecycleOwner(), months -> {
            monthsContainer.removeAllViews();

            if (months == null || months.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                monthsContainer.setVisibility(View.GONE);
                return;
            }

            emptyState.setVisibility(View.GONE);
            monthsContainer.setVisibility(View.VISIBLE);

            String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

            for (String month : months) {
                View monthCard = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_month, monthsContainer, false);

                TextView tvMonthName = monthCard.findViewById(R.id.tv_month_name);
                TextView tvMonthTotal = monthCard.findViewById(R.id.tv_month_total);
                TextView tvMonthStatus = monthCard.findViewById(R.id.tv_month_status);

                tvMonthName.setText(getMonthName(month));

                if (month.equals(currentMonth)) {
                    tvMonthStatus.setText("Active");
                    tvMonthStatus.setTextColor(android.graphics.Color.parseColor("#4ECDC4"));
                } else {
                    tvMonthStatus.setText("Locked");
                    tvMonthStatus.setTextColor(android.graphics.Color.parseColor("#FFFFFFAA"));
                }

                viewModel.getTotalAmountByMonth(month).observe(getViewLifecycleOwner(), total -> {
                    if (total != null) {
                        tvMonthTotal.setText("₹" + String.format("%.2f", total));
                    } else {
                        tvMonthTotal.setText("₹0.00");
                    }
                });

                monthCard.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), MonthDetailActivity.class);
                    intent.putExtra("month", month);
                    startActivity(intent);
                });

                monthsContainer.addView(monthCard);
            }
        });
    }

    private String getMonthName(String monthYear) {
        try {
            String[] parts = monthYear.split("-");
            String[] months = {"January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};
            int monthIndex = Integer.parseInt(parts[0]) - 1;
            return months[monthIndex] + " " + parts[1];
        } catch (Exception e) {
            return monthYear;
        }
    }
}