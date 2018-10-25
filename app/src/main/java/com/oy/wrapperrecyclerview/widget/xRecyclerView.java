package com.oy.wrapperrecyclerview.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.oy.wrapperrecyclerview.R;

/**
 * Author   : xiaoyu
 * Date     : 2018/10/11 下午3:14
 * Version  : v1.0.0
 * Describe : 下拉刷新 上滑加载更多的RecyclerView, 只适配了LinearLayoutManager, Grid和瀑布流不支持
 * <p>
 * 1. down refresh
 * 2. up load more
 * <p>
 * 使用方式：
 *     1. 布局文件使用同原生RecyclerView
 *        <com.oy.wrapperrecyclerview.widget.xRecyclerView
 *         android:id="@+id/gank_recycler_view"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent"/>
 *
 *     2. Adapter需要继承自{@link xAdapter},同时实现3个方法：{@link xAdapter#getxItemCount()},{@link xAdapter#onCreatexViewHolder(ViewGroup, int)}
 *         和{@link xAdapter#onBindxViewHolder(RecyclerView.ViewHolder, int)}。
 *
 *     3. 通过{@link #setListener(xAdapterListener)}来监听刷新回调和加载更多回调。
 *
 *     4. 通过{@link #stopRefreshing()}或{@link #stopLoadingMore()}来更新xRecyclerView的状态。
 *
 *     5. 刷新列表数据调用{@link xAdapter#notifyDataSetChanged()}
 *
 * 示例
 * 1. xAdapter实现示例见{@link com.oy.wrapperrecyclerview.adapter.GankNewsAdapter}
 * 2. xRecyclerView使用示例见{@link com.oy.wrapperrecyclerview.GankFragment}
 *
 * 问题
 * 当首次加载数据不足以填满屏幕，此时'下拉刷新'也会触发'上拉加载更多'
 *
 * 如有问题: oynix@foxmail.com
 * 再次感谢
 */
public class xRecyclerView extends SwipeRefreshLayout {

    private RecyclerView mRecyclerView;
    private boolean mRefreshing = false;
    private boolean mLoadingMore = false;
    private xAdapterListener mListener;
    private xAdapter mXAdapter;

    public xRecyclerView(Context context) {
        super(context);
        initView();
    }

    public xRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) View.inflate(getContext(), R.layout.vertical_recycler_view, null);
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setOnRefreshListener(() -> {
            if (mRefreshing || mLoadingMore) {
                setRefreshing(!mLoadingMore);
                return;
            }
            mRefreshing = true;
            if (mListener != null) {
                mListener.startRefresh();
            }
        });
    }

    public void setItemDecoration(@DrawableRes int decorationRes) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), decorationRes);
        if (drawable != null) {
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            decoration.setDrawable(drawable);
            mRecyclerView.addItemDecoration(decoration);
        }
    }

    /** 设置adapter */
    public void setAdapter(xAdapter adapter) {
        mXAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new xScrollListener(mRecyclerView, adapter));
    }

    /** 设置'下拉刷新'和'上滑加载更多'的触发监听 */
    public void setListener(xAdapterListener l) {
        mListener = l;
    }

    public void startRefreshing() {
        mRefreshing = true;
        setRefreshing(true);
    }

    /** 刷新完成后调用 */
    public void stopRefreshing() {
        setRefreshing(false);
        mRefreshing = false;
        // 临时patch
        if (mLoadingMore) {
            mLoadingMore = false;
        }
        mXAdapter.hideLoadMoreItem();
    }

    /**
     * 加载更多完成后调用
     */
    public void stopLoadingMore() {
        if (mXAdapter != null) {
            mXAdapter.switchLoadMoreState(false);
            mLoadingMore = false;
            mXAdapter.hideLoadMoreItem();

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (linearLayoutManager == null) {
                return;
            }
            int lp = linearLayoutManager.findLastVisibleItemPosition();

            if (mXAdapter.getItemCount() - 1 == lp) {
                View child = linearLayoutManager.findViewByPosition(mXAdapter.getItemCount() - 2);
                if (child == null) {
                    return;
                }
                // mRecyclerView topMargin=0 childPosition=0, mRecyclerView.getBottom即为height
                int inHeight = mRecyclerView.getBottom() - mRecyclerView.getPaddingBottom();
                int childBottom = child.getBottom();
                int deltaY = inHeight - childBottom;
                mRecyclerView.smoothScrollBy(0, -deltaY);
            }
        }
    }

    public static abstract class xAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private static final int ITEM_TYPE_LOADMORE = 1 << 12;
        private RecyclerView.ViewHolder mLoadMoreItemHolder;

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
            if (itemType == ITEM_TYPE_LOADMORE) {
                return (VH) (mLoadMoreItemHolder = new RecyclerView.ViewHolder(createLoadMoreView(viewGroup)) {
                });
            }
            return onCreatexViewHolder(viewGroup, itemType);
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            // 非'加载更多'item，进行绑定
            if (i != getItemCount() - 1) {
                onBindxViewHolder(vh, i);
            }
        }

        @Override
        public int getItemCount() {
            // item数量应该等于数据item加上底部加载提示item
            return getxItemCount() + 1;
        }

        private View createLoadMoreView(View view) {
            return View.inflate(view.getContext(), R.layout.item_load_more, null);
        }

        // 显示'加载更多'item
        void showLoadMoreItem() {
            if (mLoadMoreItemHolder == null) {
                return;
            }
            if (mLoadMoreItemHolder.itemView.getVisibility() != View.VISIBLE) {
                mLoadMoreItemHolder.itemView.setVisibility(VISIBLE);
            }
        }

        // 隐藏'加载更多'item
        void hideLoadMoreItem() {
            if (mLoadMoreItemHolder == null) {
                return;
            }
            if (mLoadMoreItemHolder.itemView.getVisibility() != View.INVISIBLE) {
                mLoadMoreItemHolder.itemView.setVisibility(INVISIBLE);
            }
        }

        // '加载更多'item是否显示
        boolean isLoadMoreShown() {
            return mLoadMoreItemHolder != null && mLoadMoreItemHolder.itemView.getVisibility() == View.VISIBLE;
        }

        // 显示 '加载更多' 或者 '努力加载中'
        void switchLoadMoreState(boolean loading) {
            if (mLoadMoreItemHolder == null) {
                return;
            }
            mLoadMoreItemHolder.itemView.findViewById(R.id.item_load_tv).setVisibility(loading ? GONE : VISIBLE);
            mLoadMoreItemHolder.itemView.findViewById(R.id.item_load_pb).setVisibility(loading ? VISIBLE : GONE);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return ITEM_TYPE_LOADMORE;
            }
            return getxItemViewType(position);
        }

        /**
         * 获取数据item数量，不包含加载提示item
         */
        protected abstract int getxItemCount();

        /**
         * 创建xViewHolder
         */
        protected abstract VH onCreatexViewHolder(ViewGroup viewGroup, int itemType);

        /**
         * 将item数据和xViewHolder绑定
         */
        protected abstract void onBindxViewHolder(VH holder, int position);

        /**
         * 获取item的类型
         */
        protected int getxItemViewType(int position) {
            return 0;
        }
    }

    private class xScrollListener extends RecyclerView.OnScrollListener {

        private xAdapter mAdapter;
        private RecyclerView.LayoutManager mManager;

        xScrollListener(RecyclerView recyclerView, xAdapter adapter) {
            mManager = recyclerView.getLayoutManager();
            mAdapter = adapter;
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (!(mManager instanceof LinearLayoutManager)) {
                return;
            }
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mManager;
            int lastCompletePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mLoadingMore) {
                    return;
                }
                if (lastCompletePosition == mAdapter.getItemCount() - 2) {
                    View child = linearLayoutManager.findViewByPosition(lastCompletePosition);
                    if (child == null)
                        return;
                    int deltaY = (recyclerView.getBottom() - recyclerView.getPaddingBottom()) - child.getBottom();
                    if (deltaY > 0) {
                        recyclerView.smoothScrollBy(0, -deltaY);
                    }
                } else if (lastCompletePosition == mAdapter.getItemCount() - 1) {
                    mLoadingMore = true;
                    mAdapter.switchLoadMoreState(true);
                    if (mListener != null) {
                        mListener.startLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (!(mManager instanceof LinearLayoutManager) || mLoadingMore) {
                return;
            }
            int newState = recyclerView.getScrollState();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mManager;
            int lastCompletePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                if (lastVisibleItemPosition == mAdapter.getItemCount() - 1 && lastCompletePosition == mAdapter.getItemCount() - 2) {
                    mAdapter.showLoadMoreItem();
                }
            }
        }
    }

    public interface xAdapterListener {
        void startRefresh();

        void startLoadMore();
    }
}
