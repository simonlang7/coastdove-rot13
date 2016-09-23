package simonlang.coastdove.messengerencryption;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import simonlang.coastdove.lib.ViewTreeNode;

/**
 * Adapter to display Hangouts messages
 */
public class HangoutsMessageListAdapter extends ArrayAdapter<ViewTreeNode> {
    private LayoutInflater mInflater;

    public HangoutsMessageListAdapter(Context context) {
        super(context, R.layout.list_item_message);
        mInflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_message, parent, false);

            holder.message = (LinearLayout)convertView.findViewById(R.id.message);
            holder.messageContainer = (LinearLayout)convertView.findViewById(R.id.message_container);
            holder.messageText = (TextView)convertView.findViewById(R.id.message_text);
            holder.messageDate = (TextView)convertView.findViewById(R.id.message_date);

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
        holder.messageContainer.setBackgroundColor(alignRight ? Color.parseColor("#BEBEBE") : Color.WHITE);
        if (textNode != null)
            holder.messageText.setText(textNode.text());

        // Don't set the date...
        holder.messageDate.setText("");

        return convertView;
    }

    private class ViewHolder {
        LinearLayout message;
        LinearLayout messageContainer;
        TextView messageText;
        TextView messageDate;
    }
}
