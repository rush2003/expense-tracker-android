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

    public LiveData<List<String>> getDistinctMonths() {
        return repository.getDistinctMonths();
    }

    public LiveData<List<Expense>> getExpensesByMonth(String month) {
        return repository.getExpensesByMonth(month);
    }

    public LiveData<Double> getTotalAmountByMonth(String month) {
        return repository.getTotalAmountByMonth(month);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsByMonth(String month) {
        return repository.getCategoryTotalsByMonth(month);
    }

}