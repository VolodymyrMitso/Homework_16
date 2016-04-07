package mitso.v.homework_16.recycler_view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mitso.v.homework_16.R;
import mitso.v.homework_16.enums.TypeRequest;
import mitso.v.homework_16.interfaces.IEventHandler;
import mitso.v.homework_16.interfaces.IResponseListener;
import mitso.v.homework_16.models.Cat;
import mitso.v.homework_16.utils.AsyncRequest;

public class CatAdapter extends RecyclerView.Adapter<CatViewHolder> {

    private ArrayList<Cat>  mCatList;
    private IEventHandler   mIEventHandler;

    public CatAdapter(ArrayList<Cat> mCatList) {
        this.mCatList = mCatList;
    }

    @Override
    public void onBindViewHolder(final CatViewHolder holder, final int position) {
        Cat cat = mCatList.get(position);

        if (cat.getUrl() == null)
            holder.mImageView_CatImage.setImageResource(R.drawable.ic_cat);
        else
            (new AsyncRequest<Bitmap>()
                    .typeRequest(TypeRequest.GET)
                    .classType(Bitmap.class)
                    .responseListener(new IResponseListener<Bitmap>() {
                        @Override
                        public void onFinish(boolean isSuccess, Bitmap response) {
                            holder.mImageView_CatImage.setImageBitmap(response);
                        }
                    }))
                    .execute(mCatList.get(position).getUrl());

        holder.mRelativeLayout_CatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIEventHandler.catOnClick(position);
            }
        });
    }

    @Override
    public CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_card, parent, false);
        return new CatViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mCatList.size();
    }

    public void setIEventHandler(IEventHandler mIEventHandler) {
        this.mIEventHandler = mIEventHandler;
    }

    public void releaseIEventHandler() {
        this.mIEventHandler = null;
    }
}
