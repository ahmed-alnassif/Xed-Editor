package com.rk.xededitor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.rk.xededitor.rkUtils;
import io.github.rosemoe.sora.text.ContentIO;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.IOException;
import java.io.*;
import com.google.android.material.tabs.TabLayout;
import static com.rk.xededitor.MainActivity.*;
import java.net.URLConnection;
import android.os.Vibrator;
import android.os.*;

public class SimpleViewHolder extends TreeNode.BaseNodeViewHolder<Object> {
    private boolean isFile;
    private ImageView arrow;
    private TreeNode node;
    private final Context context;
    private final int berryColor;
    private final int closedDrawable;
    private final int openedDrawable;
    private final int folderDrawable;
    private final int fileDrawable;
    public final int indentation_level = 50;

    private EditorManager manager;
    private CodeEditor editor;
    boolean isRotated = false;

    public SimpleViewHolder(Context context) {
        super(context);
        this.context = context;
        berryColor = ContextCompat.getColor(context, R.color.berry);
        closedDrawable = R.drawable.closed;
        openedDrawable = R.drawable.opened;
        folderDrawable = R.drawable.folder;
        fileDrawable = R.drawable.file;
        editor = MainActivity.getEditor();
        manager = new EditorManager(editor, context);
    }

    @Override
    public View createNodeView(TreeNode node, Object value) {
        this.node = node;
        isFile = node.isFile;
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_effect));
        } else {
            layout.setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ripple_effect));
        }
        final LinearLayout.LayoutParams layout_params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_params.setMargins(0, 10, 0, 10);
        layout.setLayoutParams(layout_params);

        arrow = new ImageView(context);
        final ImageView img = new ImageView(context);
        img.setImageDrawable(ContextCompat.getDrawable(context, fileDrawable));
        final TextView tv = new TextView(context);

        tv.setText(String.valueOf(value));

        tv.setTextColor(berryColor);

        LinearLayout.LayoutParams imgParams;
        if (!isFile) {
            imgParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            imgParams.setMargins(0, 0, 10, 0);
            final LinearLayout.LayoutParams arr_params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            arr_params.setMargins(indentation_level * node.indentation, 7, 0, 0);
            arrow.setLayoutParams(arr_params);
            arrow.setImageDrawable(ContextCompat.getDrawable(context, closedDrawable));
            layout.addView(arrow);
            img.setImageDrawable(ContextCompat.getDrawable(context, folderDrawable));
            img.setLayoutParams(imgParams);
        } else {
            final LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(indentation_level * node.indentation, 0, 0, 0);

            img.setLayoutParams(params);
        }

        layout.addView(img);
        layout.addView(tv);

        return layout;
    }

    @Override
    public void toggle(boolean active) {
        if (node == null || arrow == null || node.file == null) {
            return;
        }
        // implement loading
        if (!isFile) {
          //  arrow.setImageDrawable(
                    //ContextCompat.getDrawable(context, active ? openedDrawable : closedDrawable));
            
            if (isRotated) {
                    rotateImage(90, 0,arrow);
                    isRotated = false;
                } else {
                    rotateImage(0, 90,arrow);
                    isRotated = true;
                }
            
            
            if(!node.isLoaded()){
                MainActivity.looper(node.file, node, node.indentation + 1);
            node.setLoaded();
            }
            
            
        } else if (isFile) {
            //  Uri uri = node.file.getUri();

            String type = node.file.getType();
            if (type == null) {
                rkUtils.toast(context, "Error: Mime Type is null");
            }
            if (!(type.contains("text") || type.contains("plain"))) {
                // Todo: show window warning user (it's not a file )
                manager.newEditor(node.file);
            } else {
                manager.newEditor(node.file);
            }
        }
        
    }
    
    private void rotateImage(float fromDegrees, float toDegrees,View v) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300); // Set the duration of the animation in milliseconds
        rotateAnimation.setFillAfter(true); // Keeps the state of the view after the animation ends
        v.startAnimation(rotateAnimation);
    }
}
