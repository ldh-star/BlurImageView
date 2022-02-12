package com.liangguo.imageblur;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.FloatRange;
import androidx.appcompat.widget.AppCompatImageView;


/**
 * @author ldh
 * 时间: 2022/2/2 9:03
 * 邮箱: 2637614077@qq.com
 */
public class BlurImageView extends AppCompatImageView {


    /**
     * 为了提高模糊化的性能，在进行模糊时要先对图片进行压缩，再显示出来，这就是压缩的倍率，取值[0, 1]，0则会显示原图
     */
    private float mCompressScale = 0.2f;

    /**
     * 最大模糊半径
     */
    public static final float MAX_BLUR_RADIUS = 25f;

    /**
     * 最小模糊半径
     */
    public static final float MIN_BLUR_RADIUS = 0f;

    private float mBlurRadius = MIN_BLUR_RADIUS;

    private Drawable mSrcDrawable;


    public BlurImageView(Context context) {
        this(context, null);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null && getDrawable() != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BlurImageView, 0, 0);
            mBlurRadius = typedArray.getFloat(R.styleable.BlurImageView_blurRadius, mBlurRadius);
            mCompressScale = typedArray.getFloat(R.styleable.BlurImageView_compressScale, mCompressScale);
            typedArray.recycle();
            updateSrcDrawable();
        }
    }

    /**
     * 更新一次当前drawable，等价于setSrcDrawable(getDrawable())
     */
    public void updateSrcDrawable() {
        setSrcDrawable(getDrawable());
    }

    /**
     * 为该View设置drawable，然后该View会将这个Drawable模糊后显示出来
     */
    public void setSrcDrawable(Drawable drawable) {
        mSrcDrawable = drawable;
        updateBlur();
    }

    /**
     * 获取SrcDrawable
     */
    public Drawable getSrcDrawable() {
        return mSrcDrawable;
    }

    /**
     * 为了提高模糊化的性能，在进行模糊时要先对图片进行压缩，再显示出来，这就是压缩的倍率
     */
    public void setCompressScale(@FloatRange(from = 0f, to = 1f) float compressScale) {
        this.mCompressScale = compressScale;
        updateBlur();
    }

    /**
     * 为了提高模糊化的性能，在进行模糊时要先对图片进行压缩，再显示出来，这就是压缩的倍率
     */
    public float getCompressScale() {
        return mCompressScale;
    }


    /**
     * 模糊半径，取值 [0, 25]， 模糊半径越大，模糊程度越高
     */
    public void setBlurRadius(@FloatRange(from = 0f, to = 25f) float blurRadius) {
        this.mBlurRadius = blurRadius;
        updateBlur();
    }
    /**
     * 模糊半径，取值 [0, 25]， 模糊半径越大，模糊程度越高
     */
    public float getBlurRadius() {
        return this.mBlurRadius;
    }

    /**
     * 更新一次，显示当前画面的模糊画面
     */
    public void updateBlur() {
        doBlur(mSrcDrawable, mBlurRadius, mCompressScale);
    }

    private void doBlur(Drawable srcDrawable, float radius, float compressScale) {
        if (srcDrawable == null)
            return;
        if (radius < MIN_BLUR_RADIUS || radius > MAX_BLUR_RADIUS) {
            throw new IllegalArgumentException("请确保模糊半径的取值范围是0到25，blurRadius must be (0 <= blurRadius <= 25)");
        }
        if (compressScale < 0 || compressScale > 1) {
            throw new IllegalArgumentException("请确保compressScale的取值范围是0到1，compressScale must be (0 <= compressScale <= 1)");
        }
        if (compressScale == 0 || radius == 0) {
            //直接显示，不进行模糊
            super.setImageDrawable(srcDrawable);
        } else {
            if (srcDrawable instanceof  BitmapDrawable) {
                Bitmap blurred = blurRenderScript(((BitmapDrawable) srcDrawable).getBitmap(), radius, compressScale);
                setImageBitmap(blurred);
            } else setImageDrawable(srcDrawable);
        }
        invalidate();
    }

    private Bitmap blurRenderScript(Bitmap smallBitmap, float radius, float compressScale) {
        int width = Math.round(smallBitmap.getWidth() * compressScale);
        int height = Math.round(smallBitmap.getHeight() * compressScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(smallBitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript renderScript = RenderScript.create(getContext());
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        theIntrinsic.setRadius(radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}