package simonlang.coastdove.messengerencryption;

import android.graphics.Rect;
import android.os.Bundle;
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

    /**
     * Indicates whether the given text is encrypted (i.e., starts with ROT13{ and has a
     * closing } somewhere)
     * @param original    Text to check
     * @return True if encrypted
     */
    public static boolean isEncrypted(String original) {
        return original.startsWith("ROT13{")
                && original.lastIndexOf('}') > 0;
    }

    /**
     * Checks whether the given screen needs to be decrypted, and does so if necessary
     * @param original    Original String (message)
     * @return ROT13 Decryption if String is of form "ROT13{message}non-encrypted text",
     *         otherwise the original String
     */
    public static String checkDecrypt(String original) {
        if (original.startsWith("ROT13{")) {
            int stopIndex = original.lastIndexOf('}');
            if (stopIndex > 0) {
                String toEncrypt = original.substring(6, stopIndex);
                return encrypt(toEncrypt);
            }
        }
        return original;
    }

    /**
     * ROT13-encrypts or decrypts the given String
     * @param toEncrypt    String to encrypt or decrypt
     * @return Encrypted or decrypted String
     */
    public static String encrypt(String toEncrypt) {
        StringBuilder encrypted = new StringBuilder(toEncrypt.length());
        for (int i = 0; i < toEncrypt.length(); ++i) {
            char oldChar = toEncrypt.charAt(i);
            if (Character.isLetter(oldChar)) {
                char zero;
                if (Character.isLowerCase(oldChar))
                    zero = 'a';
                else
                    zero = 'A';
                char newChar = (char)((((int)oldChar - (int)zero + 13) % 26) + (int)zero);
                encrypted.append(newChar);
            }
            else
                encrypted.append(oldChar);
        }
        return encrypted.toString();
    }


    private ChatOverlay mChatOverlay;
    private SendButtonOverlay mSendButtonOverlay;
    private ViewTreeNode mSendButtonNode;
    private ViewTreeNode mMessageInputNode;
    private ViewTreeNode mListNode;

    @Override
    protected void onServiceBound() {
        mChatOverlay = new ChatOverlay(this, R.layout.chat_overlay);
        mSendButtonOverlay = new SendButtonOverlay(this);
    }

    @Override
    protected void onServiceUnbound() {
        mChatOverlay.remove();
        mChatOverlay = null;
        mSendButtonOverlay.remove();
        mSendButtonOverlay = null;
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
            mSendButtonOverlay.show();
        }
        else {
            mChatOverlay.hide();
            mSendButtonOverlay.hide();
        }
    }

    @Override
    protected void onLayoutsDetected(Set<String> set) {
        if (getLastActivity().endsWith(".ConversationActivity")) {
//            if (getLastScrollPosition() != null &&
//                getLastScrollPosition().getToIndex() == getLastScrollPosition().getItemCount() - 1)
//                requestAction("id/list", AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);

            requestViewTree("id/list", true);
            requestViewTree("id/message_text", false);
            requestViewTree("id/floating_send_button_wrapper", false);
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
            mListNode = node;
        }
        else if (node != null && node.viewIDResourceName().endsWith("id/message_text")) {
            mMessageInputNode = node;
        }
        else if (node != null && node.viewIDResourceName().endsWith("id/floating_send_button_wrapper")) {
            mSendButtonNode = node;
            setOverlayBounds();
        }
    }

    @Override
    protected void onScrollPositionDetected(ScrollPosition scrollPosition) {
        if (getLastActivity().endsWith(".ConversationActivity")) {
            requestViewTree("id/list", true);
        }
    }

    @Override
    protected void onActionSuccessful(ViewTreeNode viewTreeNode, AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {

    }

    @Override
    protected void onActionFailed(ViewTreeNode viewTreeNode, AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {

    }

    public void encryptMessageText() {
        if (mMessageInputNode == null)
            return;
        String original = mMessageInputNode.text();
        String encrypted = "ROT13{" + encrypt(original) + "}";
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, encrypted);

        requestAction(mMessageInputNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT, args);
        requestAction(mSendButtonNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
    }

    private void setOverlayBounds() {
        if (mListNode != null) {
            Rect bounds = new Rect();
            mListNode.getBoundsInScreen(bounds);
            mChatOverlay.setBounds(bounds);
        }
        if (mSendButtonNode != null) {
            Rect bounds = new Rect();
            mSendButtonNode.getBoundsInScreen(bounds);
            mSendButtonOverlay.setBounds(bounds);
        }
    }
}
