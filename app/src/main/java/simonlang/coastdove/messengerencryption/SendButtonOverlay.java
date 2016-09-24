package simonlang.coastdove.messengerencryption;

import android.content.Context;
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
