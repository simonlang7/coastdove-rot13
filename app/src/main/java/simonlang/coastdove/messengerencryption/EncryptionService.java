/*  Coast Dove
    Copyright (C) 2016  Simon Lang
    Contact: simon.lang7 at gmail dot com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package simonlang.coastdove.messengerencryption;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Collection;
import java.util.LinkedList;
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

    private MessengerData mMessengerData;
    private ChatOverlay mChatOverlay;
    private SendButtonOverlay mSendButtonOverlay;
    private ViewTreeNode mSendButtonNode;
    private ViewTreeNode mMessageInputNode;
    private ViewTreeNode mListNode;
    private boolean mSendMessage;

    @Override
    protected void onServiceBound() {
        mChatOverlay = new ChatOverlay(this, R.layout.chat_overlay);
        mSendButtonOverlay = new SendButtonOverlay(this);
        mSendMessage = false;
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
        switch (getLastAppPackageName()) {
            case MessengerData.hangouts:
                mMessengerData = MessengerData.hangoutsData;
                break;
            case MessengerData.whatsapp:
                mMessengerData = MessengerData.whatsappData;
                break;
            default:
                mMessengerData = null;
        }
    }

    @Override
    protected void onAppClosed() {
        mChatOverlay.remove();
    }

    @Override
    protected void onActivityDetected(String activity) {
        if (activity.endsWith(messengerData().getConversationActivity())) {
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
        if (messengerData() == null)
            return;
        if (getLastActivity().endsWith(messengerData().getConversationActivity())) {
//            if (getLastScrollPosition() != null &&
//                getLastScrollPosition().getToIndex() == getLastScrollPosition().getItemCount() - 1)
//                requestAction("id/list", AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);

            requestViewTree(messengerData().getListTreeID(), true);
            requestViewTree(messengerData().getMessageInputFieldID(), false);
            requestViewTree(messengerData().getSendButtonID(), false);
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
        if (messengerData() == null)
            return;
        if (node != null && node.viewIDResourceName().endsWith(messengerData().getListTreeID())) {
            LinkedList<ViewTreeNode> messages = node.findNodes(messengerData().getMessageFilter());
            mChatOverlay.addMessages(node, messages, getLastScrollPosition());
            setOverlayBounds();
            mListNode = node;
        }
        else if (node != null && node.viewIDResourceName().endsWith(messengerData().getMessageInputFieldID())) {
            mMessageInputNode = node;
            if (mSendMessage) {
                String original = mMessageInputNode.text();
                String encrypted = "ROT13{" + encrypt(original) + "}";
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, encrypted);

                requestAction(mMessageInputNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT, args);
                requestAction(mSendButtonNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                mSendMessage = false;
            }
        }
        else if (node != null && node.viewIDResourceName().endsWith(messengerData().getSendButtonID())) {
            mSendButtonNode = node;
            setOverlayBounds();
        }
    }

    @Override
    protected void onScrollPositionDetected(ScrollPosition scrollPosition) {
        if (messengerData() == null)
            return;
        if (getLastActivity().endsWith(messengerData().getConversationActivity())) {
            requestViewTree(messengerData().getListTreeID(), true);
        }
    }

    @Override
    protected void onActionSuccessful(ViewTreeNode viewTreeNode, AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {

    }

    @Override
    protected void onActionFailed(ViewTreeNode viewTreeNode, AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {

    }

    public void encryptMessageText() {
        if (messengerData() == null)
            return;
        if (mMessageInputNode == null)
            return;
        mSendMessage = true;
        requestViewTree(messengerData().getMessageInputFieldID(), false);
    }

    public MessengerData messengerData() {
        return mMessengerData;
    }

    private void setOverlayBounds() {
        if (messengerData() == null)
            return;
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
