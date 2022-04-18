package no.uio.ifi.oscarlr.in5600_autoinsurance.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private boolean disableReplaceButtons = false;
    private boolean disableSeeDetailsButtons = false;

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
            try {
                File f = new File(claims.get(position).getClaimPhotoFilepath());
                b = BitmapFactory.decodeFile(f.getAbsolutePath());
                holder.imageView.setImageBitmap(b);
            } catch (Exception e) {
//                e.printStackTrace(); // Expected error if no image is stored locally
            }


        } else {
            holder.imageView.setImageBitmap(claims.get(position).getClaimPhotoBitmap());
        }

        if (disableReplaceButtons) {
            holder.replaceButton.setEnabled(false);
        }

        if (disableSeeDetailsButtons) {
            holder.seeDetailsButton.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return claims.size();
    }

    public void disableReplaceButton() {
        disableReplaceButtons = true;
    }

    public void setDisableSeeDetailsButtons() {
        disableSeeDetailsButtons = true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        ImageView imageView;
        Button replaceButton;
        Button seeDetailsButton;

        public ViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            description = itemView.findViewById(R.id.cardDescription);
            imageView = itemView.findViewById(R.id.cardPhoto);
            replaceButton = itemView.findViewById(R.id.cardReplaceButton);
            seeDetailsButton = itemView.findViewById(R.id.cardSeeDetailsButton);

            itemView.findViewById(R.id.cardReplaceButton).setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onReplaceClick(position);
                    }
                }
            });

            itemView.findViewById(R.id.cardSeeDetailsButton).setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onSeeDetailsClick(position);
                    }
                }
            });
        }
    }
}
