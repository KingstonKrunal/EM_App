package com.krunal_shah.emapp.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.krunal_shah.emapp.Fragments.BalanceFragment;
import com.krunal_shah.emapp.R;
import com.krunal_shah.emapp.TransactionDb.AppDatabase;
import com.krunal_shah.emapp.TransactionDb.AppExecutors;
import com.krunal_shah.emapp.TransactionDb.TransactionEntry;
import com.krunal_shah.emapp.TransactionDb.TransactionViewModel;
import com.krunal_shah.emapp.Utilities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddExpenseActivity.class.getSimpleName();
    private static AppDatabase appDatabase;
    TextInputEditText amountTextInputEditText;
    TextInputEditText descriptionTextInputEditText;
    TextInputLayout amountTextInputLayout;
    TextInputLayout descriptionTextInputLayout;
    TextView dateTextView;
    LinearLayout dateLinearLayout;
    Spinner categorySpinner;
    ArrayList<String> categories;
    Calendar myCalendar;
    String description;
    Date dateOfExpense;
    //These variables contain data which will be stored permanently on hitting save button
    int amount;
    String categoryOfExpense;       //This parameter is to decide category in a transaction
    String categoryOfTransaction;  //This parameter to decide whether it is income and expense
    //Variable to keep track from where it came to this activity
    String intentFrom;
    TransactionViewModel transactionViewModel;
    int transactionid;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amountTextInputEditText = findViewById(R.id.amountTextInputEditText);
        descriptionTextInputEditText = findViewById(R.id.descriptionTextInputEditText);
        amountTextInputLayout = findViewById(R.id.amountTextInputLayout);
        descriptionTextInputLayout = findViewById(R.id.descriptionTextInputLayout);
        dateTextView = findViewById(R.id.dateTextView);
        dateLinearLayout = findViewById(R.id.dateLinerLayout);
        categorySpinner = findViewById(R.id.categorySpinner);

        appDatabase = AppDatabase.getInstance(getApplicationContext());


        transactionViewModel = ViewModelProviders.of(this)
                .get(TransactionViewModel.class);

        categories = new ArrayList<>();

        myCalendar = Calendar.getInstance();
        setDateToTextView();

        //First task here is to determine from where this activity is launched from the 4 possibilities

        Intent intent = getIntent();

        intentFrom = intent.getStringExtra("from");

        if (intentFrom.equals(Constants.addIncomeString)) {
            categoryOfTransaction = Constants.incomeCategory;
            setTitle("Add Income");
            categories.add("Income");
            categorySpinner.setClickable(false);
            categorySpinner.setEnabled(false);
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_list_item_1, categories));

        } else if (intentFrom.equals(Constants.addExpenseString)) {
            categoryOfTransaction = Constants.expenseCategory;
            setTitle("Add Expense");
            categories.add("Food");
            categories.add("Travel");
            categories.add("Clothes");
            categories.add("Movies");
            categories.add("Health");
            categories.add("Grocery");
            categories.add("Other");
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this,
                    android.R.layout.simple_list_item_1, categories));

        } else if (intentFrom.equals(Constants.editIncomeString)) {
            setTitle("Edit Income");

            amountTextInputEditText.setText(String.valueOf(intent.getIntExtra("amount", 0)));
            amountTextInputEditText.setSelection(amountTextInputEditText.getText().length());
            descriptionTextInputEditText.setText(intent.getStringExtra("description"));
            descriptionTextInputEditText.setSelection(descriptionTextInputEditText.getText().length());
            transactionid = intent.getIntExtra("id", -1);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = sdf.parse(intent.getStringExtra("date"));
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTextView.setText(intent.getStringExtra("date"));

            categoryOfTransaction = Constants.incomeCategory;
            categories.add("Income");
            categorySpinner.setClickable(false);
            categorySpinner.setEnabled(false);
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_list_item_1, categories));

        } else if (intentFrom.equals(Constants.editExpenseString)) {
            categoryOfTransaction = Constants.expenseCategory;
            setTitle("Edit Expense");
            amountTextInputEditText.setText(String.valueOf(intent.getIntExtra("amount", 0)));
            amountTextInputEditText.setSelection(amountTextInputEditText.getText().length());
            descriptionTextInputEditText.setText(intent.getStringExtra("description"));
            descriptionTextInputEditText.setSelection(descriptionTextInputEditText.getText().length());
            dateTextView.setText(intent.getStringExtra("date"));
            transactionid = intent.getIntExtra("id", -1);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = sdf.parse(intent.getStringExtra("date"));
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            categories.add("Food");
            categories.add("Travel");
            categories.add("Clothes");
            categories.add("Movies");
            categories.add("Health");
            categories.add("Grocery");
            categories.add("Other");
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_list_item_1, categories));
            categorySpinner.setSelection(categories.indexOf(intent.getStringExtra("category")));
        }

        dateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

    }

    public void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateToTextView();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
                dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

        //In this method not able to set max date
        /*new DatePickerDialog(AddExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateToTextView();
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();*/

        //This methode requires android n
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(AddExpenseActivity.this);

            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    setDateToTextView();
                }
            });

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }*/
    }

    public void setDateToTextView() {
        Date date = myCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateToBeSet = sdf.format(date);
        dateTextView.setText(dateToBeSet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_expense_activty_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        BalanceFragment balanceFragment = new BalanceFragment();
//        int balance = balanceFragment.getBal();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.saveButton:
//                if (amount > balance){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(AddExpenseActivity.this);
//                    builder.setTitle("Alert!");
//                    builder.setMessage("Expense exceeded the amount of balance!");
//                    builder.setCancelable(false);
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }

                // COMPLETED: 10-09-2018 //1.Retrieve and Save data to database and also update the recycler view

                if (amountTextInputEditText.getText().toString().isEmpty()
                        || descriptionTextInputEditText.getText().toString().isEmpty()) {

                    if (amountTextInputEditText.getText().toString().isEmpty())
                        amountTextInputEditText.setError("Amount cannot be empty");
                    if (descriptionTextInputEditText.getText().toString().isEmpty())
                        descriptionTextInputEditText.setError("Please write some description");

                } else {
                    amount = Integer.parseInt(amountTextInputEditText.getText().toString());
                    description = descriptionTextInputEditText.getText().toString();
                    dateOfExpense = myCalendar.getTime();

                    if (intentFrom.equals(Constants.addIncomeString)
                            || intentFrom.equals(Constants.editIncomeString))
                        categoryOfExpense = "Income";
                    else
                        categoryOfExpense = categories.get(categorySpinner.getSelectedItemPosition());

                    final TransactionEntry mTransactionEntry = new TransactionEntry(amount,
                            categoryOfExpense,
                            description,
                            dateOfExpense,
                            categoryOfTransaction
                    );

                    if (intentFrom.equals(Constants.addIncomeString) || intentFrom.equals(Constants.addExpenseString)) {

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                appDatabase.transactionDao().insertExpense(mTransactionEntry);
                            }
                        });

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        Snackbar.make(getCurrentFocus(), "Transaction Added", Snackbar.LENGTH_LONG).show();
                    } else {
                        mTransactionEntry.setId(transactionid);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                appDatabase.transactionDao().updateExpenseDetails(mTransactionEntry);

                            }
                        });

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        Snackbar.make(getCurrentFocus(), "Transaction Updated", Snackbar.LENGTH_LONG).show();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

                break;
        }

        return true;
    }
}