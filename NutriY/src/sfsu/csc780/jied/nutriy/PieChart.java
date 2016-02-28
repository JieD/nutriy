package sfsu.csc780.jied.nutriy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view that shows a pie chart of Calorie.
 * Unfinished text paint
 */
public class PieChart extends View {
	
	private boolean mShowChart;
	private float mPieRadius;
	
	private Paint mTextPaint;
	private int mTextColor;
	private float mTextSize;
	
	private int mEmptyPieColor;
	private int mCarbColor;
	private int mFatColor;
	private int mProteinColor;
	
	private Paint mEmptyPiePaint;
	
	private int mCarbPercentage;
	private int mFatPercentage;
	private int mProteinPercentage;
	
	private float centerX;
	private float centerY;
	private static final float DEFAULT_OFFSET = 2.0f;
	private static final float FOCUS_OFFSET = 4.0f;
	private static final float CARB_SLICE_START_ANGLE = 0;
	
	private Slice[] sliceArray = new Slice[3];
	
	private RectF mPieBounds = new RectF() ;
	
	public PieChart(Context context) {
		super(context);
	}

	/**
     * Class constructor taking a context and an attribute set. This constructor
     * is used by the layout engine to construct a {@link PieChart} from a set of
     * XML attributes.
     *
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     *                {@link sfsu.csc780.jied.nutriy.R.styleable.PieChart} as well as attributes inherited
     *                from {@link android.view.View}.
     */
	public PieChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// attrs contains the raw values for the XML attributes
        // that were specified in the layout, which don't include
        // attributes set by styles or themes, and which may have
        // unresolved references. Call obtainStyledAttributes()
        // to get the final values for each attribute.
        //
        // This call uses R.styleable.PieChart, which is an array of
        // the custom attributes that were declared in attrs.xml.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieChart,
                0, 0
        );
        
        try {
            mShowChart         = a.getBoolean(R.styleable.PieChart_showChart, false);
            mEmptyPieColor     = a.getColor(R.styleable.PieChart_emptyPieColor, R.color.empty_pie_blue);
            mCarbColor      = a.getColor(R.styleable.PieChart_carbSliceColor, R.color.pie_blue);
            mFatColor       = a.getColor(R.styleable.PieChart_fatSliceColor, R.color.pie_red);
            mProteinColor   = a.getColor(R.styleable.PieChart_proteinSliceColor, R.color.pie_green);
            mCarbPercentage    = a.getInteger(R.styleable.PieChart_carbPercentage, 50);
            mFatPercentage     = a.getInteger(R.styleable.PieChart_fatPercentage, 30);
            mProteinPercentage = a.getInteger(R.styleable.PieChart_proteinPercentage, 20);
        } finally {
            a.recycle();
        }
        
		init();
	}
	
	private void init() {
		
		mTextColor = getResources().getColor(R.color.white);
		mTextSize = getResources().getDimension(R.dimen.medium_large_text_size);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mTextPaint.setColor(mTextColor);
	    mTextPaint.setTextSize(mTextSize);

	    mEmptyPiePaint = new Paint(0);
	    mEmptyPiePaint.setColor(mEmptyPieColor);
	    mEmptyPiePaint.setStyle(Paint.Style.FILL);
	    
	    Slice mCarbSlice = new Slice(mPieBounds, CARB_SLICE_START_ANGLE, mCarbPercentage, mCarbColor);
	    Slice mFatSlice = new Slice(mPieBounds, mCarbSlice.getEndAngle(), mFatPercentage, mFatColor);
	    Slice mProteinSlice = new Slice(mPieBounds, mFatSlice.getEndAngle(), mProteinPercentage, mProteinColor);
	    sliceArray[0] = mCarbSlice;
	    sliceArray[1] = mFatSlice;
	    sliceArray[2] = mProteinSlice;
	}
	
	public boolean isShowChart() {
	   return mShowChart;
	}

	public void setShowChart(boolean showChart) {
	   mShowChart = showChart;
	   invalidate();
	   requestLayout();
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //
        // Set dimensions for text, pie chart, etc
        //
        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;
        
        setPieBounds(ww, hh);
	}
	
	// Figure out how big we can make the pie.
	private void setPieBounds(float w, float h) {
		centerX = ((float) w) / 2;
		centerY = ((float) h) / 2;
		mPieRadius = Math.min(w, h) / 2;
		mPieBounds.set(
				centerX - mPieRadius,
				centerY - mPieRadius,
				centerX + mPieRadius,
				centerY + mPieRadius
				);
		float xOffset = centerX - mPieRadius;
		mPieBounds.offsetTo(xOffset, getPaddingTop());
	}
	
	// Draw the pie slices
	public void drawSlices(Canvas canvas) {
		for (int i = 0; i < sliceArray.length; i++) {
			Slice slice = sliceArray[i];
			slice.setOffsetBounds(mPieBounds, DEFAULT_OFFSET);
			
			if (i == 2) {
				// added to draw the full circle
				slice.setSweepAngle(-slice.getStartAngle() - 360);
			}
			canvas.drawArc(slice.getmBounds(), slice.getStartAngle(), 
				slice.getSweepAngle(), true, slice.getPaint());
			slice.setOriginalBounds(mPieBounds, DEFAULT_OFFSET);
		}	
	}
	
	protected void onDraw(Canvas canvas) {
	   super.onDraw(canvas);
	   
	   if (mShowChart) {
		   drawSlices(canvas);
	   } else {
		   canvas.drawCircle(centerX, centerY, mPieRadius, mEmptyPiePaint);
	   }
	}

	public double getmCarbPercentage() {
		return mCarbPercentage;
	}

	public void setmCarbPercentage(int mCarbPercentage) {
		this.mCarbPercentage = mCarbPercentage;
	    // invalidate();
	    // requestLayout();
	}

	public double getmFatPercentage() {
		return mFatPercentage;
	}

	public void setmFatPercentage(int mFatPercentage) {
		this.mFatPercentage = mFatPercentage;
		// invalidate();
	    // requestLayout();
	}

	public double getmProteinPercentage() {
		return mProteinPercentage;
	}

	public void setmProteinPercentage(int mProteinPercentage) {
		this.mProteinPercentage = mProteinPercentage;
		//invalidate();
	    // requestLayout();
	}
	
	public void setPercentage(int carbsPercentage, int fatPercentage, int proteinPercentage ) {
		setmCarbPercentage(carbsPercentage);
	    setmFatPercentage(fatPercentage);
		setmProteinPercentage(proteinPercentage);
		invalidate();
	    requestLayout();
	}
	
	private class Slice {
		public RectF mBounds;
        
		public int mPercentage;
        public int mColor;
        public Paint mPaint;

        // computed values
        public float mStartAngle;
        public float mSweepAngle;
        
        public Slice(RectF bounds, float startAngle, int percentage, int color) {
        	mBounds = bounds;
        	mPercentage = percentage;
        	mColor = color;
        	mStartAngle = startAngle;
        	mSweepAngle = 360 * percentage / -100; 
        	initPaint(color);
        } 
        
        public Paint initPaint(int color) {
        	mPaint = new Paint(0);
	        mPaint.setColor(mColor);
	        mPaint.setStyle(Paint.Style.FILL);
	        return mPaint;
        }
        
        /** 
         * Note: Math.sin/cos(radian), not Math.sin/cos(angle)
         * @param  offset - to create gap between pie slices
         * @return [xOffset, yOffset]
         */
        public float[] calculateOffset(float offset) {
        	float offsetArray[] = new float[2];
        	float centerAngle = - (mStartAngle + mSweepAngle / 2);
        	double radian = 2 * Math.PI * centerAngle / 360;
        	offsetArray[0] = (float) (Math.cos(radian) * offset);
        	offsetArray[1] = - (float) (Math.sin(radian) * offset);
        	return offsetArray;
        }
        
        // move the bound to incorporate offset
        public void setOffsetBounds(RectF originalBound, float offset) {
        	float offsetArray[] = calculateOffset(offset);
        	originalBound.offset(offsetArray[0], offsetArray[1]);
        }
        
        // return to the original bound
        public void setOriginalBounds(RectF offsetBound, float offset) {
        	float offsetArray[] = calculateOffset(offset);
        	offsetBound.offset(-offsetArray[0], -offsetArray[1]);
        }
        
        public float getEndAngle() {
        	return mStartAngle + mSweepAngle;
        }
        
        public Paint getPaint() {
        	return mPaint;
        }

		public int getPercentage() {
			return mPercentage;
		}

		public void setPercentage(int percentage) {
			this.mPercentage = percentage;
		}

		public float getStartAngle() {
			return mStartAngle;
		}

		public void setStartAngle(float startAngle) {
			this.mStartAngle = startAngle;
		}

		public float getSweepAngle() {
			return mSweepAngle;
		}

		public void setSweepAngle(float sweepAngle) {
			this.mSweepAngle = sweepAngle;
		}          
		
		public RectF getmBounds() {
			return mBounds;
		}

		public void setmBounds(RectF mBounds) {
			this.mBounds = mBounds;
		}		
	}	
}