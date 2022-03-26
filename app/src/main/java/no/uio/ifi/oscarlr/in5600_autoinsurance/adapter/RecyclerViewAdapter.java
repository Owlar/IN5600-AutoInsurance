package no.uio.ifi.oscarlr.in5600_autoinsurance.adapter;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<Claim> claims;

    public RecyclerViewAdapter(Context context, List<Claim> claims) {
        this.inflater = LayoutInflater.from(context);
        this.claims = claims;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, photo, location, status;

        public ViewHolder(View view) {
            super(view);

            description = view.findViewById(R.id.t1);
            photo = view.findViewById(R.id.t2);
            location = view.findViewById(R.id.t3);
            status = view.findViewById(R.id.t4);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_card_element, parent, false);

        DataProcessor dataProcessor = new DataProcessor(inflater.getContext());
        dataProcessor.saveClaims(claims);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.description.setText(claims.get(0).claimDes.get(position));
        holder.description.setText(claims.get(position).claimDes);
//        holder.photo.setText(claims.get(0).claimPhoto.get(position));
//        holder.location.setText(claims.get(0).claimLocation.get(position));
//        holder.status.setText(claims.get(0).claimStatus.get(position));
    }

    @Override
    public int getItemCount() {
        return claims.size();
    }


}
