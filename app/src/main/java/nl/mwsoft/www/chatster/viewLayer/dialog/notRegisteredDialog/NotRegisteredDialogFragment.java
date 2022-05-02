package nl.mwsoft.www.chatster.viewLayer.dialog.notRegisteredDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.chat.ChatActivity;

public class NotRegisteredDialogFragment extends DialogFragment {

    private TextView tvNotRegisteredExplain;
    private TextView tvNotRegisteredAsk;
    private ChatActivity chatActivity;

    public interface NotRegisteredDialogFragmentListener {
        public void onPositiveClick(DialogFragment dialog);
        public void onNegativeClick(DialogFragment dialog);
    }

    NotRegisteredDialogFragment.NotRegisteredDialogFragmentListener notRegisteredDialogFragmentListener;

    public static NotRegisteredDialogFragment newInstance(String contactName) {
        Bundle args = new Bundle();
        NotRegisteredDialogFragment fragment = new NotRegisteredDialogFragment();
        args.putString(ConstantRegistry.CHATSTER_CONTACT_NAME, contactName);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.not_registered_dialog, null);
        tvNotRegisteredExplain = (TextView) view.findViewById(R.id.tvNotRegisteredExplain);
        tvNotRegisteredAsk = (TextView) view.findViewById(R.id.tvNotRegisteredAsk);

        if (getArguments() != null){
            if(getArguments().getString(ConstantRegistry.CHATSTER_CONTACT_NAME) != null){
                if(!getArguments().getString(ConstantRegistry.CHATSTER_CONTACT_NAME).isEmpty()){
                    tvNotRegisteredExplain.setText(getString(R.string.not_registered_explain, getArguments().getString(ConstantRegistry.CHATSTER_CONTACT_NAME), getArguments().getString(ConstantRegistry.CHATSTER_CONTACT_NAME)));
                    tvNotRegisteredAsk.setText(getString(R.string.not_registered_ask, getArguments().getString(ConstantRegistry.CHATSTER_CONTACT_NAME), getArguments().getString(ConstantRegistry.CHATSTER_CONTACT_NAME)));
                } else{
                    Toast.makeText(chatActivity, R.string.smth_went_wrong, Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(chatActivity, R.string.smth_went_wrong, Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(chatActivity, R.string.smth_went_wrong, Toast.LENGTH_LONG).show();
        }

        builder.setPositiveButton(R.string.invite,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                notRegisteredDialogFragmentListener.onPositiveClick(NotRegisteredDialogFragment.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                notRegisteredDialogFragmentListener.onNegativeClick(NotRegisteredDialogFragment.this);
            }
        });

        builder.setView(view);

        //AlertDialog dialog = builder.create();

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity){
            chatActivity = (ChatActivity) context;
            notRegisteredDialogFragmentListener = (NotRegisteredDialogFragment.NotRegisteredDialogFragmentListener) chatActivity;
        }
    }
}
