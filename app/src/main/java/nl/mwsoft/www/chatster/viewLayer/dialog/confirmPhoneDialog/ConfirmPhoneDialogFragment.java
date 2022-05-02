package nl.mwsoft.www.chatster.viewLayer.dialog.confirmPhoneDialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;

public class ConfirmPhoneDialogFragment extends DialogFragment {

    private TextView tvNumberConfirmPhoneDialog;


    public interface ConfirmPhoneDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String phoneToVerify);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    ConfirmPhoneDialogListener confirmPhoneDialogListener;

    public static ConfirmPhoneDialogFragment newInstance(String phoneToVerify) {

        Bundle args = new Bundle();
        args.putString(ConstantRegistry.CHATSTER_PHONE_TO_VERIFY, phoneToVerify);
        ConfirmPhoneDialogFragment fragment = new ConfirmPhoneDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.confirm_phone_dialog, null);

        tvNumberConfirmPhoneDialog = (TextView) view.findViewById(R.id.tvNumberConfirmPhoneDialog);
        // set value
        if(!getArguments().getString(ConstantRegistry.CHATSTER_PHONE_TO_VERIFY).equals(null)
                && !getArguments().getString(ConstantRegistry.CHATSTER_PHONE_TO_VERIFY).isEmpty()){
            tvNumberConfirmPhoneDialog.setText(getArguments().getString(ConstantRegistry.CHATSTER_PHONE_TO_VERIFY));
        }

        builder.setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        confirmPhoneDialogListener.onDialogPositiveClick(ConfirmPhoneDialogFragment.this,
                                tvNumberConfirmPhoneDialog.getText().toString());
                    }
                })
                .setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        confirmPhoneDialogListener.onDialogNegativeClick(ConfirmPhoneDialogFragment.this);
                    }
                });

        builder.setView(view);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            // Instantiate the NoticeDialogListener so we can send events to the host
            confirmPhoneDialogListener = (ConfirmPhoneDialogListener) activity;
        }
    }

}
