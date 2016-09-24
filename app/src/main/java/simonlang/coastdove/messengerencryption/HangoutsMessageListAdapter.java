package simonlang.coastdove.messengerencryption;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import simonlang.coastdove.lib.ViewTreeNode;

/**
 * Adapter to display Hangouts messages
 */
public class HangoutsMessageListAdapter extends ArrayAdapter<ViewTreeNode> {
    private LayoutInflater mInflater;
    private EncryptionService mService;

    public HangoutsMessageListAdapter(EncryptionService service) {
        super(service, R.layout.list_item_message);
        mService = service;
        mInflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_message, parent, false);

            holder.message = (LinearLayout)convertView.findViewById(R.id.message);
            holder.imageButton = (Button)convertView.findViewById(R.id.image_button);
            holder.messageContainer = (LinearLayout)convertView.findViewById(R.id.message_container);
            holder.messageText = (TextView)convertView.findViewById(R.id.message_text);
            holder.messageDate = (TextView)convertView.findViewById(R.id.message_date);
            holder.padlockImage = (ImageView)convertView.findViewById(R.id.padlock_image);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();

        ViewTreeNode item = getItem(position);
        boolean alignRight = item.hasNode(new ViewTreeNode.Filter() {
            @Override
            public boolean filter(ViewTreeNode viewTreeNode) {
                return viewTreeNode.viewIDResourceName().endsWith("id/message_bubble_left_margin_placeholder");
            }
        });
        ViewTreeNode textNode = item.findNode(new ViewTreeNode.Filter() {
            @Override
            public boolean filter(ViewTreeNode viewTreeNode) {
                return viewTreeNode.viewIDResourceName().endsWith("id/messageText");
            }
        });
        holder.message.setGravity(alignRight ? Gravity.RIGHT : Gravity.LEFT);
        holder.messageContainer.setBackground(getContext().getDrawable(alignRight ? R.drawable.message_bubble_right_bg : R.drawable.message_bubble_left_bg));
        if (textNode != null)
            holder.messageText.setText(EncryptionService.checkDecrypt(textNode.text()));

        if (EncryptionService.isEncrypted(textNode.text()))
            holder.padlockImage.setVisibility(View.VISIBLE);
        else
            holder.padlockImage.setVisibility(View.GONE);

        // Don't set the date...
        holder.messageDate.setText("");

        // Image button
        final ViewTreeNode imageNode = item.findNode(new ViewTreeNode.Filter() {
            @Override
            public boolean filter(ViewTreeNode viewTreeNode) {
                return viewTreeNode.getContentDescription() != null &&
                        viewTreeNode.getContentDescription().equals("Photo");
            }
        });
        if (imageNode != null) {
            holder.imageButton.setVisibility(View.VISIBLE);
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mService.requestAction(imageNode, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                }
            });
        }
        else {
            holder.imageButton.setVisibility(View.GONE);
            holder.imageButton.setOnClickListener(null);
        }

        return convertView;
    }

    private class ViewHolder {
        LinearLayout message;
        Button imageButton;
        LinearLayout messageContainer;
        TextView messageText;
        TextView messageDate;
        ImageView padlockImage;
    }
}
