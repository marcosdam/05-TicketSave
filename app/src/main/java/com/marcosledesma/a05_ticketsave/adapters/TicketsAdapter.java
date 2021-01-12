package com.marcosledesma.a05_ticketsave.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcosledesma.a05_ticketsave.R;
import com.marcosledesma.a05_ticketsave.configuraciones.Configuracion;
import com.marcosledesma.a05_ticketsave.modelos.Ticket;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketVH> {

    private List<Ticket> objects;
    private int resource;
    private Context context;

    public TicketsAdapter(List<Ticket> objects, int resource, Context context) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
    }

    // Devuelve TicketViewHolder pasándole el elemento
    @NonNull
    @Override
    public TicketVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elemento = LayoutInflater.from(context).inflate(resource, null);   // Elemento
        elemento.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); // Parámetros
        return new TicketVH(elemento);
    }

    // Obtener todos los datos del ticket y pintarlos en el TicketViewHolder
    @Override
    public void onBindViewHolder(@NonNull TicketVH holder, int position) {
        holder.imgTicket.setImageURI(Uri.parse(objects.get(position).getUrlImagen()));
        holder.txtComercio.setText(objects.get(position).getNombreComercio());
        holder.txtFecha.setText(Configuracion.sdf.format(objects.get(position).getFechaCompra()));
        holder.txtImporte.setText(Configuracion.nf.format(objects.get(position).getImporteCompra()));
    }

    // Devuelve objects.size (tamaño lista de Tickets)
    @Override
    public int getItemCount() {
        return objects.size();
    }

    // Clase interna TicketViewHolder
    public class TicketVH extends RecyclerView.ViewHolder{

        ImageView imgTicket;
        TextView txtComercio, txtFecha, txtImporte;

        public TicketVH(@NonNull View itemView){
            super(itemView);
            imgTicket = itemView.findViewById(R.id.imgTicketElemento);
            txtComercio = itemView.findViewById(R.id.txtComercioElemento);
            txtFecha = itemView.findViewById(R.id.txtFechaElemento);
            txtImporte = itemView.findViewById(R.id.txtImporteElemento);
        }

    }
}
