package com.marcosledesma.a05_ticketsave.configuraciones;

import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class Configuracion {

    // Var públicas que utilizará la app
    public static SimpleDateFormat sdf;
    public static NumberFormat nf;
    public static FirebaseUser currentUser;

    // Inicializar
    static {
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        nf = NumberFormat.getCurrencyInstance();
    }
}
