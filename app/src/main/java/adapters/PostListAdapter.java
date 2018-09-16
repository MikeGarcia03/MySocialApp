package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tics.mysocialapp.R;

import java.util.List;

import models.PostItems;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>{

    private Context context;
    private List<PostItems> posts;

    public PostListAdapter() {
    }

    public PostListAdapter(Context context, List<PostItems> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PostItems postItems = posts.get(position);

        holder.userNameTxt.setText(postItems.getUserName());
        holder.postTitleTxt.setText(postItems.getTitle());
        holder.postDescriptionTxt.setText(postItems.getDescription());

        Glide.with(context)
                .load(postItems.getImageUrl())
                .into(holder.postImageView);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameTxt;
        public TextView postTitleTxt;
        public ImageView postImageView;
        public TextView postDescriptionTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.userNameTextView);
            postTitleTxt = itemView.findViewById(R.id.postTitleTextView);
            postImageView = itemView.findViewById(R.id.postedImageView);
            postDescriptionTxt = itemView.findViewById(R.id.postDescriptionTextView);

        }
    }
}
