package com.unse.bienestar.comedor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unse.bienestar.comedor.Modelo.Usuario;
import com.bienestar.comedor.R;
import com.unse.bienestar.comedor.Utils.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.EventosViewHolder> {
    private ArrayList<Usuario> mUsuarios;
    private ArrayList<Usuario> mListCopia;
    private Context context;

    public UsuariosAdapter(ArrayList<Usuario> list, Context ctx) {
        this.mUsuarios = list;
        this.context = ctx;
        this.mListCopia = new ArrayList<>();
        this.mListCopia.addAll(mUsuarios);
    }

    @NonNull
    @Override
    public UsuariosAdapter.EventosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios, parent, false);

        return new UsuariosAdapter.EventosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosAdapter.EventosViewHolder holder, int position) {
        Usuario usuario = mUsuarios.get(position);

        String name = usuario.getNombre() + " " + usuario.getApellido();
        holder.name.setText(name);
        holder.dni.setText(String.valueOf(usuario.getIdUsuario()));
        holder.estado.setText(usuario.getValidez() == 1 ? "ACTIVO" : "INACTIVO");

    }

    public void filtrar(String txt, int tipo) {
        ArrayList<Usuario> result = new ArrayList<>();
        switch (tipo) {
            case Utils.LIST_RESET:
                mUsuarios.clear();
                mUsuarios.addAll(mListCopia);
                break;
            case Utils.LIST_DNI:
                for (Usuario item : mListCopia) {
                    if (String.valueOf(item.getIdUsuario()).contains(txt)) {
                        result.add(item);
                    }

                }
                mUsuarios.clear();
                mUsuarios.addAll(result);
                break;
            case Utils.LIST_NOMBRE:
                for (Usuario item : mListCopia) {
                    if (item.getNombre().toLowerCase().contains(txt.toLowerCase())
                            || item.getApellido().toLowerCase().contains(txt.toLowerCase())) {
                        result.add(item);
                    }

                }
                mUsuarios.clear();
                mUsuarios.addAll(result);
                break;
        }
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return mUsuarios.get(position).getIdUsuario();
    }

    @Override
    public int getItemCount() {
        return mUsuarios.size();
    }

    static class EventosViewHolder extends RecyclerView.ViewHolder {

        TextView dni, name, estado;

        EventosViewHolder(View itemView) {
            super(itemView);

            dni = itemView.findViewById(R.id.txtDni);
            name = itemView.findViewById(R.id.txtName);
            estado = itemView.findViewById(R.id.txtEstado);
        }
    }

    public void change(ArrayList<Usuario> list) {
        mUsuarios = list;
        this.mListCopia = new ArrayList<>();
        this.mListCopia.addAll(mUsuarios);
        notifyDataSetChanged();
    }
}
