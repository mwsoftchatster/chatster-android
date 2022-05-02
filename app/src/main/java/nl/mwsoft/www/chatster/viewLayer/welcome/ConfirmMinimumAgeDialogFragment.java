package nl.mwsoft.www.chatster.viewLayer.welcome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;

import nl.mwsoft.www.chatster.R;

public class ConfirmMinimumAgeDialogFragment extends DialogFragment {

    private WelcomeActivity welcomeActivity;

    public static ConfirmMinimumAgeDialogFragment newInstance() {
        ConfirmMinimumAgeDialogFragment fragment = new ConfirmMinimumAgeDialogFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater.
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because its going in the dialog layout.
        View view = inflater.inflate(R.layout.confirm_age_dialog, null);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity.
                welcomeActivity.minimumAgeConfirmed();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity.
            }
        });

        builder.setView(view);

        // Create the AlertDialog.
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        welcomeActivity = (WelcomeActivity) context;
    }
}