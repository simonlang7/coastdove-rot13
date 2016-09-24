package simonlang.coastdove.messengerencryption;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import java.util.Iterator;
import java.util.LinkedList;

import simonlang.coastdove.lib.ScrollPosition;
import simonlang.coastdove.lib.ViewTreeNode;

/**
 * Chat overlay
 */
public class ChatOverlay extends Overlay {
    private EncryptionService mService;
    private ListView mListView;
    private MessageListAdapter mListAdapter;
    private ViewTreeNode mIdListNode;
    private ScrollPosition mScrollPosition;
    private SparseArrayCompat<ViewTreeNode> mMessages;
    private Pair<Integer, Integer> mLastListItemMapping;

    public ChatOverlay(@NonNull EncryptionService service, int resource) {
        super(service, resource);
        mService = service;
        mListAdapter = new MessageListAdapter(service);
        mMessages = new SparseArrayCompat<>(60);
        mLastListItemMapping = new Pair<>(0, 0);
    }

    public void addMessages(ViewTreeNode idListNode, LinkedList<ViewTreeNode> messages, ScrollPosition scrollPosition) {
        if (idListNode == null || scrollPosition == null) {
            mMessages.clear();
            mListAdapter.clear();
            return;
        }

        mIdListNode = idListNode;
        if (mScrollPosition == null || mScrollPosition.getItemCount() != scrollPosition.getItemCount()) {
            mMessages.clear();
        }

        mScrollPosition = scrollPosition;
        Iterator<ViewTreeNode> it = messages.iterator();
        if (messages.size() != scrollPosition.getToIndex() + 1 - scrollPosition.getFromIndex()) {
            return;
        }
        for (int i = scrollPosition.getFromIndex(); i <= scrollPosition.getToIndex() && it.hasNext(); ++i) {
            mMessages.put(i, it.next());
        }

        mListAdapter.clear();
        for (int i = 0; i < mMessages.size(); ++i) {
            int key = mMessages.keyAt(i);
            mListAdapter.add(mMessages.get(key));
        }
        mLastListItemMapping = new Pair<>(mMessages.size() - 1, scrollPosition.getItemCount() - 1);
    }

    @Override
    public void inflate() {
        super.inflate();
        mListView = (ListView)findViewById(R.id.list);
        mListView.setAdapter(mListAdapter);
        Button previous = (Button)findViewById(R.id.button_load_previous);
        Button next = (Button)findViewById(R.id.button_load_next);
        if (Build.VERSION.SDK_INT >= 23) {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_ROW_INT, 103);
                    mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                }
            });
        }

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean mScrollingUp = false;
            private int mPrevFirstVisibileItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mPrevFirstVisibileItem > view.getFirstVisiblePosition())
                    mScrollingUp = true;
                else
                    mScrollingUp = false;
                if (scrollState == SCROLL_STATE_IDLE) {
                    mPrevFirstVisibileItem = view.getFirstVisiblePosition();

                    Bundle args = new Bundle();

                    if (view.getFirstVisiblePosition() == 0) {
                        mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
                    }
                    else if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                    }
                    else {
                        int differenceFromLast = mLastListItemMapping.first -
                                (mScrollingUp ? view.getFirstVisiblePosition() : view.getLastVisiblePosition());
                        args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_ROW_INT, mLastListItemMapping.second - differenceFromLast);
                        mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION, args);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }
}
