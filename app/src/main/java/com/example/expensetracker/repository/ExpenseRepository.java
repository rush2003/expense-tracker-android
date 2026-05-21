package com.example.expensetracker.repository;
import com.example.expensetracker.database.CategoryTotal;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.expensetracker.database.Expense;
import com.example.expensetracker.database.ExpenseDao;
import com.example.expensetracker.database.ExpenseDatabase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {

    private final ExpenseDao expenseDao;
    private final LiveData<List<Expense>> allExpenses;
    private final LiveData<Double> totalAmount;
    private final LiveData<List<CategoryTotal>> categoryTotals;
    private final ExecutorService executorService;

    public ExpenseRepository(Application application) {
        ExpenseDatabase database = ExpenseDatabase.getInstance(application);
        expenseDao = database.expenseDao();
        allExpenses = expenseDao.getAllExpenses();
        totalAmount = expenseDao.getTotalAmount();
        categoryTotals = expenseDao.getCategoryTotals();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Expense expense) {
        executorService.execute(() -> expenseDao.insert(expense));
    }

    public void delete(Expense expense) {
        executorService.execute(() -> expenseDao.delete(expense));
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