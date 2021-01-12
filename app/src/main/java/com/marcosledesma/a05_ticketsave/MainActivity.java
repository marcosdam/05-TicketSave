package com.marcosledesma.a05_ticketsave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marcosledesma.a05_ticketsave.adapters.TicketsAdapter;
import com.marcosledesma.a05_ticketsave.configuraciones.Configuracion;
import com.marcosledesma.a05_ticketsave.databinding.ActivityMainBinding;
import com.marcosledesma.a05_ticketsave.modelos.Ticket;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // instancia autenticación FireBase
    private FirebaseAuth mAuth;
    // binding con todos lo elementos de la vista (evita findViewById)
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);   -> Eliminar setContentView y especificar binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View vista = binding.getRoot();
        setContentView(vista);
        // Ya tengo todos los elementos de la vista en el bingding (no necesito definir vars ni hacer findViewById)

        mAuth = FirebaseAuth.getInstance();

        // Listener btnDoLogin
        binding.btnDoLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // si los txt no están vacíos llamamos a la func doLogin
                if (!binding.txtEmailLogin.getText().toString().isEmpty()
                        && !binding.txtPasswordLogin.getText().toString().isEmpty()) {
                    doLogin(binding.txtEmailLogin.getText().toString(), binding.txtPasswordLogin.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener btnRegister
        binding.btnRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.txtEmailLogin.getText().toString().isEmpty()
                        && !binding.txtPasswordLogin.getText().toString().isEmpty()) {
                    doRegister(binding.txtEmailLogin.getText().toString(), binding.txtPasswordLogin.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Recibe email y password y utiliza createUserWithEmailAndPassword method de FireBase (Sign up new users)
    private void doRegister(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Configuracion.currentUser = user;   //
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Registro fallido.",
                                    Toast.LENGTH_SHORT).show();
                            Configuracion.currentUser = null;
                            updateUI(null);
                        }
                    }
                });
    }

    //
    private void updateUI(FirebaseUser user) {
        if (user != null){
            // Abriré la nueva ventana (ListadoTicketsActivity)
            Configuracion.currentUser = user;
            startActivity(new Intent(this, ListadoTicketsActivity.class));
            // Cierra la ventana
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // Recibe email y password y utiliza signInWithEmailAndPassword method de FireBase (Sign up existing users)
    private void doLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Configuracion.currentUser = user;   //
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login fallido.",
                                    Toast.LENGTH_SHORT).show();
                            Configuracion.currentUser = null;   //
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}