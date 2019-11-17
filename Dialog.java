package com.wuqibo.bppcallbackservice.btoolkit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialog {

    public interface Callback {
        void call();
    }

    public static void showAlert(Context context, String title, String content, final Callback onClose) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if (title != null || title.length() > 0) {
            dialog.setTitle(title);
        }
        if (content != null || content.length() > 0) {
            dialog.setMessage(content);
        }
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onClose != null) {
                    onClose.call();
                }
            }
        });
        dialog.show();
    }

    public static void showConfirm(Context context, String title, String content, final Callback onConfirm, final Callback onCancel) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if (title != null || title.length() > 0) {
            dialog.setTitle(title);
        }
        if (content != null || content.length() > 0) {
            dialog.setMessage(content);
        }
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onConfirm != null) {
                    onConfirm.call();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onCancel != null) {
                    onCancel.call();
                }
            }
        });
        dialog.show();
    }

}
