package org.ar.anyhouse.weight;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.ar.anyhouse.BuildConfig;
import org.ar.anyhouse.vm.Listener;
import org.ar.anyhouse.vm.Speaker;

import java.util.List;



/**
 * Item的宽度应设为MATCH_PARAENT<br>
 * 应用于RecyclerView的LinearLayoutManager、GridLayoutManager和StaggeredGridLayoutManager，水平方向上固定间距大小，从而使条目宽度自适应。<br>
 * 注意：在对StaggeredGridLayoutManager使用时，应注意可能会出item大小不同，导致凹凸导致item的摆放位置与adapter中位置不同的情况，
 * 这时的间距设置会导致显示错乱。这种情况推荐在Decoration上设置左右都相等的padding,剩余的padding由recyclerview来设置，内部已经实现；<br>
 * <br>
 * 子类可心重写{@link #getVisualPosByAdapterPos(int)}来覆盖默认的理论视觉位置实现。<br>
 *
 * @author : renpeng
 * @since : 2018/9/29
 */
public class AverageGapItemDecoration extends RecyclerView.ItemDecoration {

    private float gapHorizontalDp;
    private float gapVerticalDp;
    private float edgePaddingDp;
    private int gapHSizePx = -1;
    private int gapVSizePx = -1;
    private int edgePaddingPx = -1;
    private int eachItemPaddingH = -1; //每个条目应该在水平方向上加的padding 总大小，即=paddingLeft+paddingRight
    private int spanCount = -1;
    private List<Object> list;
    /**
     * @param gapHorizontalDp 水平间距
     * @param gapVerticalDp   垂直间距
     * @param edgePaddingDp   两端的padding大小
     */
    public AverageGapItemDecoration(float gapHorizontalDp, float gapVerticalDp, float edgePaddingDp, List<Object> list) {
        this.gapHorizontalDp = gapHorizontalDp;
        this.gapVerticalDp = gapVerticalDp;
        this.edgePaddingDp = edgePaddingDp;
        this.list= list;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager rvLayoutManager = parent.getLayoutManager();

        if (rvLayoutManager == null || parent.getAdapter() == null) {
            return;
        }
        if (gapHSizePx < 0 || gapVSizePx < 0) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            parent.getDisplay().getMetrics(displayMetrics);
            gapHSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gapHorizontalDp, displayMetrics);
            gapVSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gapVerticalDp, displayMetrics);
            edgePaddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, edgePaddingDp, displayMetrics);

            if (rvLayoutManager instanceof GridLayoutManager) {
                GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
                spanCount = layoutManager.getSpanCount();
                eachItemPaddingH = (edgePaddingPx * 2 + gapHSizePx * (spanCount - 1)) / spanCount;
            } else if (rvLayoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
                spanCount = layoutManager.getSpanCount();
                eachItemPaddingH = (gapHSizePx * (spanCount - 1)) / spanCount;
                //特殊设置
                int rvPadding = 0;
                if (layoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                    rvPadding = edgePaddingPx - gapHSizePx / 2;
                } else {
                    rvPadding = edgePaddingPx - gapVSizePx / 2;
                }
                rvPadding = rvPadding >= 0 ? rvPadding : 0;
                parent.setPadding(rvPadding, 0, rvPadding, 0);
            } else if (rvLayoutManager instanceof LinearLayoutManager) {
                spanCount = 1;
                eachItemPaddingH = (edgePaddingPx * 2 + gapHSizePx * (spanCount - 1)) / spanCount;
            } else {
                throw new RuntimeException("不支持的LayoutManager");
            }
        }

        int position = parent.getChildAdapterPosition(view);
        if (!handleDecorationAtAdapterPosition(outRect, view, parent, state, position)) {
            if (rvLayoutManager instanceof GridLayoutManager) {
                setGridDecoration(outRect, view, parent, state, position);
            } else if (rvLayoutManager instanceof StaggeredGridLayoutManager) {
                setStaggeredGridDecoration((StaggeredGridLayoutManager) rvLayoutManager, outRect, view, parent, state, position);
            } else if (rvLayoutManager instanceof LinearLayoutManager) {
                spanCount = 1;
                setLinearDecoration((LinearLayoutManager) rvLayoutManager, outRect, view, parent, state, position);
            } else {
                throw new RuntimeException("不支持的LayoutManager");
            }
        }
        if (BuildConfig.DEBUG) {
            Log.d("ItemDecoration", "pos=" + position + "," + outRect.toShortString());
        }
    }

    private void setGridDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state, int position) {
        if (list.size()>position){
           if (list.get(position) instanceof Speaker || list.get(position) instanceof Listener){
                int count = parent.getAdapter().getItemCount();
                outRect.top = gapVSizePx;
                outRect.bottom = 0;
                int visualPos = getVisualPosByAdapterPos(position);
                if (visualPos % spanCount == 1) {
                    //第一列
                    outRect.left = 0;
                    outRect.right = eachItemPaddingH - edgePaddingPx;
                } else if (visualPos % spanCount == 0) {
                    //最后一列
                    outRect.left = 25;
                    outRect.right = edgePaddingPx;
                } else {
                    outRect.left = gapHSizePx - (eachItemPaddingH - edgePaddingPx);
                    outRect.right = eachItemPaddingH - outRect.left;
                }

                if (visualPos - spanCount <= 0) {
                    //第一行
                    outRect.top = edgePaddingPx;
                } else if (isLastRow(visualPos, spanCount, count)) {
                    //最后一行
                    outRect.bottom = edgePaddingPx;
                }
            }
        }


    }

    private void setStaggeredGridDecoration(StaggeredGridLayoutManager layoutManager, Rect outRect, View view, RecyclerView parent, RecyclerView.State state, int position) {
        if (layoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
            int count = parent.getAdapter().getItemCount();

            outRect.top = gapVSizePx;
            outRect.bottom = 0;
            int visualPos = getVisualPosByAdapterPos(position);
            outRect.left = outRect.right = gapHSizePx / 2;

            if (visualPos - spanCount <= 0) {
                //第一行
                outRect.top = edgePaddingPx;
            } else if (isLastRow(visualPos, spanCount, count)) {
                //最后一行
                outRect.bottom = edgePaddingPx;
            }
        } else {
            throw new RuntimeException("暂不支持");
        }
    }

    protected boolean handleDecorationAtAdapterPosition(Rect outRect, View view, RecyclerView parent, RecyclerView.State state, int position) {
        return false;
    }

    protected int getVisualPosByAdapterPos(int position) {
        return position + 1;
    }

    private void setLinearDecoration(LinearLayoutManager layoutManager, Rect outRect, View view, RecyclerView parent, RecyclerView.State state, int position) {
        if (layoutManager.getOrientation() == RecyclerView.VERTICAL) {
            int count = parent.getAdapter().getItemCount();

            outRect.top = gapVSizePx;
            outRect.bottom = 0;
            int visualPos = getVisualPosByAdapterPos(position);
            outRect.left = outRect.right = edgePaddingPx;

            if (visualPos - spanCount <= 0) {
                //第一行
                outRect.top = edgePaddingPx;
            } else if (isLastRow(visualPos, spanCount, count)) {
                //最后一行
                outRect.bottom = edgePaddingPx;
            }
        } else {
            throw new RuntimeException("暂不支持");
        }
    }

    private boolean isLastRow(int visualPos, int spanCount, int sectionItemCount) {
        int lastRowCount = sectionItemCount % spanCount;
        lastRowCount = lastRowCount == 0 ? spanCount : lastRowCount;
        return visualPos > sectionItemCount - lastRowCount;
    }
}
