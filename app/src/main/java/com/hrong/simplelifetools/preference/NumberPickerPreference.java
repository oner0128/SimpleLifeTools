package com.hrong.simplelifetools.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;

import com.hrong.simplelifetools.R;

/**
 * Created by rrr on 2017/3/4.
 */

public class NumberPickerPreference extends DialogPreference {
    private Context mContext;
    private int currentValue;
    private int min;
    private int max;
    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
        min = typedArray.getInt(R.styleable.NumberPickerPreference_min, Integer.MIN_VALUE);
        max = typedArray.getInt(R.styleable.NumberPickerPreference_max, Integer.MAX_VALUE);
        typedArray.recycle();
    }

    public NumberPickerPreference setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public NumberPickerPreference setMax(int max) {
        this.max = max;
        return this;
    }

    public int getMax() {
        return max;
    }

    public NumberPickerPreference setMin(int min) {
        this.min = min;
        return this;
    }

    public int getMin() {
        return min;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        Window window = getDialog().getWindow();
        if (window != null)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        mNumberPicker = new NumberPicker(mContext);
        mNumberPicker.setMinValue(getMin());
        mNumberPicker.setMaxValue(getMax());
        mNumberPicker.setValue(getCurrentValue());
        mNumberPicker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mNumberPicker.setGravity(Gravity.CENTER);
        builder.setView(mNumberPicker);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        mNumberPicker.clearFocus();
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            int value = mNumberPicker.getValue();
            if (callChangeListener(value)) {
                currentValue = value;
                persistInt(currentValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, getCurrentValue());
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        int value = defaultValue instanceof Integer ? (int) defaultValue : min;
        currentValue = restorePersistedValue ? getPersistedInt(value) : value;
    }
}

