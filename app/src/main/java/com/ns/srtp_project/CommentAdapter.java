package com.ns.srtp_project;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/11.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private ArrayList<Comment> mList;
    private Context mContext;;
    public  CommentAdapter(Context mContext,ArrayList<Comment> mList){
        this.mContext=mContext;
        this.mList=mList;
    }

    public void removeData(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.comment_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.nickNmae.setText(String.valueOf(mList.get(position).getNickName()));
        holder.text.setText(mList.get(position).getText());
        holder.place.setText(mList.get(position).getCity()+"市"+mList.get(position).getCounty()+mList.get(position).getStreet());
        holder.date.setText(sdf.format(mList.get(position).getDate()));
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nickNmae,text,place,date;
        public MyViewHolder(View view)
        {
            super(view);
            nickNmae=view.findViewById(R.id.comment_nickname);
            text=view.findViewById(R.id.comment_text);
            place=view.findViewById(R.id.comment_place);
            date=view.findViewById(R.id.comment_date);
        }
    }
}
