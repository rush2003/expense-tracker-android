package com.example.expensetracker.database;

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

}