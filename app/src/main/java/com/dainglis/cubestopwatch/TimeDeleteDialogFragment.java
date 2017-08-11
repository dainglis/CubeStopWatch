package com.dainglis.cubestopwatch;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by David Inglis on 3/6/2017.
 * Simple delete dialog fragment for RecordsActivity time list.
 */

public class TimeDeleteDialogFragment extends DialogFragment {

    public interface  TimeDeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    TimeDeleteDialogListener tdListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            tdListener = (TimeDeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement TimeDeleteDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_ask)
                .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tdListener.onDialogPositiveClick(TimeDeleteDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tdListener.onDialogNegativeClick(TimeDeleteDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
