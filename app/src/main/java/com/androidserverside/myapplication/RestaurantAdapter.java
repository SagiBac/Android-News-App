package com.androidserverside.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class RestaurantAdapter  extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>{

        private List<Restaurant> restaurants;
        RestaurantEventListener listener;


        public void setListener(RestaurantEventListener listener){
            this.listener = listener;
        }

        public RestaurantAdapter(List<Restaurant> restaurants){
            this.restaurants = restaurants;
        }


        public class RestaurantViewHolder extends RecyclerView.ViewHolder{
            TextView RestaurantTitleTv;
            TextView RestaurantRatingTv;
            TextView RestaurantDetails;
            ImageView RestaurantImage;
            ProgressBar RestaurantImageProgressBar;

            public RestaurantViewHolder(@NonNull final View itemView) {
                super(itemView);
                RestaurantTitleTv = itemView.findViewById(R.id.Article_title);
                RestaurantRatingTv = itemView.findViewById(R.id.Article_description);
                RestaurantImage = itemView.findViewById(R.id.Article_image);
                RestaurantImageProgressBar = itemView.findViewById(R.id.RestaurantFragment_ProgressBar);

                RestaurantDetails = itemView.findViewById(R.id.Article_SeeMore);
                RestaurantDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.GoToRestaurant(getAdapterPosition());
                    }
                });
            }
        }

        @NonNull
        @Override
        public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,parent,false);
            RestaurantViewHolder RestaurantViewHolder = new RestaurantViewHolder(view);
            return RestaurantViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
            Restaurant Restaurant = restaurants.get(position);
            holder.RestaurantTitleTv.setText(Restaurant.getName());
            holder.RestaurantRatingTv.setText(Restaurant.getRating());
            holder.RestaurantImage.setImageBitmap(Restaurant.getImage());
        }

        @Override
        public int getItemCount() {
            return restaurants.size();
        }

        interface RestaurantEventListener{
            void GoToRestaurant(int position);
        }

    }
