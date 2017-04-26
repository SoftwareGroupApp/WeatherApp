package com.hannahburzynski.weatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Class inherits from DialogFragment and is responsible for alerting
 * the user of a application error.
 */
public class AlertDialogFragment extends DialogFragment {

    /**
     * creates a dialog box to inform an error state
     * @param savedInstanceState the bundle from the saved state
     * @return a dialog, the error message
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Set the title and message for the dialog message
        builder.setTitle(R.string.error_title);
        builder.setMessage(R.string.error_message);
        // Use null for on click listener when you don't want to do anything when the button is tapped
        builder.setPositiveButton(R.string.error_positive_button_text, null);

        // Use builder to create the dialog object
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
