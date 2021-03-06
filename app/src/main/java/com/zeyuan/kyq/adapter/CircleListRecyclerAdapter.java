package com.zeyuan.kyq.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.zeyuan.kyq.Entity.ForumBaseEntity;
import com.zeyuan.kyq.Entity.HomePageEntity;
import com.zeyuan.kyq.R;
import com.zeyuan.kyq.biz.Factory;
import com.zeyuan.kyq.utils.Const;
import com.zeyuan.kyq.utils.DataUtils;
import com.zeyuan.kyq.utils.DensityUtil;
import com.zeyuan.kyq.utils.ExceptionUtils;
import com.zeyuan.kyq.utils.IntegerVersionSignature;
import com.zeyuan.kyq.utils.OtherUtils;
import com.zeyuan.kyq.utils.UiUtils;
import com.zeyuan.kyq.view.ForumDetailActivity;
import com.zeyuan.kyq.view.InfoCenterActivity;
import com.zeyuan.kyq.view.ShowPhotoActivity;
import com.zeyuan.kyq.widget.CircleImageView;
import com.zeyuan.kyq.widget.MyGridView;
import com.zeyuan.kyq.widget.RadiusBackgroundSpan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19.
 */
public class CircleListRecyclerAdapter extends BaseRecyclerAdapter<CircleListRecyclerAdapter.CircleListViewHolder> {

    private Context context;
    private List<ForumBaseEntity> list;
    private Map<String, String> circleValues;

    public CircleListRecyclerAdapter(Context context, List<ForumBaseEntity> list) {
        this.context = context;
        if (list == null) list = new ArrayList<>();
        this.list = list;
        circleValues = (Map<String, String>) Factory.getData(Const.N_DataCircleValues);
    }

    @Override
    public CircleListViewHolder getViewHolder(View view) {
        return new CircleListViewHolder(view, false);
    }

    @Override
    public CircleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_forum_base_recycler, parent, false);
        return new CircleListViewHolder(v, viewType, true);
    }

    @Override
    public void onBindViewHolder(CircleListViewHolder vh, int position, boolean isItem) {

        try {
            final ForumBaseEntity entity = list.get(position);

            vh.name.setText(TextUtils.isEmpty(entity.getAuthor()) ? "" : entity.getAuthor());
            vh.time.setText(DataUtils.showFormatTime(entity.getDateLine()));
            vh.title.setText(TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
            if ("1".equals(entity.getType())) {
                vh.like.setVisibility(View.VISIBLE);
                vh.like.setText(TextUtils.isEmpty(entity.getLikeNum()) ? "0" : entity.getLikeNum());
            } else {
                vh.like.setVisibility(View.GONE);
            }

            vh.reply.setText(TextUtils.isEmpty(entity.getReplyNum()) ? "0" : entity.getReplyNum());

            if (TextUtils.isEmpty(entity.getSummary())) {
                vh.content.setText("");
                vh.content.setVisibility(View.GONE);
            } else {
                vh.content.setText(entity.getSummary());
                vh.content.setVisibility(View.VISIBLE);
            }
            //设置圈子数据与背景
            String circle = "";
            SpannableStringBuilder ssb;
            final List<String> temp = entity.getCircleId();
            if (temp != null && temp.size() > 0) {
                ssb = new SpannableStringBuilder();
                for (final String str : temp) {
                    String c = "  " + getCircleValues(str) + " >   ";
                    ssb.append(c);
                    ssb.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            HomePageEntity homePageEntity = new HomePageEntity();
                            homePageEntity.setSkiptype("5");
                            homePageEntity.setSign_a(str);
                            UiUtils.toMenuJump(context, homePageEntity, null, false, null);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);// 设置文字下划线不显示
                            ds.setColor(ContextCompat.getColor(context, R.color.text_blue));// 设置字体颜色
                        }
                    }, circle.length(), circle.length() + c.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.setSpan(new RadiusBackgroundSpan(ContextCompat.getColor(context, R.color.tag_bg), DensityUtil.dip2px(context, 2),DensityUtil.dip2px(context, 3)), circle.length(), circle.length() + c.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    circle += c;
                }

                vh.circle.setMovementMethod(LinkMovementMethod.getInstance());
                vh.circle.setText(ssb);
                vh.v_circle.setVisibility(View.VISIBLE);
            } else {
                vh.circle.setText("");
                vh.v_circle.setVisibility(View.INVISIBLE);
            }
//            String circle = "";
//            List<String> temp = entity.getCircleId();
//            if (temp != null && temp.size() > 0) {
//                for (String str : temp) {
//                    circle += "  #" + getCircleValues(str);
//                }
//                vh.v_circle.setVisibility(View.VISIBLE);
//            } else {
//                vh.v_circle.setVisibility(View.INVISIBLE);
//            }
//            vh.circle.setText(circle);

            String posttype = entity.getPosttype();
            if (!TextUtils.isEmpty(posttype)) {
                int t = Integer.valueOf(posttype);
                if (t != 0 && (t & 2) == 2) {
                    vh.cream.setVisibility(View.VISIBLE);
                } else {
                    vh.cream.setVisibility(View.GONE);
                }
                if (t != 0 && (t & 1) == 1) {
                    vh.top.setVisibility(View.VISIBLE);
                } else {
                    vh.top.setVisibility(View.GONE);
                }
            } else {
                vh.cream.setVisibility(View.GONE);
                vh.top.setVisibility(View.GONE);
            }

            String url = entity.getHeadImgUrl();
            if (TextUtils.isEmpty(url)) {
                vh.civ.setImageResource(R.mipmap.default_avatar);
            } else {
                Glide.with(context).load(url)
                        .signature(new IntegerVersionSignature(1))
                        .error(R.mipmap.default_avatar)
                        .into(vh.civ);
            }

            vh.civ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!OtherUtils.isEmpty(entity.getOwnerID())) {
                        context.startActivity(new Intent(context, InfoCenterActivity.class)
                                .putExtra(Const.InfoCenterID, entity.getOwnerID()));
                    }
                }
            });

            final List<String> imgs = entity.getPic();

            if (imgs != null && imgs.size() > 0) {
                vh.layout.removeAllViews();
                int f = imgs.size() > 3 ? 3 : imgs.size();
                for (int i = 0; i < f; i++) {
                    final int n = i;
                    View v = View.inflate(context, R.layout.item_forum_small_gv, null);
                    TextView tv = (TextView) v.findViewById(R.id.tv);
                    ImageView iv = (ImageView) v.findViewById(R.id.iv);
                    if (i == f - 1 && f != 1) {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText("共" + imgs.size() + "张");
                    } else {
                        tv.setVisibility(View.GONE);
                    }
                    try {
                        Glide.with(context).load(imgs.get(i)).signature(new IntegerVersionSignature(1))
                                .error(R.mipmap.loading_fail).into(iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, ShowPhotoActivity.class)
                                        .putExtra("list", (Serializable) imgs)
                                        .putExtra("position", n));
                            }
                        });
                    } catch (Exception e) {

                    }
                    vh.layout.addView(v);
                }
                vh.layout.setVisibility(View.VISIBLE);

            } else {
                vh.layout.removeAllViews();
                vh.layout.setVisibility(View.GONE);
            }

            vh.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!OtherUtils.isEmpty(entity.getIndex()))
                            context.startActivity(new Intent(context, ForumDetailActivity.class)
                                    .putExtra(Const.FORUM_ID, entity.getIndex())
                                    .putExtra(Const.AUTHORID, entity.getOwnerID()));
                    } catch (Exception e) {
                        ExceptionUtils.ExceptionToUM(e, context, "CircleListRecyclerAdapter");
                    }
                }
            });

        } catch (Exception e) {
            ExceptionUtils.ExceptionSend(e, "getView");
        }

    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    private String getCircleValues(String id) {
        if (TextUtils.isEmpty(id)) return "";
        if (circleValues == null) return "其他圈子";
        String temp = circleValues.get(id);
        if (TextUtils.isEmpty(temp)) {
            return "其他圈子";
        } else {
            return temp;
        }
    }

    public void update(List<ForumBaseEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class CircleListViewHolder extends RecyclerView.ViewHolder {

        MyGridView gv;
        CircleImageView civ;
        TextView name, time, title, content, circle, like, reply;
        ImageView cream, top;
        LinearLayout layout;
        View v_circle, v;

        public CircleListViewHolder(View itemView, boolean isItem) {
            super(itemView);
            init(itemView, -1, isItem);
        }

        public CircleListViewHolder(View itemView, int viewType, boolean isItem) {
            super(itemView);
            init(itemView, viewType, isItem);
        }

        private void init(View itemView, int viewType, boolean isItem) {
            if (isItem) {
                v = itemView;
                civ = (CircleImageView) itemView.findViewById(R.id.civ);
                name = (TextView) itemView.findViewById(R.id.name);
                time = (TextView) itemView.findViewById(R.id.time);
                title = (TextView) itemView.findViewById(R.id.title);
                content = (TextView) itemView.findViewById(R.id.content);
                reply = (TextView) itemView.findViewById(R.id.reply);
                like = (TextView) itemView.findViewById(R.id.like);
                circle = (TextView) itemView.findViewById(R.id.circle);
                cream = (ImageView) itemView.findViewById(R.id.iv_cream);
                top = (ImageView) itemView.findViewById(R.id.iv_top);
                layout = (LinearLayout) itemView.findViewById(R.id.layout);
                v_circle = itemView.findViewById(R.id.v_circle);
            }
        }

    }
}
