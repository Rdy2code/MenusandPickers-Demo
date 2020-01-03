/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.droidcafeinput;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This activity handles radio buttons for choosing a delivery method for an
 * order, a spinner for setting the label for a phone number, and EditText input
 * controls.
 */
public class OrderActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    //Variables for storing references to views in the UI
    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mDatePickerEditText;
    private EditText mTimePickerEditText;


    /**
     * Variable indicates whether user has touched any of the views in the interface. When set to
     * true, the UI will trigger an AlertDialog to warn the user about unsaved changes if the user
     * presses the back arrow before filling in the fields
     */

    private boolean mOrderStarted = false;

    //Declare and Initialize an OnTouchListener, which can then be attached to any desired view in the UI
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Actions taken when user touches into a view

            //User has pressed on a field, which indicates they may be starting an order
            mOrderStarted = true;

            //We do not want the touch listener to consume the event because we want to have it
            //available to other click handlers if necessary
            return false;
        }
    };


    /**
     * Sets the content view to activity_order, and gets the intent and its
     * data. Also creates an array adapter and layout for a spinner.
     *
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Initialize the views
        mNameEditText = findViewById(R.id.name_text);
        mAddressEditText = findViewById(R.id.address_text);
        mDatePickerEditText = findViewById(R.id.date);
        mTimePickerEditText = findViewById(R.id.time);

        //Attach the OnTouchListener
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mAddressEditText.setOnTouchListener(mOnTouchListener);

        mDatePickerEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerEditText.setKeyListener(null);
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getSupportFragmentManager(), getString(R.string.tag_date_picker));
            }
        });

        mTimePickerEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerEditText.setKeyListener(null);
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getSupportFragmentManager(), getString(R.string.tag_time_picker));
            }
        });

        // Get the intent and its data.
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.order_textview);
        textView.setText(message);

        // Create the spinner.
        Spinner spinner = findViewById(R.id.label_spinner);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        // Create an ArrayAdapter using the string array and default spinner
        // layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.labels_array,
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
    }

    /**
     * Checks which radio button was clicked and displays a toast message to
     * show the choice.
     *
     * @param view The radio button view.
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked.
        switch (view.getId()) {
            case R.id.sameday:
                if (checked)
                    // Same day service
                    displayToast(getString(
                            R.string.same_day_messenger_service));
                break;
            case R.id.nextday:
                if (checked)
                    // Next day delivery
                    displayToast(getString(
                            R.string.next_day_ground_delivery));
                break;
            case R.id.pickup:
                if (checked)
                    // Pick up
                    displayToast(getString(
                            R.string.pick_up));
                break;
            default:
                // Do nothing.
                break;
        }
    }

    /**
     * Displays the actual message in a toast message.
     *
     * @param message Message to display.
     */
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    // Interface callback for when any spinner item is selected.
    @Override
    public void onItemSelected(AdapterView<?> adapterView,
                               View view, int i, long l) {
        String spinnerLabel = adapterView.getItemAtPosition(i).toString();
        displayToast(spinnerLabel);
    }

    // Interface callback for when no spinner item is selected.
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing.
    }

    /**
     * This handles the back arrow from system bar only
     */
    @Override
    public void onBackPressed() {
        //Check if user has begun entering information
        if (!mOrderStarted) {
            super.onBackPressed();
            return;
        }

        showUnsavedChangesDialog();

    }

    private void showUnsavedChangesDialog() {
        //Otherwise, build the AlertDialog to warn the user about possible unsaved changes
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OrderActivity.this);
        //Set the dialog title and message (see Material Design Specs: https://material.io/components/dialogs/#anatomy)
        alertBuilder.setTitle(getString(R.string.alert_dialog_title));
        alertBuilder.setMessage(getString(R.string.alert_dialog_message));

        //Continue ordering
        alertBuilder.setPositiveButton(getString(R.string.alert_positive_button),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Dismiss the dialog and return to the MainActivity
        alertBuilder.setNegativeButton(getString(R.string.alert_negative_button),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        //Two ways to create and show dialog; I prefer one line of code to two
//        AlertDialog alertDialog = alertBuilder.create();
//        alertDialog.show();

        //Create and show the Dialog
        alertBuilder.show();
    }

    //This handles the navigation up from the arrow in the appbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mOrderStarted) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Called from DatePickerFragment passing in the DatePicker data
    public void processDatePickerResult (int year, int month, int day) {
        String yearText = Integer.toString(year);
        String monthText = Integer.toString(month+1);
        String dayText = Integer.toString(day);
        String date = monthText + "/" + dayText + "/" + yearText;
        mDatePickerEditText.setText(date);
    }

    //Called from TimePickerFragment
    public void processTimeResult (int hour, int minute, String amOrPm) {
        String time = String.format("%2d:%02d %2s", hour, minute, amOrPm);
        mTimePickerEditText.setText(time);
    }
}
