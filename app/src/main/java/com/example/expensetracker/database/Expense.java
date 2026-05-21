package com.example.expensetracker.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private double amount;
    private String category;
    private String date;
    private String note;

    public Expense(String title, double amount, String category, String date, String note) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getNote() { return note; }

    // Setter for id only
    public void setId(int id) { this.id = id; }
}