package com.example.expensetracker.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.expensetracker.R;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private ExpenseViewModel viewModel;
    private PieChart pieChart;
    private TextView tvNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.pie_chart);
        tvNoData = view.findViewById(R.id.tv_no_data);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        viewModel.getCategoryTotals().observe(getViewLifecycleOwner(), categoryTotals -> {
            if (categoryTotals == null || categoryTotals.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
            } else {
                tvNoData.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                setupPieChart(categoryTotals);
            }
        });
    }

    private void setupPieChart(List<com.example.expensetracker.database.CategoryTotal> categoryTotals) {
        List<PieEntry> entries = new ArrayList<>();

        for (com.example.expensetracker.database.CategoryTotal ct : categoryTotals) {
            entries.add(new PieEntry((float) ct.total, ct.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(
                Color.parseColor("#6200EE"),
                Color.parseColor("#03DAC5"),
                Color.parseColor("#E53935"),
                Color.parseColor("#43A047"),
                Color.parseColor("#FB8C00"),
                Color.parseColor("#1E88E5"),
                Color.parseColor("#8E24AA")
        );
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setCenterText("Spending\nBreakdown");
        pieChart.setCenterTextSize(14f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}