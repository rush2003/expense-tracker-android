package com.example.expensetracker.fragments;

import android.graphics.Color;
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
import com.example.expensetracker.R;
import com.example.expensetracker.database.CategoryTotal;
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
    private LinearLayout categoryList;

    private int[] colors = {
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#FFD93D"),
            Color.parseColor("#6BCB77"),
            Color.parseColor("#4D96FF"),
            Color.parseColor("#FF922B"),
            Color.parseColor("#CC5DE8")
    };

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
        categoryList = view.findViewById(R.id.category_list);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        viewModel.getCategoryTotals().observe(getViewLifecycleOwner(), categoryTotals -> {
            if (categoryTotals == null || categoryTotals.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
                categoryList.setVisibility(View.GONE);
            } else {
                tvNoData.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                categoryList.setVisibility(View.VISIBLE);
                setupPieChart(categoryTotals);
                buildCategoryList(categoryTotals);
            }
        });
    }

    private void setupPieChart(List<CategoryTotal> categoryTotals) {
        List<PieEntry> entries = new ArrayList<>();
        for (CategoryTotal ct : categoryTotals) {
            entries.add(new PieEntry((float) ct.total, ct.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(13f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(4f);
        dataSet.setSelectionShift(8f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.parseColor("#6200EE"));
        pieChart.setHoleRadius(52f);
        pieChart.setTransparentCircleRadius(57f);
        pieChart.setTransparentCircleColor(Color.parseColor("#6200EE"));
        pieChart.setTransparentCircleAlpha(80);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void buildCategoryList(List<CategoryTotal> categoryTotals) {
        categoryList.removeAllViews();

        float total = 0;
        for (CategoryTotal ct : categoryTotals) total += ct.total;

        for (int i = 0; i < categoryTotals.size(); i++) {
            CategoryTotal ct = categoryTotals.get(i);
            int color = colors[i % colors.length];

            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 20);
            row.setLayoutParams(rowParams);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Color dot
            View dot = new View(getContext());
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(28, 28);
            dotParams.setMargins(0, 0, 20, 0);
            dot.setLayoutParams(dotParams);
            android.graphics.drawable.GradientDrawable dotBg = new android.graphics.drawable.GradientDrawable();
            dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            dotBg.setColor(color);
            dot.setBackground(dotBg);

            // Category name
            TextView tvName = new TextView(getContext());
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(ct.category);
            tvName.setTextColor(Color.WHITE);
            tvName.setTextSize(15f);

            // Percentage
            TextView tvPercent = new TextView(getContext());
            int percent = Math.round((float)(ct.total / total) * 100);
            tvPercent.setText(percent + "%");
            tvPercent.setTextColor(Color.parseColor("#FFFFFFAA"));
            tvPercent.setTextSize(13f);
            LinearLayout.LayoutParams percentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            percentParams.setMargins(0, 0, 20, 0);
            tvPercent.setLayoutParams(percentParams);

            // Amount
            TextView tvAmount = new TextView(getContext());
            tvAmount.setText("₹" + String.format("%.0f", ct.total));
            tvAmount.setTextColor(Color.WHITE);
            tvAmount.setTextSize(15f);
            tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);

            row.addView(dot);
            row.addView(tvName);
            row.addView(tvPercent);
            row.addView(tvAmount);
            categoryList.addView(row);
        }
    }
}