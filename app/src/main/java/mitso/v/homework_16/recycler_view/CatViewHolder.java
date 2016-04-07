package mitso.v.homework_16.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import mitso.v.homework_16.R;

public class CatViewHolder extends RecyclerView.ViewHolder {

    public ImageView        mImageView_CatImage;
    public RelativeLayout   mRelativeLayout_CatCard;

    public CatViewHolder(View itemView) {
        super(itemView);
        mImageView_CatImage = (ImageView) itemView.findViewById(R.id.iv_CatImage_CC);
        mRelativeLayout_CatCard = (RelativeLayout) itemView.findViewById(R.id.rl_CatCard_CC);
    }
}