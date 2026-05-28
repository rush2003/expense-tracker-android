package com.example.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.database.CategoryTotal;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthDetailActivity extends AppCompatActivity {

    private ExpenseViewModel viewModel;
    private PieChart pieChart;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private LinearLayout categoryList;
    private TextView tvTotal;
    private String selectedMonth;
    private boolean isCurrentMonth;

    private int[] colors = {
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#FFD93D"),
            Color.parseColor("#6BCB77"),
            Color.parseColor("#4D96FF"),
            Color.parseColor("#FF922B"),
            Color.parseColor("#CC5DE8")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_detail);

        // Status bar
        getWindow().setStatusBarColor(Color.parseColor("#6200EE"));

        selectedMonth = getIntent().getStringExtra("month");
        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        isCurrentMonth = selectedMonth.equals(currentMonth);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getMonthName(selectedMonth));
            getSupportActionBar().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(Color.parseColor("#6200EE")));
        }

        pieChart = findViewById(R.id.pie_chart);
        recyclerView = findViewById(R.id.recycler_view);
        categoryList = findViewById(R.id.category_list);
        tvTotal = findViewById(R.id.tv_total);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Observe total
        viewModel.getTotalAmountByMonth(selectedMonth).observe(this, total -> {
            if (total != null) {
                tvTotal.setText("₹" + String.format("%.2f", total));
            } else {
                tvTotal.setText("₹0.00");
            }
        });

        // Observe expenses
        viewModel.getExpensesByMonth(selectedMonth).observe(this, expenses -> {
            if (expenses != null) {
                adapter.setExpenses(expenses);
            }
        });

        // Observe category totals
        viewModel.getCategoryTotalsByMonth(selectedMonth).observe(this, categoryTotals -> {
            if (categoryTotals != null && !categoryTotals.isEmpty()) {
                setupPieChart(categoryTotals);
                buildCategoryList(categoryTotals);
            }
        });

        // Swipe to delete only for current month
        if (isCurrentMonth) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@androidx.annotation.NonNull RecyclerView rv,
                                      @androidx.annotation.NonNull RecyclerView.ViewHolder vh,
                                      @androidx.annotation.NonNull RecyclerView.ViewHolder t) {
                    return false;
                }

                @Override
                public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    com.example.expensetracker.database.Expense deleted =
                            adapter.getExpenseAt(viewHolder.getAdapterPosition());
                    viewModel.delete(deleted);
                    Snackbar.make(recyclerView, "Expense deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> viewModel.insert(deleted))
                            .show();
                }
            }).attachToRecyclerView(recyclerView);
        }
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

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 20);
            row.setLayoutParams(rowParams);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Color dot
            android.view.View dot = new android.view.View(this);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(28, 28);
            dotParams.setMargins(0, 0, 20, 0);
            dot.setLayoutParams(dotParams);
            android.graphics.drawable.GradientDrawable dotBg =
                    new android.graphics.drawable.GradientDrawable();
            dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            dotBg.setColor(color);
            dot.setBackground(dotBg);

            // Category name
            TextView tvName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(ct.category);
            tvName.setTextColor(Color.WHITE);
            tvName.setTextSize(15f);

            // Percentage
            TextView tvPercent = new TextView(this);
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
            TextView tvAmount = new TextView(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}