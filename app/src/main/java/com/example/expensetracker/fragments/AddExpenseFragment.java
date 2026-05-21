package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.expensetracker.R;
import com.example.expensetracker.database.Expense;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    private ExpenseViewModel viewModel;
    private EditText etTitle, etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.et_title);
        etAmount = view.findViewById(R.id.et_amount);
        etNote = view.findViewById(R.id.et_note);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        btnSave = view.findViewById(R.id.btn_save);

        // Setup category spinner
        String[] categories = {"Food", "Transport", "Shopping", "Bills", "Health", "Entertainment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (title.isEmpty()) {
            etTitle.setError("Please enter a title");
            return;
        }
        if (amountStr.isEmpty()) {
            etAmount.setError("Please enter an amount");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Expense expense = new Expense(title, amount, category, date, note);
        viewModel.insert(expense);

        Toast.makeText(getContext(), "Expense saved!", Toast.LENGTH_SHORT).show();

        // Clear fields
        etTitle.setText("");
        etAmount.setText("");
        etNote.setText("");
    }
}