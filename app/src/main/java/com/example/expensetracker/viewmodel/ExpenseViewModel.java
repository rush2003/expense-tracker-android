package com.example.expensetracker.viewmodel;

import com.example.expensetracker.database.CategoryTotal;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.expensetracker.database.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final LiveData<List<CategoryTotal>> categoryTotals;
    private final ExpenseRepository repository;
    private final LiveData<List<Expense>> allExpenses;
    private final LiveData<Double> totalAmount;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        allExpenses = repository.getAllExpenses();
        totalAmount = repository.getTotalAmount();
        categoryTotals = repository.getCategoryTotals();
    }

    public void insert(Expense expense) {
        repository.insert(expense);
    }

    public void delete(Expense expense) {
        repository.delete(expense);
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<List<CategoryTotal>> getCategoryTotals() {
        return categoryTotals;
    }

}