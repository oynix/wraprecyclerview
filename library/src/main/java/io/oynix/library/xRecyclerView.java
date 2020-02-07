package io.oynix.library;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Author   : xiaoyu
 * Date     : 2018/10/11 下午3:14
 * Version  : v1.0.0 Describe : 下拉刷新
 * 上滑加载更多的RecyclerView, 只适配了LinearLayoutManager, Grid和瀑布流不支持
 * <p>
 * 1. down refresh 2. up load more
 * <p>
 * 使用方式： 1. 布局文件使用同原生RecyclerView <com.oy.wrapperrecyclerview.widget.xRecyclerView
 * android:id="@+id/gank_recycler_view" android:layout_width="match_parent"
 * android:layout_height="match_parent"/>
 * <p>
 * 2. Adapter需要继承自{@link xAdapter},同时实现3个方法：{@link xAdapter#getxItemCount()},{@link
 * xAdapter#onCreatexViewHolder(ViewGroup, int)} 和{@link xAdapter#onBindxViewHolder(RecyclerView.ViewHolder,
 * int)}。
 * <p>
 * 3. 通过{@link #setListener(xAdapterListener)}来监听刷新回调和加载更多回调。
 * <p>
 * 4. 通过{@link #stopRefreshing()}或{@link #stopLoadingMore()}来更新xRecyclerView的状态。
 * <p>
 * 5. 刷新列表数据调用{@link xAdapter#notifyDataSetChanged()}
 * <p>
 * 问题 当首次加载数据不足以填满屏幕，此时'下拉刷新'也会触发'上拉加载更多'
 * <p>
 * 如有问题: oynix@foxmail.com 再次感谢
 */
public class xRecyclerView extends SwipeRefreshLayout {

    // 空闲，非加载状态
    private static final int STATE_IDLE = 0;
    // 刷新中
    private static final int STATE_REFRESHING = 1;
    // 加载更多中
    private static final int STATE_LOADINGMORE = 2;

    private RecyclerView mRecyclerView;
    private xAdapterListener mListener;
    private xAdapter mXAdapter;
    private int mState = STATE_IDLE;

    public xRecyclerView(Context context) {
        super(context);
        initView();
    }

    public xRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mRecyclerView = new RecyclerView(getContext());
        addView(mRecyclerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setOnRefreshListener(() -> {
            // 只有处于IDLE状态时，才允许刷新
            if (mState != STATE_IDLE) {
                // 如果正在LoadMore，停止刷新动画
                if (mState == STATE_LOADINGMORE)
                    setRefreshing(false);
                return;
            }
            // refresh监听为空，则停止刷新动画，不进行刷新动画
            if (mListener == null) {
                setRefreshing(false);
                return;
            }
            switchState(STATE_REFRESHING);
            mListener.startRefresh();
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

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    /** 设置adapter */
    public void setAdapter(@NonNull xAdapter adapter) {
        mXAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new xScrollListener(mRecyclerView, adapter));
    }

    /** 设置'下拉刷新'和'上滑加载更多'的触发监听 */
    public void setListener(xAdapterListener l) {
        mListener = l;
    }

    /** 手动切换至刷新状态，一般在进入页面首次加载前调用 */
    public void startRefreshing() {
        if (mState != STATE_IDLE)
            return;
        switchState(STATE_REFRESHING);
    }

    /** 刷新完成后调用 */
    public void stopRefreshing() {
        if (mState != STATE_REFRESHING)
            return;
        switchState(STATE_IDLE);
    }

    /** 加载更多完成后调用 */
    public void stopLoadingMore() {
        if (mState != STATE_LOADINGMORE)
            return;
        switchState(STATE_IDLE);
//        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        if (linearLayoutManager == null) {
//            return;
//        }
//        int lp = linearLayoutManager.findLastVisibleItemPosition();
//
//        if (mXAdapter.getItemCount() - 1 == lp) {
//            View child = linearLayoutManager.findViewByPosition(mXAdapter.getItemCount() - 2);
//            if (child == null) {
//                return;
//            }
//            // mRecyclerView topMargin=0 childPosition=0, mRecyclerView.getBottom即为height
//            int inHeight = mRecyclerView.getBottom() - mRecyclerView.getPaddingBottom();
//            int childBottom = child.getBottom();
//            int deltaY = inHeight - childBottom;
//            mRecyclerView.smoothScrollBy(0, -deltaY);
//        }
    }

    // 切换状态，[STATE_IDLE, STATE_REFRESHING, STATE_LOADINGMORE]
    private void switchState(int newState) {
        // out from old state时额外的操作
        if (mState == STATE_LOADINGMORE) {
            // 切换LoadMoreItem状态，并隐藏LoadMoreItem
            mXAdapter.changeLoadMoreState(false);
            mXAdapter.changeLoadMoreVisibility(false);
        } else if (mState == STATE_REFRESHING) {
            // 停止刷新动画
            setRefreshing(false);
        }
        // into new state时额外的操作
        if (newState == STATE_REFRESHING) {
            setRefreshing(true);
        } else if (newState == STATE_LOADINGMORE) {
            mXAdapter.changeLoadMoreState(true);
            mXAdapter.changeLoadMoreVisibility(true);
        }
        // 切换状态
        mState = newState;
    }

    public static abstract class xAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private static final int ITEM_TYPE_LOADMORE = 1 << 12;
        private LoadMoreViewHolder mLoadMoreItemHolder;

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
            if (itemType == ITEM_TYPE_LOADMORE) {
                if (mLoadMoreItemHolder == null) {
                    mLoadMoreItemHolder = createLoadMoreViewHolder(viewGroup);
                    if (mLoadMoreItemHolder == null)
                        mLoadMoreItemHolder = LoadMoreViewHolder.defaultHolder(viewGroup);
                }
                return mLoadMoreItemHolder.getViewHolder();
            }
            return onCreatexViewHolder(viewGroup, itemType);
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            // 非'加载更多'item，进行View和数据的绑定
            if (i != getItemCount() - 1) {
                onBindxViewHolder(vh, i);
            }
        }

        @Override
        public int getItemCount() {
            // item数量应该等于数据item加上底部加载提示item
            return getxItemCount() + 1;
        }

        // 显示/隐藏LoadMoreView
        void changeLoadMoreVisibility(boolean show) {
            if (mLoadMoreItemHolder == null)
                return;
            mLoadMoreItemHolder.changeLoadMoreViewVisibility(show);
        }

        // 加载中状态/非加载状态
        void changeLoadMoreState(boolean loading) {
            if (mLoadMoreItemHolder == null)
                return;
            mLoadMoreItemHolder.changeLoadMoreViewState(loading);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return ITEM_TYPE_LOADMORE;
            }
            return getxItemViewType(position);
        }

        /** 获取数据item数量，不包含加载提示item */
        protected abstract int getxItemCount();

        /** 创建xViewHolder */
        protected abstract VH onCreatexViewHolder(ViewGroup viewGroup, int itemType);

        /** 将item数据和xViewHolder绑定 */
        protected abstract void onBindxViewHolder(VH holder, int position);

        /** 获取item的类型 */
        protected int getxItemViewType(int position) {
            return 0;
        }

        /** 自定义LoadMoreView样式，不重写则使用默认样式 */
        protected LoadMoreViewHolder createLoadMoreViewHolder(ViewGroup vg) {
            return null;
        }
    }

    // LoadMoreView管理：切换LoadMoreItem显隐形、LoadMoreView加载中/非加载状态
    public abstract static class LoadMoreViewHolder {

        private RecyclerView.ViewHolder mVh;
        protected View mLoadMoreView;

        protected LoadMoreViewHolder() {
            mLoadMoreView = createLoadMoreView();
            FrameLayout wrap = new FrameLayout(mLoadMoreView.getContext());
            wrap.addView(mLoadMoreView);
            wrap.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            mVh = new RecyclerView.ViewHolder(wrap) {
            };
            changeLoadMoreViewState(false);
            changeLoadMoreViewVisibility(false);
        }

        <VH> VH getViewHolder() {
            return (VH) mVh;
        }

        // 展示/隐藏LoadMoreView
        void changeLoadMoreViewVisibility(boolean show) {
            mVh.itemView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }

        /** 创建自定义的LoadMoreView */
        public abstract View createLoadMoreView();

        /** 改变LoadMoreView状态：加载中/非加载 */
        public abstract void changeLoadMoreViewState(boolean loading);

        // 默认的LoadMoreView样式：一个ProgressBar和提示文字
        // 非加载状态时，只显示提示文字；加载中状态时只显示ProgressBar
        static LoadMoreViewHolder defaultHolder(ViewGroup vg) {
            return new LoadMoreViewHolder() {

                private View mProgress;
                private TextView mTv;

                @Override
                public View createLoadMoreView() {
                    // ViewGroup
                    Context context = vg.getContext();
                    FrameLayout itemView = new FrameLayout(context);
                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                    itemView.setLayoutParams(lp);
                    // ProgressBar
                    int progressSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
                    FrameLayout.LayoutParams plp = new FrameLayout.LayoutParams(progressSize, progressSize);
                    mProgress = new ProgressBar(context);
                    plp.gravity = Gravity.CENTER;
                    itemView.addView(mProgress, plp);
                    // TextView
                    mTv = new TextView(context);
                    mTv.setText("上拉加载更多");
                    mTv.setTextColor(Color.BLACK);
                    FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tlp.gravity = Gravity.CENTER;
                    itemView.addView(mTv, tlp);

                    return itemView;
                }

                @Override
                public void changeLoadMoreViewState(boolean loading) {
                    mProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
                    mTv.setVisibility(loading ? View.GONE : View.VISIBLE);
                }
            };
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
            if (!(mManager instanceof LinearLayoutManager))
                return;
            // 状态改变成IDLE状态，判断是否需要处理LoadMoreView状态
            // LoadMoreView展示，但未完全展示时，滑动隐藏LoadMoreView
            // LoadMoreView完全展示时，触发加载更多
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mManager;
                int lastCompletePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                View lastVisibleChild = layoutManager.findViewByPosition(lastVisibleItemPosition);
                if (lastVisibleChild == null) {
                    return;
                }

                // RecyclerView用来显示item的区域，未减去topPadding
                int rvInHeight = recyclerView.getBottom() - recyclerView.getPaddingBottom() - recyclerView.getTop();

                // refreshing 或 loading more 时不处理
                if (mState != STATE_IDLE) {
                    return;
                }

                View child = layoutManager.findViewByPosition(lastCompletePosition);
                if (child == null) {
                    return;
                }
                if (lastCompletePosition == mAdapter.getItemCount() - 2) {
                    int deltaY = rvInHeight - child.getBottom();
                    if (deltaY > 0) {
                        recyclerView.smoothScrollBy(0, -deltaY);
                    }
                } else if (lastCompletePosition == mAdapter.getItemCount() - 1) {
                    // 触发LoadMore操作前提，滑动list，LoadMoreView逐渐展示出来
                    // 判断：item是否把RecyclerView占满，如果尚未占满则没有必要展示LoadMoreView，数据量太小
                    // 当LoadMoreView完整展示时，如果第一个item也完整展示，则说明无法达到出发LoadMore操作条件
                    int firstCompletelyPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstCompletelyPosition == 0) {
                        return;
                    }
                    if (mListener == null) {
                        return;
                    }
                    switchState(STATE_LOADINGMORE);
                    mListener.startLoadMore();
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (!(mManager instanceof LinearLayoutManager) || mState != STATE_IDLE)
                return;
            int newState = recyclerView.getScrollState();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mManager;
            int lastCompletePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                // LoadMoreView 展示:完整展示或部分展示
                if (lastVisibleItemPosition == mAdapter.getItemCount() - 1 || lastCompletePosition == mAdapter.getItemCount() - 1) {
                    if (mState == STATE_IDLE) {
                        mAdapter.changeLoadMoreVisibility(true);
                        mAdapter.changeLoadMoreState(false);
                    }
                }
            }
        }
    }

    public interface xAdapterListener {
        void startRefresh();

        void startLoadMore();
    }
}
