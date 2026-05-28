package com.example.expensetracker.database;

import com.example.expensetracker.database.CategoryTotal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT SUM(amount) FROM expenses")
    LiveData<Double> getTotalAmount();

    @Query("SELECT * FROM expenses WHERE category = :category")
    LiveData<List<Expense>> getExpensesByCategory(String category);

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    LiveData<List<CategoryTotal>> getCategoryTotals();

    @Query("SELECT strftime('%m-%Y', date('now')) as currentMonth")
    String getCurrentMonth();

    @Query("SELECT DISTINCT substr(date, 4, 7) as month FROM expenses ORDER BY substr(date, 7, 4) DESC, substr(date, 4, 2) DESC")
    LiveData<List<String>> getDistinctMonths();

    @Query("SELECT * FROM expenses WHERE substr(date, 4, 7) = :month ORDER BY id DESC")
    LiveData<List<Expense>> getExpensesByMonth(String month);

    @Query("SELECT SUM(amount) FROM expenses WHERE substr(date, 4, 7) = :month")
    LiveData<Double> getTotalAmountByMonth(String month);

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE substr(date, 4, 7) = :month GROUP BY category")
    LiveData<List<CategoryTotal>> getCategoryTotalsByMonth(String month);

}