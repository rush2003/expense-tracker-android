package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.example.expensetracker.database.Expense;

public class HistoryFragment extends Fragment {

    private ExpenseViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView tvNoExpenses;
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNoExpenses = view.findViewById(R.id.tv_no_expenses);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        viewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses == null || expenses.isEmpty()) {
                tvNoExpenses.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvNoExpenses.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setExpenses(expenses);
            }
        });

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Expense deletedExpense = adapter.getExpenseAt(viewHolder.getAdapterPosition());
                viewModel.delete(deletedExpense);

                // Show undo snackbar
                Snackbar.make(view, "Expense deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> viewModel.insert(deletedExpense))
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }
}