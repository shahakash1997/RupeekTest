package com.rupeek.rupeektest.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rupeek.rupeektest.R;
import com.rupeek.rupeektest.RupeekApp;
import com.rupeek.rupeektest.databinding.PlaceItemLayoutBinding;
import com.rupeek.rupeektest.models.PlaceModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {
    private static final String TAG = "PlacesAdapter";
    private List<PlaceModel> placeList;
    private Context context;
    private LayoutInflater layoutInflater;
    private PlaceClickListener listener;

    public PlacesAdapter(List<PlaceModel> placeList, Context context, PlaceClickListener listener) {
        this.placeList = placeList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;

    }

    public void clearALl() {
        this.placeList.clear();
        notifyDataSetChanged();
    }

    public void updateList(List<PlaceModel> placeList) {
        this.placeList.addAll(placeList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaceItemLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.place_item_layout, parent, false);
        return new PlaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.PlaceViewHolder holder, int position) {
        final PlaceModel placeModel = placeList.get(position);
        if (placeModel != null) {
            holder.layoutBinding.tvPlaceName.setText(placeModel.getName());
            holder.layoutBinding.tvPlaceAddress.setText(placeModel.getAddress());
            loadImagesImageView(placeModel.getImage(), holder.layoutBinding.ivPlace);
            holder.layoutBinding.cvMain.setOnClickListener(v -> listener.onClick(v.getId(), placeModel,holder.getAdapterPosition()));
            holder.layoutBinding.ivDirec.setOnClickListener(v -> listener.onClick(v.getId(), placeModel,holder.getAdapterPosition()));
            holder.layoutBinding.ivUpvote.setOnClickListener(v -> listener.onClick(v.getId(), placeModel,holder.getAdapterPosition()));
            if (placeModel.getChecked() != null && placeModel.getChecked()) {
                holder.layoutBinding.ivUpvote.setImageDrawable(RupeekApp.getContext().getDrawable(R.drawable.ic_baseline_favorite_filled_24));
            } else {
                holder.layoutBinding.ivUpvote.setImageDrawable(RupeekApp.getContext().getDrawable(R.drawable.ic_baseline_favorite_border_24));

            }
        }

    }

    private void loadImagesImageView(final String url, final ImageView imageView) {
        Log.d(TAG, "loadImagesImageView: " + url);
        Picasso.get().load(url)
                .resize(200, 200)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return placeList == null ? 0 : placeList.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        public PlaceItemLayoutBinding layoutBinding;

        public PlaceViewHolder(@NonNull PlaceItemLayoutBinding binding) {
            super(binding.getRoot());
            this.layoutBinding = binding;
        }
    }

    interface PlaceClickListener {
        void onClick(int id, PlaceModel placeModel,int pos);
    }
}
