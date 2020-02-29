package com.mivik.gameclient;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.StyleSpan;

public class BoldSpan extends StyleSpan {
	public BoldSpan() {
		super(Typeface.NORMAL);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setFakeBoldText(true);
		super.updateDrawState(ds);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
		paint.setFakeBoldText(true);
		super.updateMeasureState(paint);
	}
}