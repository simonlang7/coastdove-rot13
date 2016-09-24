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

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;

/**
 * Overlay for the send button
 */
public class SendButtonOverlay extends Overlay {
    private EncryptionService mService;

    public SendButtonOverlay(@NonNull EncryptionService service) {
        super(service, R.layout.send_button);
        mService = service;
    }

    @Override
    public void inflate() {
        super.inflate();
        ImageButton sendButton = (ImageButton)findViewById(R.id.send_image_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.encryptMessageText();
            }
        });
    }
}
