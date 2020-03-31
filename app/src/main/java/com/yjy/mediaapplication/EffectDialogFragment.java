package com.yjy.mediaapplication;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjy.camera.Filter.BlackWhiteFilter;
import com.yjy.camera.Filter.BlurFilter;
import com.yjy.camera.Filter.LPSFilter;
import com.yjy.camera.Filter.ShaperFilter;
import com.yjy.camera.Filter.SobelFilter;
import com.yjy.camera.Filter.WaterFilter;
import com.yjy.camera.UI.ICameraFragment;
import com.yjy.camera.Utils.CameraUtils;
import com.yjy.mediaapplication.bean.FilterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EffectDialogFragment extends DialogFragment {


    private RecyclerView mRecyclerView;
    private List<FilterModel> mList;

    private ICameraFragment mCamera;

    private static final String DATA = "data";


    public static EffectDialogFragment  newInstance(){
        EffectDialogFragment fragment = new EffectDialogFragment();

        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(this.getActivity(), R.style.dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM|Gravity.FILL_HORIZONTAL;
        params.width =ViewGroup.LayoutParams.MATCH_PARENT;
        params.windowAnimations =R.style.dialogAnim;
        dialog.getWindow().setAttributes(params);
        return dialog;
    }

    public void setCamera(ICameraFragment mCamera) {
        this.mCamera = mCamera;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup)
                inflater.inflate(R.layout.effect_layout,container,false);
        mRecyclerView = viewGroup.findViewById(R.id.list);

        mList = ((MainActivity)getActivity()).getData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false));

        Adapter adapter = new Adapter(getContext(),mList,mCamera);
        mRecyclerView.setAdapter(adapter);
        return viewGroup;
    }







    private static class Adapter extends RecyclerView.Adapter<HorizontalViewHolder>{

        private ICameraFragment mCamera;
        private Context mContext;
        private List<FilterModel> mList;
        private Adapter(Context context, List<FilterModel> list,ICameraFragment cameraFragment){
            mContext = context;
            mList = list;
            mCamera = cameraFragment;
        }

        @NonNull
        @Override
        public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.text_layout, viewGroup, false);
            return new HorizontalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HorizontalViewHolder viewHolder, final int i) {
            viewHolder.mTv.setText(mList.get(i).getName());
            if(mList.get(i).isSelect()){
                viewHolder.mSelectIv.setImageResource(R.drawable.ic_select);
            }else {
                viewHolder.mSelectIv.setImageResource(R.drawable.ic_non_select);
            }
            viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCamera!=null){
                        if(!mList.get(i).isSelect()){
                            if(mList.get(i).getFilter() instanceof WaterFilter){
                                final WaterFilter filter = (WaterFilter) mList.get(i).getFilter();
                                final TextView view = new TextView(mContext);
                                view.setText("edit by yjy239");
                                view.setTextSize(16);
                                view.setTextColor(Color.BLACK);
                                filter.resetView(view);
                                mCamera.postEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCamera.addFilter(mList.get(i).getFilter());
                                    }
                                });

                            }


                        }else {
                            mCamera.removeFilter(mList.get(i).getFilter());
                        }

                        mList.get(i).setSelect(!mList.get(i).isSelect());
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private static class HorizontalViewHolder extends RecyclerView.ViewHolder {

        TextView mTv;
        ConstraintLayout mContent;
        ImageView mSelectIv;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.tv);
            mContent = itemView.findViewById(R.id.content);
            mSelectIv = itemView.findViewById(R.id.select_iv);

        }
    }


}
