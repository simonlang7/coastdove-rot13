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
