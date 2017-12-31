/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.captain_miao.optroundcardview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * A FrameLayout with a rounded corner background and shadow.
 * <p>
 * CardView uses <code>elevation</code> property on Lollipop for shadows and falls back to a
 * custom emulated shadow implementation on older platforms.
 * <p>
 * Due to expensive nature of rounded corner clipping, on platforms before Lollipop, CardView does
 * not clip its children that intersect with rounded corners. Instead, it adds padding to avoid such
 * intersection (See {@link #setPreventCornerOverlap(boolean)} to change this behavior).
 * <p>
 * Before Lollipop, CardView adds padding to its content and draws shadows to that area. This
 * padding amount is equal to <code>maxCardElevation + (1 - cos45) * cornerRadius</code> on the
 * sides and <code>maxCardElevation * 1.5 + (1 - cos45) * cornerRadius</code> on top and bottom.
 * <p>
 * Since padding is used to offset content for shadows, you cannot set padding on CardView.
 * Instead, you can use content padding attributes in XML or
 * {@link #setContentPadding(int, int, int, int)} in code to set the padding between the edges of
 * the CardView and children of CardView.
 * <p>
 * Note that, if you specify exact dimensions for the CardView, because of the shadows, its content
 * area will be different between platforms before Lollipop and after Lollipop. By using api version
 * specific resource values, you can avoid these changes. Alternatively, If you want CardView to add
 * inner padding on platforms Lollipop and after as well, you can call
 * {@link #setUseCompatPadding(boolean)} and pass <code>true</code>.
 * <p>
 * To change CardView's elevation in a backward compatible way, use
 * {@link #setCardElevation(float)}. CardView will use elevation API on Lollipop and before
 * Lollipop, it will change the shadow size. To avoid moving the View while shadow size is changing,
 * shadow size is clamped by {@link #getMaxCardElevation()}. If you want to change elevation
 * dynamically, you should call {@link #setMaxCardElevation(float)} when CardView is initialized.
 *
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardBackgroundColor
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardCornerRadius
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardElevation
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardMaxElevation
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardUseCompatPadding
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardPreventCornerOverlap
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPadding
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingLeft
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingTop
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingRight
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingBottom
 */
public class OptRoundCardView extends FrameLayout implements CardViewDelegate {
    private static final int[] COLOR_BACKGROUND_ATTR = {android.R.attr.colorBackground};
    private static final CardViewImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IMPL = new CardViewApi21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            IMPL = new CardViewJellybeanMr1();
        } else {
            IMPL = new CardViewEclairMr1();
        }
        IMPL.initStatic();
    }

    private boolean mCompatPadding;

    private boolean mPreventCornerOverlap;

    private final Rect mContentPadding = new Rect();

    private final Rect mShadowBounds = new Rect();


    public OptRoundCardView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public OptRoundCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public OptRoundCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        // NO OP
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // NO OP
    }

    /**
     * Returns whether CardView will add inner padding on platforms Lollipop and after.
     *
     * @return <code>true</code> if CardView adds inner padding on platforms Lollipop and after to
     * have same dimensions with platforms before Lollipop.
     */
    @Override
    public boolean getUseCompatPadding() {
        return mCompatPadding;
    }

    /**
     * CardView adds additional padding to draw shadows on platforms before Lollipop.
     * <p>
     * This may cause Cards to have different sizes between Lollipop and before Lollipop. If you
     * need to align CardView with other Views, you may need api version specific dimension
     * resources to account for the changes.
     * As an alternative, you can set this flag to <code>true</code> and CardView will add the same
     * padding values on platforms Lollipop and after.
     * <p>
     * Since setting this flag to true adds unnecessary gaps in the UI, default value is
     * <code>false</code>.
     *
     * @param useCompatPadding <code>true></code> if CardView should add padding for the shadows on
     *                         platforms Lollipop and above.
     * @attr ref android.support.v7.cardview.R.styleable#CardView_cardUseCompatPadding
     */
    public void setUseCompatPadding(boolean useCompatPadding) {
        if (mCompatPadding == useCompatPadding) {
            return;
        }
        mCompatPadding = useCompatPadding;
        IMPL.onCompatPaddingChanged(this);
    }

    /**
     * Sets the padding between the Card's edges and the children of CardView.
     * <p>
     * Depending on platform version or {@link #getUseCompatPadding()} settings, CardView may
     * update these values before calling {@link android.view.View#setPadding(int, int, int, int)}.
     *
     * @param left   The left padding in pixels
     * @param top    The top padding in pixels
     * @param right  The right padding in pixels
     * @param bottom The bottom padding in pixels
     * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPadding
     * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingLeft
     * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingTop
     * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingRight
     * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingBottom
     */
    public void setContentPadding(int left, int top, int right, int bottom) {
        mContentPadding.set(left, top, right, bottom);
        IMPL.updatePadding(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (IMPL instanceof CardViewApi21 == false) {
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            switch (widthMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minWidth = (int) Math.ceil(IMPL.getMinWidth(this));
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
                            MeasureSpec.getSize(widthMeasureSpec)), widthMode);
                    break;
            }

            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minHeight = (int) Math.ceil(IMPL.getMinHeight(this));
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
                            MeasureSpec.getSize(heightMeasureSpec)), heightMode);
                    break;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OptRoundCardView, defStyleAttr,
                R.style.OptRoundCardView_DayNight);
        ColorStateList backgroundColor;

        if (a.hasValue(R.styleable.OptRoundCardView_optRoundCardBackgroundColor)) {
            // use xml color
            backgroundColor = a.getColorStateList(R.styleable.OptRoundCardView_optRoundCardBackgroundColor);
        } else {
            // There isn't one set, so we'll compute one based on the theme
            final TypedArray aa = getContext().obtainStyledAttributes(COLOR_BACKGROUND_ATTR);
            final int themeColorBackground = aa.getColor(0, 0);
            aa.recycle();

            // If the theme colorBackground is light, use our own light color, otherwise dark
            final float[] hsv = new float[3];
            Color.colorToHSV(themeColorBackground, hsv);
            backgroundColor = ColorStateList.valueOf(hsv[2] > 0.5f
                    ? getResources().getColor(R.color.opt_round_card_view_light_background)
                    : getResources().getColor(R.color.opt_round_card_view_dark_background));
        }


        float radius = a.getDimension(R.styleable.OptRoundCardView_optRoundCardCornerRadius, 0);
        float elevation = a.getDimension(R.styleable.OptRoundCardView_optRoundCardElevation, 0);
        float maxElevation = a.getDimension(R.styleable.OptRoundCardView_optRoundCardMaxElevation, 0);
        mCompatPadding = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardUseCompatPadding, false);
        mPreventCornerOverlap = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardPreventCornerOverlap, true);
        int defaultPadding = a.getDimensionPixelSize(R.styleable.OptRoundCardView_optRoundContentPadding, 0);
        mContentPadding.left = a.getDimensionPixelSize(R.styleable.OptRoundCardView_optRoundContentPaddingLeft,
                defaultPadding);
        mContentPadding.top = a.getDimensionPixelSize(R.styleable.OptRoundCardView_optRoundContentPaddingTop,
                defaultPadding);
        mContentPadding.right = a.getDimensionPixelSize(R.styleable.OptRoundCardView_optRoundContentPaddingRight,
                defaultPadding);
        mContentPadding.bottom = a.getDimensionPixelSize(R.styleable.OptRoundCardView_optRoundContentPaddingBottom,
                defaultPadding);
        if (elevation > maxElevation) {
            maxElevation = elevation;
        }

        int cornerFlag = 0;
        boolean flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardLeftTopCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_LEFT_TOP_CORNER : 0;

        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardRightTopCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_RIGHT_TOP_CORNER : 0;

        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardLeftBottomCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_LEFT_BOTTOM_CORNER : 0;

        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardRightBottomCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_RIGHT_BOTTOM_CORNER : 0;

        int edgesFlag = 0;
        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardLeftEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_LEFT_EDGES : 0;

        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardTopEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_TOP_EDGES : 0;

        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardRightEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_RIGHT_EDGES : 0;

        flag = a.getBoolean(R.styleable.OptRoundCardView_optRoundCardBottomEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_BOTTOM_EDGES : 0;

        a.recycle();

        IMPL.initialize(this, context, backgroundColor.getDefaultColor(), radius, elevation, maxElevation, cornerFlag, edgesFlag);
    }

    /**
     * Updates the background color of the CardView
     *
     * @param color The new color to set for the card background
     * @attr ref android.support.v7.cardview.R.styleable#CardView_cardBackgroundColor
     */
    public void setCardBackgroundColor(int color) {
        IMPL.setBackgroundColor(this, color);
    }

    /**
     * Returns the inner padding after the Card's left edge
     *
     * @return the inner padding after the Card's left edge
     */
    public int getContentPaddingLeft() {
        return mContentPadding.left;
    }

    /**
     * Returns the inner padding before the Card's right edge
     *
     * @return the inner padding before the Card's right edge
     */
    public int getContentPaddingRight() {
        return mContentPadding.right;
    }

    /**
     * Returns the inner padding after the Card's top edge
     *
     * @return the inner padding after the Card's top edge
     */
    public int getContentPaddingTop() {
        return mContentPadding.top;
    }

    /**
     * Returns the inner padding before the Card's bottom edge
     *
     * @return the inner padding before the Card's bottom edge
     */
    public int getContentPaddingBottom() {
        return mContentPadding.bottom;
    }

    /**
     * Updates the corner radius of the CardView.
     *
     * @param radius The radius in pixels of the corners of the rectangle shape
     * @attr ref android.support.v7.cardview.R.styleable#CardView_cardCornerRadius
     * @see #setRadius(float)
     */
    public void setRadius(float radius) {
        IMPL.setRadius(this, radius);
    }

    /**
     * Returns the corner radius of the CardView.
     *
     * @return Corner radius of the CardView
     * @see #getRadius()
     */
    public float getRadius() {
        return IMPL.getRadius(this);
    }

    /**
     * Internal method used by CardView implementations to update the padding.
     *
     * @hide
     */
    @Override
    public void setShadowPadding(int left, int top, int right, int bottom) {
        mShadowBounds.set(left, top, right, bottom);
        super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
                right + mContentPadding.right, bottom + mContentPadding.bottom);
    }

    /**
     * Updates the backward compatible elevation of the CardView.
     *
     * @param elevation The backward compatible elevation in pixels.
     * @attr ref android.support.v7.cardview.R.styleable#CardView_cardElevation
     * @see #getCardElevation()
     * @see #setMaxCardElevation(float)
     */
    public void setCardElevation(float elevation) {
        IMPL.setElevation(this, elevation);
    }

    /**
     * Returns the backward compatible elevation of the CardView.
     *
     * @return Elevation of the CardView
     * @see #setCardElevation(float)
     * @see #getMaxCardElevation()
     */
    public float getCardElevation() {
        return IMPL.getElevation(this);
    }

    /**
     * Updates the backward compatible maximum elevation of the CardView.
     * <p>
     * Calling this method has no effect if device OS version is Lollipop or newer and
     * {@link #getUseCompatPadding()} is <code>false</code>.
     *
     * @param maxElevation The backward compatible maximum elevation in pixels.
     * @attr ref android.support.v7.cardview.R.styleable#CardView_cardMaxElevation
     * @see #setCardElevation(float)
     * @see #getMaxCardElevation()
     */
    public void setMaxCardElevation(float maxElevation) {
        IMPL.setMaxElevation(this, maxElevation);
    }

    /**
     * Returns the backward compatible maximum elevation of the CardView.
     *
     * @return Maximum elevation of the CardView
     * @see #setMaxCardElevation(float)
     * @see #getCardElevation()
     */
    public float getMaxCardElevation() {
        return IMPL.getMaxElevation(this);
    }

    /**
     * Returns whether CardView should add extra padding to content to avoid overlaps with rounded
     * corners on pre-Lollipop platforms.
     *
     * @return True if CardView prevents overlaps with rounded corners on platforms before Lollipop.
     * Default value is <code>true</code>.
     */
    @Override
    public boolean getPreventCornerOverlap() {
        return mPreventCornerOverlap;
    }

    /**
     * On pre-Lollipop platforms, CardView does not clip the bounds of the Card for the rounded
     * corners. Instead, it adds padding to content so that it won't overlap with the rounded
     * corners. You can disable this behavior by setting this field to <code>false</code>.
     * <p>
     * Setting this value on Lollipop and above does not have any effect unless you have enabled
     * compatibility padding.
     *
     * @param preventCornerOverlap Whether CardView should add extra padding to content to avoid
     *                             overlaps with the CardView corners.
     * @attr ref android.support.v7.cardview.R.styleable#CardView_cardPreventCornerOverlap
     * @see #setUseCompatPadding(boolean)
     */
    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        if (preventCornerOverlap == mPreventCornerOverlap) {
            return;
        }
        mPreventCornerOverlap = preventCornerOverlap;
        IMPL.onPreventCornerOverlapChanged(this);
    }

    private static final boolean SDK_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    /**
     * show corner or rect
     */
    public void showCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        if (SDK_LOLLIPOP) {
            ((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        }
    }

    public void showLeftTopCorner(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showLeftTopRect(!show);
        }
    }

    public void showRightTopCorner(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showRightTopRect(!show);
        }
    }

    public void showLeftBottomCorner(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showLeftBottomRect(!show);
        }
    }

    public void showRightBottomCorner(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showRightBottomRect(!show);
        }
    }

    /**
     * show Edge Shadow
     */
    public void showEdgeShadow(boolean left, boolean top, boolean right, boolean bottom) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showEdgeShadow(left, top, right, bottom);
        }
    }

    public void showLeftEdgeShadow(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showLeftEdgeShadow(show);
        }
    }

    public void showTopEdgeShadow(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showTopEdgeShadow(show);
        }
    }

    public void showRightEdgeShadow(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showRightEdgeShadow(show);
        }
    }

    public void showBottomEdgeShadow(boolean show) {
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showBottomEdgeShadow(show);
        }
    }
}
