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
 * Adapter to display messages
 */
public class MessageListAdapter extends ArrayAdapter<ViewTreeNode> {
    private LayoutInflater mInflater;
    private EncryptionService mService;

    public MessageListAdapter(EncryptionService service) {
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
        boolean alignRight = item.hasNode(mService.messengerData().getAlignRightIndicator());
        ViewTreeNode textNode = item.findNode(mService.messengerData().getTextNodeFilter());
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
        final ViewTreeNode imageNode = item.findNode(mService.messengerData().getImageNodeFilter());
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
