package com.example.hp.tiptip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView  leftHead;
        ImageView rightHead;


        // view表示父类的布局，用其获取子项
        public ViewHolder(View view) {
            super(view);
            leftLayout =  view.findViewById(R.id.left_layout);
            rightLayout =  view.findViewById(R.id.right_layout);
            leftMsg =  view.findViewById(R.id.left_msg);
            rightMsg =  view.findViewById(R.id.right_msg);
            leftHead =  view.findViewById(R.id.head_left);
            rightHead  =  view.findViewById(R.id.head_right);

        }
    }

    public MsgAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    /**
     * &#x521b;&#x5efa; ViewHolder &#x52a0;&#x8f7d; RecycleView &#x5b50;&#x9879;&#x7684;&#x5e03;&#x5c40;
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 为 RecycleView 子项赋值
     * 赋值通过 position 判断子项位置
     * 当子项进入界面时执行
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftHead.setVisibility(View.VISIBLE);
            holder.rightHead.setVisibility(View.GONE);
        } else if (msg.getType() == Msg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightHead.setVisibility(View.VISIBLE);
            holder.leftHead.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();

    }

}
