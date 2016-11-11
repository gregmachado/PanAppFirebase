package gregmachado.com.panappfirebase.util;

/**
 * Created by gregmachado on 10/11/16.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import gregmachado.com.panappfirebase.R;

public class DialogHandler {
    public Runnable ans_true = null;
    public Runnable ans_false = null;

    // Dialog. --------------------------------------------------------------

    public boolean Confirm(Context act, String Title, String ConfirmText,
                           String CancelBtn, String OkBtn, Runnable aProcedure, Runnable bProcedure) {
        ans_true = aProcedure;
        ans_false= bProcedure;
        AlertDialog dialog = new AlertDialog.Builder(act).create();
        dialog.setTitle(Title);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        ans_true.run();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        ans_false.run();
                    }
                });
        dialog.setIcon(R.drawable.ic_error_red_300_18dp);
        dialog.show();
        return true;
    }
}