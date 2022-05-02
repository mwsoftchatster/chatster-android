package nl.mwsoft.www.chatster.viewLayer.chat;

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

public class SpyModeDialogFragment extends DialogFragment {

    private TextView tvSpyModeDialogQuestion;


    public interface SpyModeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    SpyModeDialogFragment.SpyModeDialogListener spyModeDialogListener;

    public static SpyModeDialogFragment newInstance(String question) {
        Bundle args = new Bundle();
        args.putString(ConstantRegistry.CHATSTER_SPY_MODE_QUESTION, question);

        SpyModeDialogFragment fragment = new SpyModeDialogFragment();
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
        View view = inflater.inflate(R.layout.go_in_spy_mode_dialog, null);

        tvSpyModeDialogQuestion = (TextView) view.findViewById(R.id.tvSpyModeDialogQuestion);
        // set value
        if(!getArguments().getString(ConstantRegistry.CHATSTER_SPY_MODE_QUESTION).equals(null)
                && !getArguments().getString(ConstantRegistry.CHATSTER_SPY_MODE_QUESTION).isEmpty()){
            tvSpyModeDialogQuestion.setText(getArguments().getString(ConstantRegistry.CHATSTER_SPY_MODE_QUESTION));
        }

        builder.setPositiveButton(R.string.go,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                spyModeDialogListener.onDialogPositiveClick(SpyModeDialogFragment.this);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                spyModeDialogListener.onDialogNegativeClick(SpyModeDialogFragment.this);
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
            spyModeDialogListener = (SpyModeDialogFragment.SpyModeDialogListener) activity;
        }
    }

}
