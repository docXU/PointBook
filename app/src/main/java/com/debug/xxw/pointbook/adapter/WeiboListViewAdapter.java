package com.debug.xxw.pointbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.model.Weibo;
import com.debug.xxw.pointbook.net.WeiboNetter;
import com.debug.xxw.pointbook.viewmodel.CircleImageView;
import com.debug.xxw.pointbook.viewmodel.NineGridTestLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeiboListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static String head_view = "head_view";
    private Context context;
    private List<Weibo> mList;
    private OnItemClickListener onLikeClickListener;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CommunityViewHolder mViewHolder = ((CommunityViewHolder) holder);
        if (!mList.get(position).getHeadimg().isEmpty()) {
            Picasso.with(context).load(mList.get(position).getHeadimg()).error(R.mipmap.ic_launcher).into(mViewHolder.userPic);
        } else {
            Picasso.with(context).load(R.drawable.defaulthead).error(R.mipmap.ic_launcher).into(mViewHolder.userPic);
        }
        mViewHolder.nicePic.setIsShowAll(mList.get(position).getContentImgs().isShowAll);
        mViewHolder.nicePic.setUrlList(mList.get(position).getContentImgs().urlList);
        mViewHolder.username.setText(mList.get(position).getUsername());
        mViewHolder.publicTime.setText(mList.get(position).getPublicTime());
        mViewHolder.msglevel.setText(mList.get(position).getMsglevel());
        mViewHolder.textContent.setText(mList.get(position).getContent());
        mViewHolder.recentLike.setText(mList.get(position).getRecentLike());
        mViewHolder.recentLow.setText(mList.get(position).getRecentLow());
        mViewHolder.recentComment.setText(mList.get(position).getRecentComment());
        if (onLikeClickListener != null) {
            mViewHolder.recentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onLikeClickListener.onClick(position, WeiboNetter.like_counter);
                }
            });
            mViewHolder.recentLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onLikeClickListener.onClick(position, WeiboNetter.low_counter);
                }
            });
            mViewHolder.userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLikeClickListener.onClick(position, head_view);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public void setList(List<Weibo> list) {
        mList = list;
        this.notifyDataSetChanged();
    }

    public void changeCounter(int position, String count, String who) {
        if (who.equals(WeiboNetter.like_counter)) {
            mList.get(position).setRecentLike(count);
        } else if (who.equals(WeiboNetter.low_counter)) {
            mList.get(position).setRecentLow(count);
        } else {
            mList.get(position).setRecentComment(count);
        }
        this.notifyDataSetChanged();
    }

    public void setOnLikeClickListener(OnItemClickListener onItemClickListener) {
        this.onLikeClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int positionm, String who);
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userPic;
        TextView username;
        TextView publicTime;
        TextView msglevel;
        TextView textContent;
        NineGridTestLayout nicePic;
        TextView recentLike;
        TextView recentLow;
        TextView recentComment;

        CommunityViewHolder(View itemView) {
            super(itemView);

            nicePic = itemView.findViewById(R.id.layout_nine_grid);
            userPic = itemView.findViewById(R.id.userPicture);
            username = itemView.findViewById(R.id.userName);
            publicTime = itemView.findViewById(R.id.publicTime);
            msglevel = itemView.findViewById(R.id.from);
            textContent = itemView.findViewById(R.id.textContent);
            recentLike = itemView.findViewById(R.id.recent_like);
            recentLow = itemView.findViewById(R.id.recent_low);
            recentComment = itemView.findViewById(R.id.recent_comment);
        }
    }
}
