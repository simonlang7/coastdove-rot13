package simonlang.coastdove.messengerencryption;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Collection;
import java.util.Set;

import simonlang.coastdove.lib.AppMetaInformation;
import simonlang.coastdove.lib.CoastDoveListenerService;
import simonlang.coastdove.lib.EventType;
import simonlang.coastdove.lib.InteractionEventData;
import simonlang.coastdove.lib.ScrollPosition;
import simonlang.coastdove.lib.ViewTreeNode;

/**
 * Service to provide an encryption overlay to messaging apps
 */
public class EncryptionService extends CoastDoveListenerService {
    private ChatOverlay mChatOverlay;

    @Override
    protected void onServiceBound() {
        mChatOverlay = new ChatOverlay(this, R.layout.chat_overlay);
    }

    @Override
    protected void onServiceUnbound() {
        mChatOverlay.remove();
        mChatOverlay = null;
    }

    @Override
    protected void onAppEnabled(String s) {

    }

    @Override
    protected void onAppDisabled(String s) {
        mChatOverlay.remove();
    }

    @Override
    protected void onMetaInformationDelivered(String s, AppMetaInformation appMetaInformation) {

    }

    @Override
    protected void onAppOpened() {
    }

    @Override
    protected void onAppClosed() {
        mChatOverlay.remove();
    }

    @Override
    protected void onActivityDetected(String activity) {
        if (activity.endsWith(".ConversationActivity")) {
            setOverlayBounds();
            mChatOverlay.show();
        }
        else {
            mChatOverlay.hide();
        }
    }

    @Override
    protected void onLayoutsDetected(Set<String> set) {
        if (getLastActivity().endsWith(".ConversationActivity") &&
                getLastScrollPosition() != null &&
                getLastScrollPosition().getToIndex() == getLastScrollPosition().getItemCount() - 1) {
            requestAction("id/list", AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        }
    }

    @Override
    protected void onInteractionDetected(Collection<InteractionEventData> collection, EventType eventType) {

    }

    @Override
    protected void onNotificationDetected(String s) {

    }

    @Override
    protected void onScreenStateDetected(boolean b) {

    }

    @Override
    protected void onViewTreeReceived(ViewTreeNode node) {
        if (node != null && node.viewIDResourceName().endsWith("id/list")) {
            mChatOverlay.addMessages(node, getLastScrollPosition());
            setOverlayBounds();
            if (node.getRangeInfo() != null)
                Log.d("EncryptionService", "Range (" + node.getRangeInfo().getType() + "): " + node.getRangeInfo().getCurrent() + " between " +
                    node.getRangeInfo().getMin() + " and " + node.getRangeInfo().getMax());
        }
    }

    @Override
    protected void onScrollPositionDetected(ScrollPosition scrollPosition) {
        if (getLastActivity().endsWith(".ConversationActivity"))
            requestViewTree("id/list", true);
    }

    @Override
    protected void onActionSuccessful(ViewTreeNode viewTreeNode, AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {

    }

    @Override
    protected void onActionFailed(ViewTreeNode viewTreeNode, AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {

    }

    private void setOverlayBounds() {
        ViewTreeNode tree = getLastViewTree();
        if (tree != null && tree.viewIDResourceName().endsWith("id/list")) {
            Rect bounds = new Rect();
            tree.getBoundsInScreen(bounds);
            mChatOverlay.setBounds(bounds);
        }
    }
}
