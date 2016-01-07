package com.mc.vlcdemo;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mc.vlcdemo.lrc.LrcProcess;
import com.mc.vlcdemo.lrc.LrcProcess.LrcCallback;

public class LrcView extends View {
	
	private Paint mPaint;
	
	private int lineH;
	
	private int mOffset;
	
	private int mstartY;
	
	private int mColor;
	
	private int mHighlightdColor;
	
	private List<String> mLines;
	
	private int mCurrentLine;
	
	private boolean isInTouchMode = false;
	
	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
		float textSize = typedArray.getDimensionPixelSize(R.styleable.LrcView_text_size, 16);
		mColor = typedArray.getColor(R.styleable.LrcView_color, Color.BLACK);
		mHighlightdColor = typedArray.getColor(R.styleable.LrcView_highlighted_color, Color.RED);
		typedArray.recycle();
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTypeface(Typeface.SERIF);
		mPaint.setTextSize(textSize);
		mPaint.setColor(mColor);
		lineH = mPaint.getFontMetricsInt(null);
		
	}

	public LrcView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LrcView(Context context) {
		this(context, null);
	}
	
	public void setLrcCallbak(){
		LrcProcess.getInstance().addCallBack(mLrcCallback);
	}
	
	public void removeLrcCallbak(){
		LrcProcess.getInstance().removeCallback(mLrcCallback);
	}
	
	private LrcCallback mLrcCallback = new LrcCallback() {
		
		@Override
		public void onLrcChanged(List<String> lines, int currentLine) {
			
			if (!isInTouchMode) {
				mLines = lines;
				mCurrentLine = currentLine;
			}
			invalidate();
		}
	};
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mLines != null) {
			
			for (int i = 0; i < mLines.size(); i++) {
				String line = mLines.get(i);
				if (i == mCurrentLine) {
					mPaint.setColor(mHighlightdColor);
				}
				
				while (mOffset <= (1-mLines.size()+mCurrentLine)*lineH) {
					mOffset += lineH;
					scrollTo(0, -mOffset);
				}
				
				canvas.drawText(line, getWidth()/2, getHeight()/2 + lineH * (i - mCurrentLine), mPaint);
				mPaint.setColor(mColor);
			}
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mstartY = (int) event.getY();
			isInTouchMode = true;
			return true;
		case MotionEvent.ACTION_MOVE:
			if (mLines != null && mLines.size() > 0) {
				int currentY = (int)event.getY();
				int expectdOffset = mOffset + currentY - mstartY;
				if (expectdOffset <= mCurrentLine * lineH && expectdOffset >= (1-mLines.size()+mCurrentLine)*lineH) {
					mstartY = currentY;
					mOffset = expectdOffset;
					scrollTo(0, -mOffset);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			isInTouchMode = false;
			break;
		}
		return super.onTouchEvent(event);
	};
}
