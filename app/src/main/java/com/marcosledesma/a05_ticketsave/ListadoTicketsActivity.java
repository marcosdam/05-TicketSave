package com.marcosledesma.a05_ticketsave;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.marcosledesma.a05_ticketsave.adapters.TicketsAdapter;
import com.marcosledesma.a05_ticketsave.configuraciones.Configuracion;
import com.marcosledesma.a05_ticketsave.databinding.ActivityListadoTicketsBinding;
import com.marcosledesma.a05_ticketsave.modelos.Ticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;

public class ListadoTicketsActivity extends AppCompatActivity {

    private final int REQ_PERMISOS = 1;
    private ActivityListadoTicketsBinding binding;

    // Vars para el Recycler
    private ArrayList<Ticket> listaTickets;
    private TicketsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_listado_tickets);

        // Modificar porque el binding ya contiene los elementos de la vista
        binding = ActivityListadoTicketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //
        listaTickets = new ArrayList<>();
        adapter = new TicketsAdapter(listaTickets, R.layout.elemento_ticket, this);
        layoutManager = new LinearLayoutManager(this);

        //
        binding.content.listadoTicketsRecycler.setHasFixedSize(true);
        binding.content.listadoTicketsRecycler.setLayoutManager(layoutManager);
        binding.content.listadoTicketsRecycler.setAdapter(adapter);

        setSupportActionBar(binding.toolbar);

        // Listener fab
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobar versión Android (si es inferior a API 23 no pide permisos)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    takePhoto();
                } // Si es superior los pido
                else {
                    // Si ya tengo los permisos -> takePhoto();
                    if (ContextCompat.checkSelfPermission(ListadoTicketsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(ListadoTicketsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        takePhoto();
                    }else {
                        // Si no los tengo, los pido
                        String[]permisos = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(ListadoTicketsActivity.this, permisos, REQ_PERMISOS);

                    }
                }
            }
        });

        //
        binding.txtEmailUser.setText(Configuracion.currentUser.getEmail());
    }


    // onRequestPermissionResult asociado a REQ_PERMISOS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        if(requestCode == REQ_PERMISOS){
            if (permissions.length > 0) {
                // Si me autorizas a hacer la foto y guardarla -> takePhoto();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    takePhoto();
                }
            }
        }
    }
}