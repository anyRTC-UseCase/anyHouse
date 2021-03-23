package org.ar.anyhouse.utils

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


/**
 * 描述 : RecyclerView GridLayoutManager 等间距。
 *
 *
 * 等间距需满足两个条件：
 * 1.各个模块的大小相等，即 各列的left+right 值相等；
 * 2.各列的间距相等，即 前列的right + 后列的left = 列间距；
 *
 *
 * 在[.getItemOffsets] 中针对 outRect 的left 和right 满足这两个条件即可
 *
 *
 * 作者 : shiguotao
 * 版本 : V1
 * 创建时间 : 2020/3/19 4:54 PM
 */
class GridSpaceItemDecoration

 (
        private val mSpanCount: Int, //行间距
        private val mRowSpacing: Int, // 列间距
        private val mColumnSpacing: Int) : ItemDecoration() {
    private val TAG = "GridSpaceItemDecoration"

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view); // 获取view 在adapter中的位置。
        val column = position % mSpanCount; // view 所在的列

        outRect.left = column * mColumnSpacing / mSpanCount; // column * (列间距 * (1f / 列数))
        outRect.right = mColumnSpacing - (column + 1) * mColumnSpacing / mSpanCount; // 列间距 - (column + 1) * (列间距 * (1f /列数))


        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
        if (position >= mSpanCount) {
            outRect.top = mRowSpacing; // item top
        }

    }


}
