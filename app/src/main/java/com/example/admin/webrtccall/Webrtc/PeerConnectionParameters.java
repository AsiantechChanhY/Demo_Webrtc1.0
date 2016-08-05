package com.example.admin.webrtccall.Webrtc;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.admin.webrtccall.R;
import com.example.admin.webrtccall.model.RtcItem;

import java.util.List;

public class PeerConnectionParameters extends RecyclerView.Adapter<PeerConnectionParameters.ViewHolder> {
    public final boolean videoCallEnabled;
    public final boolean loopback;
    public final int videoWidth;
    public final int videoHeight;
    public final int videoFps;
    public final int videoStartBitrate;
    public final String videoCodec;
    public final boolean videoCodecHwAcceleration;
    public final int audioStartBitrate;
    public final String audioCodec;
    public final boolean cpuOveruseDetection;

    private int paddingLeft = 0;
    private int columns;
    private LayoutInflater inflater;
    private OnAdapterEventListener adapterListener;
    private Context context;
    private List<RtcItem> opponents;

    public PeerConnectionParameters(Context context, List<RtcItem> opponents, boolean videoCallEnabled, boolean loopback,
                                    int videoWidth, int videoHeight, int videoFps, int videoStartBitrate,
                                    String videoCodec, boolean videoCodecHwAcceleration,
                                    int audioStartBitrate, String audioCodec, boolean cpuOveruseDetection) {

        this.context = context;
        this.opponents = opponents;
        this.videoCallEnabled = videoCallEnabled;
        this.loopback = loopback;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;
        this.videoStartBitrate = videoStartBitrate;
        this.videoCodec = videoCodec;
        this.videoCodecHwAcceleration = videoCodecHwAcceleration;
        this.audioStartBitrate = audioStartBitrate;
        this.audioCodec = audioCodec;
        this.cpuOveruseDetection = cpuOveruseDetection;
        this.inflater = LayoutInflater.from(context);
    }


    private void setPadding(int itemMargin) {
        int allCellWidth = (videoWidth + (itemMargin * 2)) * columns;
        if ((allCellWidth < videoWidth) && ((videoWidth - allCellWidth) > (itemMargin * 2))) { //set padding if it makes sense to do it
            paddingLeft = (videoWidth - allCellWidth) / 2;
        }
    }

    public void setAdapterListener(OnAdapterEventListener adapterListener) {
        this.adapterListener = adapterListener;
    }


    @Override
    public PeerConnectionParameters.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_opponent_from_call, null);
        v.findViewById(R.id.innerLayout).setLayoutParams(new FrameLayout.LayoutParams(videoWidth, videoHeight));
        if (paddingLeft != 0) {
            v.setPadding(paddingLeft, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
        }
        ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PeerConnectionParameters.ViewHolder holder, int position) {
        if (position == (opponents.size() -1)){
            adapterListener.OnBindLastViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
//        return opponents.size() ;
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnAdapterEventListener {
        public void OnBindLastViewHolder(ViewHolder holder, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView connectionStatus;
        GLSurfaceView opponentView;

        public ViewHolder(View itemView) {
            super(itemView);

//            connectionStatus = (TextView) itemView.findViewById(R.id.connectionStatus);
            opponentView = (GLSurfaceView) itemView.findViewById(R.id.opponentView);
        }

        public GLSurfaceView getOpponentView() {
            return opponentView;
        }
    }
}