package com.digitaldealsolution.plotlinetask.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitaldealsolution.plotlinetask.MainActivity;
import com.digitaldealsolution.plotlinetask.Models.UserModel;
import com.digitaldealsolution.plotlinetask.NoInternetActivity;
import com.digitaldealsolution.plotlinetask.R;
import com.digitaldealsolution.plotlinetask.Utils.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class SignupTabFragment extends Fragment {

    private EditText editTextFullname, editTextEmail, editTextPassword,editTextCPassword;
    private Button signup_btn;
    private FirebaseDatabase rootNode;
    private DatabaseReference rootRefrence;
    private FirebaseAuth mAuth;
    TabLayout tabLayout;
    private static final String TAG = "MyActivity";
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_signup_tab, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextFullname = getActivity().findViewById(R.id.full_name);
        editTextEmail = getActivity().findViewById(R.id.email);
        editTextPassword =getActivity().findViewById(R.id.pass);
        editTextPassword.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextCPassword =getActivity().findViewById(R.id.confirm_pass);
        editTextCPassword.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tabLayout= getActivity().findViewById(R.id.login_tablayout);
        signup_btn = getActivity().findViewById(R.id.sigup_btn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (InternetConnection.checkConnection(getActivity())) {

                    registerUser(v);;
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please Connect to Internet to proceed further")
                            .setCancelable(false)
                            .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                                }
                            })
                            .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity(), NoInternetActivity.class));
                                    getActivity().finish();
                                }
                            }).show();
                }
            }
        });
    }

    private Boolean validateName() {
        String val = editTextFullname.getText().toString();
        if (val.isEmpty()) {
            editTextFullname.setError("Field cannot be empty");
            return false;
        } else {
            editTextFullname.setError(null);
            return true;
        }
    }
    private Boolean validateEmail(){
        String val = editTextEmail.getText().toString();
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(val.isEmpty()){
            editTextEmail.setError("Field cannot be empty");
            return false;
        }
        if(!pattern.matcher(val).matches()){
            editTextEmail.setError("Enter Valid Email");
            return false;
        }
        editTextEmail.setError(null);
        return true;

    }
    private Boolean validatePassword(){
        String val = editTextPassword.getText().toString();
        editTextCPassword = getActivity().findViewById(R.id.confirm_pass);
        String cpass = editTextCPassword.getText().toString();
        String passPatter = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        if(val.isEmpty()){
            editTextPassword.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(passPatter)) {
            editTextPassword.setError("Password too weak, Password must be 4 characters.\nPassword must not contain whitespace.\nPassword must contain at least 1 special character. ");
            return false;
        }
        else if(!val.equals(cpass)){
            editTextPassword.setError("Password and Confirm Password must match");
            return false;
        }
        else{
            editTextPassword.setError(null);
            return true;
        }
    }


    public void registerUser(View view){
        if(!validateName() | !validateEmail() | !validatePassword()) {
            return;
        }
        else{
            rootNode = FirebaseDatabase.getInstance();
            rootRefrence = rootNode.getReference("Users");
            final String name = editTextFullname.getText().toString();
            final String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                final FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                UserModel userHelper = new UserModel(name, email,mAuth.getUid());
                                rootRefrence.child(user.getUid()).setValue(userHelper);
                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
//
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
}