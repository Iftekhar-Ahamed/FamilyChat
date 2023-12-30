package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.example.familychat.Home;
import com.example.familychat.LogIn;
import com.example.familychat.R;
import com.example.familychat.utils.MyInformation;
import com.example.familychat.utils.SignalRManager;
import com.example.familychat.utils.FileOperation;

import java.io.File;

public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText usernameInput;
    EditText password;
    Button updateProfileBtn;
    ProgressBar progressBar;
    Button logoutBtn;
    public ProfileFragment() {

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        logoutBtn = view.findViewById(R.id.profle_logout);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        usernameInput = view.findViewById(R.id.profile_username);
        password = view.findViewById(R.id.profile_password);

        progressBar = view.findViewById(R.id.profile_progress_bar);
        setInProgress(false);


        usernameInput.setText(MyInformation.data.userName);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInProgress(true);
                if (getActivity() instanceof Home && SignalRManager.serviceRunning) {
                    Home homeActivity = (Home) getActivity();
                    homeActivity.stopBroadcastService();
                }
                File path = getContext().getFilesDir();
                FileOperation.deleteFile(path,"UserLogInInfo.txt");
                Intent intent = new Intent(getContext(), LogIn.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
        }
    }
}
