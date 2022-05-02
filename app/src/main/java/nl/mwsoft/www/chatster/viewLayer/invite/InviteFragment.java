package nl.mwsoft.www.chatster.viewLayer.invite;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;

public class InviteFragment extends Fragment {

    public String userName;
    public String inviteeName;
    public String inviteeEmail;
    private Unbinder unbinder;
    private InviteActivity inviteActivity;
    @BindView(R.id.etFrom)
    EditText etFrom;
    @BindView(R.id.etToName)
    EditText etToName;
    @BindView(R.id.etToEmail)
    EditText etToEmail;

    public static InviteFragment newInstance(String userName, String inviteeName) {

        Bundle args = new Bundle();
        args.putString(ConstantRegistry.USER_NAME, userName);
        args.putString(ConstantRegistry.INVITEE_NAME, inviteeName);
        InviteFragment fragment = new InviteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            userName = arguments.getString(ConstantRegistry.USER_NAME);
            inviteeName = arguments.getString(ConstantRegistry.INVITEE_NAME);

            etFrom.setText(userName);
            etToName.setText(inviteeName);

            etFrom.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    userName = s.toString();
                }
            });

            etToName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    inviteeName = s.toString();
                }
            });

            etToEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    inviteeEmail = s.toString();
                }
            });
        }else{

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        inviteActivity = (InviteActivity) context;
    }
}
