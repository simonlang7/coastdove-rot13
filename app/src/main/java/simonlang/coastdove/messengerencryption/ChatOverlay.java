package simonlang.coastdove.messengerencryption;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.ListView;

import simonlang.coastdove.lib.ViewTreeNode;

/**
 * Chat overlay
 */
public class ChatOverlay extends Overlay {
    private EncryptionService mService;
    private ListView mListView;
    private HangoutsMessageListAdapter mListAdapter;
    private ViewTreeNode mIdListNode;

    public ChatOverlay(@NonNull EncryptionService service, int resource) {
        super(service, resource);
        mService = service;
        mListAdapter = new HangoutsMessageListAdapter(service);
    }

    public void addMessages(ViewTreeNode idListNode) {
        this.mIdListNode = idListNode;
        mListAdapter.clear();
        mListAdapter.addAll(idListNode.findNodes(new ViewTreeNode.Filter() {
            @Override
            public boolean filter(ViewTreeNode node) {
                return node.viewIDResourceName().endsWith("id/messageContentFrame");
            }
        }));

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
                    mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mService.requestAction(mIdListNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
                }
            });
        }
    }
}
