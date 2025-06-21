package com.smartscanner.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;

import com.smartscanner.app.AppLog;

public class CheckableHelper implements Checkable {

	private final Object mOwner;

	public interface OnCheckedChangeListener {
		void onCheckedChanged(Object sender, boolean isChecked);
	}

	private boolean mChecked;
	private boolean mFocusable = true;
	private boolean mBroadcasting;

	private OnCheckedChangeListener mOnCheckedChangeListener;

	private int mStyleableChecked = android.R.attr.checked;
	private int mStyleableFocusable = android.R.attr.focusable;
	private int[] mStyleableAttrs = { mStyleableChecked, mStyleableFocusable };

	public static final int[] CHECKED_STATE_SET = {
		android.R.attr.state_checked
	};

	public CheckableHelper() {
		mOwner = this;
	}

	public CheckableHelper(Object owner) {
		if (owner != null) {
			mOwner = owner;
		} else {
			mOwner = this;
		}
	}

	@SuppressLint("ResourceType")
	public void readAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		try {
			final TypedArray a = context.obtainStyledAttributes(
				attrs, mStyleableAttrs, defStyleAttr, defStyleRes);

			final boolean checked = a.getBoolean(0, mChecked);
			setCheckedState(checked);

			// TODO: Why focusable attribute doesn't work through TypedArray?
			mFocusable = attrs.getAttributeBooleanValue(
				"http://schemas.android.com/apk/res/android",
				"focusable", mFocusable);

			a.recycle();
		} catch (Exception e) {
			Log.d(AppLog.TAG, "Cannot get checked attribute", e);
		}
	}

	public boolean setCheckedState(boolean checked) {
		if (mChecked != checked) {
			if (mBroadcasting) return true;

			mBroadcasting = true;
			onUpdateChecked(checked);
			mChecked = checked;

			if (mOnCheckedChangeListener != null) {
				mOnCheckedChangeListener.onCheckedChanged(mOwner, mChecked);
			}

			mBroadcasting = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setChecked(boolean checked) {
		setCheckedState(checked);
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

	protected void onUpdateChecked(boolean checked) {
		if (mOwner != this && mOwner instanceof Checkable) {
			((Checkable) mOwner).setChecked(checked);
		}
	}

	public boolean hasFocusable() {
		return mFocusable;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}
}
