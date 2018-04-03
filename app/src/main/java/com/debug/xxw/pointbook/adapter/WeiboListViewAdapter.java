package com.debug.xxw.pointbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.model.Weibo;
import com.debug.xxw.pointbook.viewmodel.CircleImageView;
import com.debug.xxw.pointbook.viewmodel.NineGridTestLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Crazyfzw on 2016/5/19.
 */

public class WeiboListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Weibo> mList;

    public WeiboListViewAdapter(Context context, List<Weibo> list) {
        this.context = context;
        mList = list;
    }

    public void addWeiboToList(Weibo w) {
        mList.add(0, w);
        notifyItemInserted(0);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommunityViewHolder mViewHolder = ((CommunityViewHolder) holder);
        Picasso.with(context).load(R.drawable.defaulthead).error(R.mipmap.ic_launcher).into(mViewHolder.userPic);
        mViewHolder.username.setText(mList.get(position).getUsername());
        mViewHolder.publicTime.setText(mList.get(position).getPublicTime());
        mViewHolder.from.setText(mList.get(position).getFrom());
        mViewHolder.textContent.setText(mList.get(position).getContent());
        mViewHolder.recentLike.setText(mList.get(position).getRecentLike());
        mViewHolder.recentShare.setText(mList.get(position).getRecentShare());
        mViewHolder.recentComment.setText(mList.get(position).getRecentComment());
        mViewHolder.nicePic.setIsShowAll(mList.get(position).getContentImgs().isShowAll);
        mViewHolder.nicePic.setUrlList(mList.get(position).getContentImgs().urlList);
    }

    @Override
    public int getItemCount() {
        return getListSize(mList);
    }

    public void setList(List<Weibo> list) {
        mList = list;
        this.notifyDataSetChanged();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userPic;
        TextView username;
        TextView publicTime;
        TextView from;
        TextView textContent;
        NineGridTestLayout nicePic;
        TextView recentLike;
        TextView recentShare;
        TextView recentComment;

        CommunityViewHolder(View itemView) {
            super(itemView);

            nicePic = itemView.findViewById(R.id.layout_nine_grid);
            userPic = itemView.findViewById(R.id.userPicture);
            username = itemView.findViewById(R.id.userName);
            publicTime = itemView.findViewById(R.id.publicTime);
            from = itemView.findViewById(R.id.from);
            textContent = itemView.findViewById(R.id.textContent);
            recentLike = itemView.findViewById(R.id.recent_like);
            recentShare = itemView.findViewById(R.id.recent_share);
            recentComment = itemView.findViewById(R.id.recent_comment);

        }
    }

    private int getListSize(List<Weibo> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

}
