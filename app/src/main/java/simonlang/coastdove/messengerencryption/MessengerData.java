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

import simonlang.coastdove.lib.ViewTreeNode;

/**
 * Contains data needed for drawing overlays, extracting data, etc.
 */
public abstract class MessengerData {
    public static final MessengerData hangoutsData = new HangoutsData();
    public static final MessengerData whatsappData = new WhatsAppData();

    public static final String hangouts = "com.google.android.talk";
    public static final String whatsapp = "com.whatsapp";

    public static class HangoutsData extends MessengerData {
        public HangoutsData() {
            conversationActivity = ".ConversationActivity";
            listTreeID = "id/list";
            messageInputFieldID = "id/message_text";
            sendButtonID = "id/floating_send_button_wrapper";
            messageFilter = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode node) {
                    return node.viewIDResourceName().endsWith("id/messageContentFrame");
                }
            };
            alignRightIndicator = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.viewIDResourceName().endsWith("id/message_bubble_left_margin_placeholder");
                }
            };
            textNodeFilter = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.viewIDResourceName().endsWith("id/messageText");
                }
            };
            imageFilter = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.getContentDescription() != null &&
                            viewTreeNode.getContentDescription().equals("Photo");
                }
            };
        }
    }

    public static class WhatsAppData extends MessengerData {
        public WhatsAppData() {
            conversationActivity = ".Conversation";
            listTreeID = "id/list";
            messageInputFieldID = "id/entry";
            sendButtonID = "id/send";
            messageFilter = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.viewIDResourceName().endsWith("id/main_layout");
                }
            };
            alignRightIndicator = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.viewIDResourceName().endsWith("id/status");
                }
            };
            textNodeFilter = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.viewIDResourceName().endsWith("id/message_text") ||
                            viewTreeNode.viewIDResourceName().endsWith("id/caption");
                }
            };
            imageFilter = new ViewTreeNode.Filter() {
                @Override
                public boolean filter(ViewTreeNode viewTreeNode) {
                    return viewTreeNode.viewIDResourceName().endsWith("id/image");
                }
            };
        }
    }


    protected String conversationActivity;
    protected String listTreeID;
    protected String messageInputFieldID;
    protected String sendButtonID;
    protected ViewTreeNode.Filter messageFilter;
    protected ViewTreeNode.Filter alignRightIndicator;
    protected ViewTreeNode.Filter textNodeFilter;
    protected ViewTreeNode.Filter imageFilter;

    private MessengerData() {
    }

    public String getConversationActivity() {
        return conversationActivity;
    }

    public String getListTreeID() {
        return listTreeID;
    }

    public String getMessageInputFieldID() {
        return messageInputFieldID;
    }

    public String getSendButtonID() {
        return sendButtonID;
    }

    public ViewTreeNode.Filter getMessageFilter() {
        return messageFilter;
    }

    public ViewTreeNode.Filter getAlignRightIndicator() {
        return alignRightIndicator;
    }

    public ViewTreeNode.Filter getTextNodeFilter() {
        return textNodeFilter;
    }

    public ViewTreeNode.Filter getImageNodeFilter() {
        return imageFilter;
    }
}
