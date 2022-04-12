package no.uio.ifi.oscarlr.in5600_autoinsurance.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<Claim> claims;
    private final RecyclerViewInterface recyclerViewInterface;

    public RecyclerViewAdapter(Context context, List<Claim> claims, RecyclerViewInterface recyclerViewInterface) {
        this.inflater = LayoutInflater.from(context);
        this.claims = claims;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_card_element, parent, false);

        return new ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.description.setText(claims.get(position).getClaimDes());
        Bitmap b = claims.get(position).getClaimPhotoBitmap();
        if (b == null) {
            File f = new File(claims.get(position).getClaimPhotoFilepath());
            b = BitmapFactory.decodeFile(f.getAbsolutePath());
            holder.imageView.setImageBitmap(b);

        } else {
            holder.imageView.setImageBitmap(claims.get(position).getClaimPhotoBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return claims.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        ImageView imageView;

        public ViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            description = itemView.findViewById(R.id.cardDescription);
            imageView = itemView.findViewById(R.id.cardPhoto);

            itemView.findViewById(R.id.cardReplaceButton).setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onReplaceClick(position);
                    }
                }
            });
        }
    }
}
