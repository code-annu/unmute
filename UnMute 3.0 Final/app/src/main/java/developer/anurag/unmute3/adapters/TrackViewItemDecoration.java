package developer.anurag.unmute3.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackViewItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private final int mTop,mRight,mBottom,mLeft;
    public TrackViewItemDecoration(Context context,int mTop,int mRight,int mBottom,int mLeft){
        this.mTop=mTop;
        this.mRight=mRight;
        this.mBottom=mBottom;
        this.mLeft=mLeft;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left=this.mLeft;
        outRect.right=this.mRight;
        outRect.top=this.mTop;
        outRect.bottom=this.mBottom;
    }
}
