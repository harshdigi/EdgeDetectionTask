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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitaldealsolution.plotlinetask.MainActivity;
import com.digitaldealsolution.plotlinetask.NoInternetActivity;
import com.digitaldealsolution.plotlinetask.R;
import com.digitaldealsolution.plotlinetask.Utils.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;


public class LoginTabFragment extends Fragment {
    private EditText editTextEmail, pass;
    private Button login;
    private TextView forget;
    private float v = 0;
    private FirebaseAuth mAuth;
    private TextView verifyEmail;

    private static final String TAG = "MyActivity";

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);
        editTextEmail = root.findViewById(R.id.email_login);
        pass = root.findViewById(R.id.pass_login);
        pass.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        login = root.findViewById(R.id.login_btn);
        forget = root.findViewById(R.id.forget);
        mAuth = FirebaseAuth.getInstance();
        verifyEmail = root.findViewById(R.id.verifyemail);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null ) {
            if(!user.isEmailVerified()){
                verifyEmail.setVisibility(View.VISIBLE);
                user.sendEmailVerification();}
        }
        editTextEmail.setTranslationX(300);
        pass.setTranslationX(300);
        login.setTranslationX(300);
        forget.setTranslationX(300);
        editTextEmail.setAlpha(v);
        pass.setAlpha(v);
        login.setAlpha(v);
        forget.setAlpha(v);
        editTextEmail.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forget.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (InternetConnection.checkConnection(getActivity())) {
                    loginUser();
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
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getActivity())) {
                    forgetuserpass();
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


    private Boolean validateEmail() {
        String val = editTextEmail.getText().toString();
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (val.isEmpty()) {
            editTextEmail.setError("Field cannot be empty");
            return false;
        } else if (!pattern.matcher(val).matches()) {
            editTextEmail.setError("Enter Valid Email");
            return false;
        } else {
            editTextEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = pass.getText().toString();
        if (val.isEmpty()) {
            pass.setError("Field cannot be empty");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    public void forgetuserpass() {
        if (!validateEmail()) {
            return;
        } else {
            String email = editTextEmail.getText().toString();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Forget password email sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(getActivity(),
                                        "Failed to send forget email. User does not exist",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public void loginUser() {
        if (!validateEmail() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        final String email = editTextEmail.getText().toString();
        final String password = pass.getText().toString();
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();
                    if(signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD))
                    {
                        // User can sign in with email/password
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    final FirebaseUser user = mAuth.getCurrentUser();

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                        Query checkUser = reference.orderByChild("email").equalTo(email);
                                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    editTextEmail.setError(null);
                                                    pass.setError(null);
                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                } else {
                                                    editTextEmail.setError("No such user exist");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Authentication failed",
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }

                        });

                    } else if (signInMethods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {
                        Toast.makeText(getContext(),"User created using Google account, Use Google Login",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Error getting sign in methods for user", task.getException());
                }
            }
        });

    }

}