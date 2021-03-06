package com.example.pratik.digitaloutpass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginStudentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginStudentFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private FirebaseAuth mAuth;
    EditText etEmail;
    EditText etPassword;
    Button bLogin;
    TextView tvGotToSignup;
    Button bLoginAsWar;
    DatabaseReference userDatabase;
    String getRole;
    boolean flag;
    public LoginStudentFragment() {
        // Required empty public constructor
    }

    public static LoginStudentFragment newInstance() {
        LoginStudentFragment fragment = new LoginStudentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_student, container, false);
        etEmail = v.findViewById(R.id.etEmailLoginStudent);
        etPassword = v.findViewById(R.id.etPasswordLoginStudent);
        bLogin = v.findViewById(R.id.bLogin);
        //bLoginAsWar = v.findViewById(R.id.bloginAsWarden);
        //bLoginAsWar.setOnClickListener(this);
        bLogin.setOnClickListener(this);
        tvGotToSignup = v.findViewById(R.id.tvGoToSignup);
        tvGotToSignup.setOnClickListener(this);

        return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogin:
                clickOnLogin();
                break;
            case R.id.tvGoToSignup:
                gotToSignup();
                break;
            //case R.id.bloginAsWarden:
                //loginAsWarden();
        }
    }

    private void gotToSignup() {
        mListener.switchFragment();
    }

    private void clickOnLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        //Toast.makeText(getContext(), "Email: "+email, Toast.LENGTH_SHORT).show();

        //Toast.makeText(getContext(), email.length()+"", Toast.LENGTH_SHORT).show();
        if(email==null || email.length()==0){
            etEmail.setError("Enter an email");
            etEmail.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
        }
        else if(password==""){
            etPassword.setError("Please enter a password");
            etPassword.requestFocus();
        }
        else if (password.length()<6){
            etPassword.setError("Password length should be greater than 6");
            etPassword.requestFocus();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();

                        userDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
                        final FirebaseUser user = mAuth.getCurrentUser();
                        final String Uid = mAuth.getCurrentUser().getUid();



                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Toast.makeText(getActivity(),"Inside",Toast.LENGTH_SHORT).show();
                                getRole = dataSnapshot.child("role").getValue(String.class);
//                                if(!getRole.equals("STUDENT")){
//
//                                    mAuth.signOut();
//                                    Toast.makeText(getContext(), "Not a Student", Toast.LENGTH_SHORT).show();
//                                }
//
//
//
//                                else {

                                    if (!(user.isEmailVerified())) {

                                        //startActivity(new Intent(getContext(), MainActivity.class));
                                        VerificationFragment verificationFragment = new VerificationFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.SSConstraintLayout, verificationFragment, "findThisFragment")
                                                .addToBackStack(null)
                                                .commit();
                                    } else {

                                        if (getRole.equals(User.STUDENT)) {
                                            startActivity(new Intent(getContext(), MainActivity.class));

                                        }else if(getRole.equals(User.WARDEN)){
                                            startActivity(new Intent(getContext(), WardenActivity.class));
                                        }
                                        else{
                                            startActivity(new Intent(getContext(), CaretakerActivity.class));
                                        }
                                    }

                                }
                            //}



                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        Toast.makeText(getContext(),getRole,Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

//    public void loginAsWarden(){
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString();
//
//        if(email==null || email.length()==0){
//            etEmail.setError("Enter an email");
//            etEmail.requestFocus();
//        }
//        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            etEmail.setError("Enter a valid email address");
//            etEmail.requestFocus();
//        }
//        else if(password==""){
//            etPassword.setError("Please enter a password");
//            etPassword.requestFocus();
//        }
//        else if (password.length()<6){
//            etPassword.setError("Password length should be greater than 6");
//            etPassword.requestFocus();
//        }
//        else{
//            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if(task.isSuccessful()) {
//                        Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
//
//                        final FirebaseUser user = mAuth.getCurrentUser();
//
//                        String Uid = mAuth.getCurrentUser().getUid();
//
//                        userDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
//
//                        userDatabase.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                getRole = dataSnapshot.child("role").getValue(String.class);
//                                if (!getRole.equals("WARDEN")) {
//                                    mAuth.signOut();
//                                    Toast.makeText(getContext(), "Not a Warden", Toast.LENGTH_SHORT).show();
//
//                                }
//
//
//
//                                else {
//                                    if (!(user.isEmailVerified())) {
//                                        //startActivity(new Intent(getContext(), MainActivity.class));
//                                        VerificationFragment verificationFragment = new VerificationFragment();
//                                        getActivity().getSupportFragmentManager().beginTransaction()
//                                                .replace(R.id.SSConstraintLayout, verificationFragment, "findThisFragment")
//                                                .addToBackStack(null)
//                                                .commit();
//                                    } else {
//                                        startActivity(new Intent(getContext(), WardenActivity.class));
//                                    }
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//
//                    }
//                    else{
//                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void switchFragment();
    }
}
