package com.android.msahakyan.nestedrecycler.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Collection;
import java.util.List;

/**
 * Created by msahakyan on 05/12/15.
 */
public class Helper {

    public static AlertDialog createConfirmationDialog(Context context, int titleResId, int messageResId,
                                                       int positiveBtnResId, int negativeBtnResId, final ConfirmationCallback callback) {
        return new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(context.getString(titleResId))
            .setMessage(context.getString(messageResId))
            .setPositiveButton(context.getString(positiveBtnResId), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callback.onConfirm();
                }
            })
            .setNegativeButton(context.getString(negativeBtnResId), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callback.onCancel();
                }
            }).show();
    }

    /**
     * Returns comma separated values for the given list of ids
     *
     * @param genreIds
     * @return <code>java.lang.String</code>
     */
    public static String getCsvGenreIds(List<Integer> genreIds) {
        if (isEmpty(genreIds)) {
            return null;
        }
        StringBuilder output = new StringBuilder();
        for (Integer id : genreIds) {
            output.append(id).append(",");
        }
        output.deleteCharAt(output.length() -1);

        return output.toString();
    }

    /**
     * Helper interface
     */
    public interface ConfirmationCallback {
        /**
         * Called on confirmation
         */
        public void onConfirm();

        /**
         * Called on cancellation
         */
        public void onCancel();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

}
