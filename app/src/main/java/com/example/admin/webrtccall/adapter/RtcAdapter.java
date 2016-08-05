//package com.example.admin.webrtccall.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.example.admin.webrtccall.R;
//import com.example.admin.webrtccall.model.RtcItem;
//import com.example.admin.webrtccall.view.RTCGLVideoView;
//
//import java.util.List;
//
///**
// * Created by Admin on 8/4/2016.
// */
//public class RtcAdapter extends RecyclerView.Adapter<RtcAdapter.ViewHolder>{
//
//    private static final String TAG = RtcAdapter.class.getSimpleName();
//    private final int itemHeight;
//    private final int itemWidth;
//    private int paddingLeft = 0;
//
//    private Context context;
//    private List<RtcItem> opponents;
//    private int gridWidth;
//    private boolean showVideoView;
//    private LayoutInflater inflater;
//    private int columns;
//    private OnAdapterEventListener adapterListener;
//
//    public RtcAdapter(Context context, List<RtcItem> users, int width, int height,
//                                    int gridWidth, int columns, int itemMargin,
//                                    boolean showVideoView) {
//        this.context = context;
//        this.opponents = users;
//        this.gridWidth = gridWidth;
//        this.columns = columns;
//        this.showVideoView = showVideoView;
//        this.inflater = LayoutInflater.from(context);
//        itemWidth = width;
//        itemHeight = height;
//        setPadding(itemMargin);
//    }
//
//
//    private void setPadding(int itemMargin) {
//        int allCellWidth = (itemWidth + (itemMargin * 2)) * columns;
//        if ((allCellWidth < gridWidth) && ((gridWidth - allCellWidth) > (itemMargin * 2))) { //set padding if it makes sense to do it
//            paddingLeft = (gridWidth - allCellWidth) / 2;
//        }
//    }
//
//    public void setAdapterListener(OnAdapterEventListener adapterListener) {
//        this.adapterListener = adapterListener;
//    }
//
//    @Override
//    public RtcAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = inflater.inflate(R.layout.list_item_opponent_from_call, null);
//        v.findViewById(R.id.innerLayout).setLayoutParams(new FrameLayout.LayoutParams(itemWidth, itemHeight));
//        if (paddingLeft != 0) {
//            v.setPadding(paddingLeft, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
//        }
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//    }
//
//    @Override
//    public void onBindViewHolder(RtcAdapter.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return opponents.size();
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public interface OnAdapterEventListener {
//        public void OnBindLastViewHolder(ViewHolder holder, int position);
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder{
//        TextView connectionStatus;
//        RTCGLVideoView opponentView;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            connectionStatus = (TextView) itemView.findViewById(R.id.connectionStatus);
//            opponentView = (RTCGLVideoView) itemView.findViewById(R.id.opponentView);
//        }
//    }
//}
