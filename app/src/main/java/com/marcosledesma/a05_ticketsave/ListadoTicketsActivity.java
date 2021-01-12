package com.marcosledesma.a05_ticketsave;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marcosledesma.a05_ticketsave.adapters.TicketsAdapter;
import com.marcosledesma.a05_ticketsave.configuraciones.Configuracion;
import com.marcosledesma.a05_ticketsave.databinding.ActivityListadoTicketsBinding;
import com.marcosledesma.a05_ticketsave.modelos.Ticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListadoTicketsActivity extends AppCompatActivity {

    private final int TAKE_PHOTO = 10;
    private final int REQ_PERMISOS = 1;
    private ActivityListadoTicketsBinding binding;

    // Vars para el Recycler
    private ArrayList<Ticket> listaTickets;
    private TicketsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // Imagen Capturada
    private String currentPhotoPath;

    // FireBase
    FirebaseDatabase database;
    private DatabaseReference referencia;
    private DatabaseReference refTickets;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Modificar porque el binding ya contiene los elementos de la vista
        binding = ActivityListadoTicketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instanciar acceso a BD
        database = FirebaseDatabase.getInstance();
        referencia = database.getReference(Configuracion.currentUser.getUid()); // Acceso al nodo grande del usr
        refTickets = referencia.child("tickets");   // Acceder al nodo tickets del usr
        // mStorageRef Creará una carpeta por cada usuario
        mStorageRef = FirebaseStorage.getInstance().getReference(Configuracion.currentUser.getUid()).child("img_tickets");

        //
        listaTickets = new ArrayList<>();
        adapter = new TicketsAdapter(listaTickets, R.layout.elemento_ticket, this);
        layoutManager = new LinearLayoutManager(this);

        // Hilo independiente del programa
        refTickets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Si snapshot existe, genero
                if (snapshot.exists()) {
                    GenericTypeIndicator<ArrayList<Ticket>> gti =
                            new GenericTypeIndicator<ArrayList<Ticket>>() {
                            };
                    listaTickets.clear();
                    listaTickets.addAll(snapshot.getValue(gti));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    takePhoto();
                } // Si es superior los pido
                else {
                    // Si ya tengo los permisos -> takePhoto();
                    if (ContextCompat.checkSelfPermission(ListadoTicketsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(ListadoTicketsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        takePhoto();
                    } else {
                        // Si no los tengo, los pido
                        String[] permisos = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(ListadoTicketsActivity.this, permisos, REQ_PERMISOS);

                    }
                }
            }
        });

        //
        binding.txtEmailUser.setText(Configuracion.currentUser.getEmail());
    }

    /**
     * Crear un fichero para guardar la foto
     *
     * @return del fichero de la foto
     */
    private File crearFichero() throws IOException {
        // Dar nombre al fichero (timeStamp del momento en que toma la foto)
        String timeStamp = new SimpleDateFormat("yyyyMMdddd_hhmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        // Saber donde guardar el fichero
        File directoryPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crear la imágen (nombre, extensión y carpeta donde se creará)
        File imagen = File.createTempFile(fileName, ".jpg", directoryPictures);

        return imagen;
    }

    // Func takePhoto -> escribirá en un fichero la imágen que toma la cámara (no devuelve la imágen)

    /**
     * Implementar la llamada a la cámara pasándole una URI del fichero que será la foto
     */
    private void takePhoto() {
        try {
            File foto = crearFichero();
            currentPhotoPath = foto.getAbsolutePath();
            Uri uriFoto = FileProvider.getUriForFile(this, "com.marcosledesma.a05_ticketsave", foto);
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
            // lanzar activity
            startActivityForResult(intentCamera, TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Saber si la cámara ha hecho bien la foto y crear ticket
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            // Crear ticket, asignar valores de la imágen, etc (), con alertDialog
            createTicket();
        }
    }

    /**
     * Crea el AlertDialog
     * Crea el Ticket
     * Inserta los valores del ticket
     * Inserta el Ticket en el ArrayList
     * Notifica de cambios en el Adapter
     */
    private void createTicket() {
        // 1. Crear Layout para Alert Dialog (ticket_dialog.xml)
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ticket_dialog, null);
        // 2. Crear Alert Dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("NEW TICKET");
        dialog.setCancelable(false);
        dialog.setView(dialogView);
        // 3. txt para el Alert Dialog (txtComercio, txtImporte, txtFecha)
        EditText txtComercio = dialogView.findViewById(R.id.txtComercioDialog);
        EditText txtImporte = dialogView.findViewById(R.id.txtImporteDialog);
        EditText txtFecha = dialogView.findViewById(R.id.txtFechaDialog);

        // 4. Opciones del Dialog (Cancel & Ok)
        dialog.setNegativeButton("CANCELAR", null);
        dialog.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtComercio.getText().toString().isEmpty()
                        && !txtImporte.getText().toString().isEmpty()) {
                    binding.content.loaderFichero.setVisibility(View.VISIBLE);
                    // Crear Ticket
                    Ticket ticket = new Ticket();

                    ticket.setNombreComercio(txtComercio.getText().toString());
                    ticket.setImporteCompra(Float.parseFloat(txtImporte.getText().toString()));
                    // Fecha del ticket (si no hay -> new Date() para que no explote)
                    if (txtFecha.getText().toString().isEmpty()) {
                        ticket.setFechaCompra(new Date());
                    } else {
                        try {
                            ticket.setFechaCompra(Configuracion.sdf.parse(txtFecha.getText().toString()));
                        } catch (ParseException e) {
                            ticket.setFechaCompra(new Date());
                        }
                    }
                    // Añadir ticket a listaTickets y notificar al Adapter (mediante refTickets)
                    Uri ficheroSubir = Uri.fromFile(new File(currentPhotoPath));

                    // Método único que lanza tarea en segundo plano (.putFile y .addOnSuccesListener)
                    mStorageRef.child(new File(currentPhotoPath).getName()).putFile(ficheroSubir)
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    // Configurar progressBar (tamaño máximo = máx bytes sizes a subir)
                                    binding.content.loaderFichero.setMax((int)snapshot.getTotalByteCount());
                                    // Cambiar valor del progress
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        // Animado
                                        binding.content.loaderFichero.setProgress((int)snapshot.getBytesTransferred(), true);
                                    }else {
                                        // Sin animación
                                        binding.content.loaderFichero.setProgress((int)snapshot.getBytesTransferred());
                                    }
                                }
                            })

                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Cuando termine crearé un Task de tipo Uri que será el resultado (comprobar que ha terminado)
                                    Task<Uri> resultado = taskSnapshot.getStorage().getDownloadUrl();
                                    // Ponerme a la escucha del resultado
                                    resultado.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            ticket.setUrlImagen(uri.toString());
                                            listaTickets.add(ticket);
                                            refTickets.setValue(listaTickets);
                                            binding.content.loaderFichero.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                }
            }
        });
        // Mostrar dialog
        dialog.show();
    }

    // Mostrar menú logOut en esta Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);    // quitar return
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // Item clickado del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item); // quitar return
        if (item.getItemId() == R.id.logOut){
            FirebaseAuth.getInstance().signOut();   // signOut FireBase
            // Volver al Main una vez hecho logOut
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return true;
    }

    // onRequestPermissionResult asociado a REQ_PERMISOS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        if (requestCode == REQ_PERMISOS) {
            if (permissions.length > 0) {
                // Si me autorizas a hacer la foto y guardarla -> takePhoto();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
            }
        }
    }
}