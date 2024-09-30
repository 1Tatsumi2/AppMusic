package com.example.musicapp.Fragments;

import static com.example.musicapp.R.id.UserName;
import static com.example.musicapp.R.id.saveUpdateButton;
import static com.example.musicapp.R.id.textView;

import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.Activities.MainActivity;
import com.example.musicapp.Activities.MusicPlayer;
import com.example.musicapp.Class.Account;
import com.example.musicapp.MusicManager.MusicManagerActivity;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.sql.DatabaseMetaData;


public class SignUpFragment extends Fragment {
    private TextView alreadyHaveAnAccount;
    private FrameLayout frameLayout;

    private EditText UserName;
    private EditText Email;
    private String Image;
    private boolean Premium;
    private String Role;
    private EditText Password;
    private EditText ConfirmPass;
    private Button signUpButton;
    private ProgressBar signUpProgress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_sign_up, container, false );
        alreadyHaveAnAccount = view.findViewById( R.id.already_have_account );
        frameLayout = getActivity().findViewById( R.id.register_frame_layout );

        UserName = view.findViewById( R.id.UserName );
        Email = view.findViewById( R.id.Email );
        Password = view.findViewById( R.id.Password );
        ConfirmPass = view.findViewById( R.id.ConfirmPass );
        signUpButton = view.findViewById( R.id.signUpButton );
        signUpProgress = view.findViewById( R.id.signUpProgress );
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        alreadyHaveAnAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment( new SignInFragment() );
            }
        } );

        UserName.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        Email.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        Password.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        ConfirmPass.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        signUpButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.setEnabled( false );
                signUpButton.setTextColor( getResources().getColor( R.color.white ) );
                signUpWithFirebase();
            }
        } );
    }

    private void signUpWithFirebase() {
        if (Email.getText().toString().matches( "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" )) {
            if (Password.getText().toString().equals( ConfirmPass.getText().toString() )) {
                signUpProgress.setVisibility( View.VISIBLE );
                mAuth.createUserWithEmailAndPassword( Email.getText().toString(), Password.getText().toString() )
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                signUpProgress.setVisibility( View.INVISIBLE );
                                signUpButton.setEnabled( true );
                                signUpButton.setTextColor( getResources().getColor( R.color.white ) );
                                if (task.isSuccessful()) {
                                    String Account = mAuth.getCurrentUser().getUid();
                                    saveAccount( Account );
                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace( R.id.register_frame_layout, new SignInFragment() ); // R.id.fragment_container là id của container chứa fragment
                                    fragmentTransaction.addToBackStack( null );
                                    fragmentTransaction.commit();
                                } else {
                                    Toast.makeText( getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                                }
                            }
                        } );
            } else {
                ConfirmPass.setError( "Password doesn't match." );
                signUpButton.setEnabled( true );
                signUpButton.setTextColor( getResources().getColor( R.color.white ) );
            }
        } else {
            Email.setError( "Invalid Email Pattern:" );
            signUpButton.setEnabled( true );
            signUpButton.setTextColor( getResources().getColor( R.color.white ) );
        }
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations( R.anim.from_left, R.anim.out_from_right );
        fragmentTransaction.replace( frameLayout.getId(), fragment );
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!UserName.getText().toString().isEmpty()) {
            if (!Email.getText().toString().isEmpty()) {
                if (!Password.getText().toString().isEmpty() && Password.getText().toString().length() >= 6) {
                    if (!ConfirmPass.getText().toString().isEmpty()) {
                        signUpButton.setEnabled( true );
                        signUpButton.setTextColor( getResources().getColor( R.color.white ) );
                    } else {
                        signUpButton.setEnabled( false );
                        signUpButton.setTextColor( getResources().getColor( R.color.white ) );
                    }

                } else {
                    signUpButton.setEnabled( false );
                    signUpButton.setTextColor( getResources().getColor( R.color.white ) );
                }

            } else {
                signUpButton.setEnabled( false );
                signUpButton.setTextColor( getResources().getColor( R.color.white ) );
            }

        } else {
            signUpButton.setEnabled( false );
            signUpButton.setTextColor( getResources().getColor( R.color.white ) );
        }
    }

    private void saveAccount(String Account) {
        // Tạo đối tượng người dùng với các thông tin đã nhập
        Account account = new Account(
                UserName.getText().toString(),
                Email.getText().toString(),
                Password.getText().toString(),
                " ", // Image URL mặc định
                false, // Premium false by default
                "User" // Default role là User
        );

        // Lưu thông tin người dùng vào Firestore
        db.collection( "Account" ).document( Account ).set( account ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText( getContext(), "User Registered Successfully", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( getContext(), "Failed to save user data", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }
}