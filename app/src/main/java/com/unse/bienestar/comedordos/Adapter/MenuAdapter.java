package com.unse.bienestar.comedordos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unse.bienestar.comedordos.Modelo.Menu;
import com.unse.bienestar.comedordos.R;
import com.unse.bienestar.comedordos.Utils.ABC;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context mContext;
    private ArrayList<Menu> mMenus;

    public MenuAdapter(Context context, ArrayList<Menu> menus) {
        mContext = context;
        mMenus = menus;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {

        Menu menu = mMenus.get(position);

        holder.txtTitulo.setText(ABC.getInfoDate(menu.getDia(), menu.getMes(), menu.getAnio()));

    }

    @Override
    public int getItemCount() {
        return mMenus.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitulo;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitulo = itemView.findViewById(R.id.txtTitulo);
        }
    }
}
