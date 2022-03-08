package com.liangguo.imageblur;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.appcompat.widget.AppCompatImageView;

import java.lang.ref.WeakReference;


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
    public static final int MAX_BLUR_RADIUS = 25;

    /**
     * 最小模糊半径，（实际上最小模糊半径是1，但是我们设定成0的话显示原图）
     */
    public static final int MIN_BLUR_RADIUS = 0;

    private int mBlurRadius = MIN_BLUR_RADIUS;

    private Drawable mSrcDrawable;

    private boolean mIsDetached = false;

    private boolean mEnableBlurInMainThread = false;

    private boolean enableSmartUpdate = true;

    private final RealtimeExecutor mExecutor = new RealtimeExecutor();

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
     * @return 是否允许在主线程上进行模糊处理，默认为false
     */
    public boolean isEnableBlurInMainThread() {
        return mEnableBlurInMainThread;
    }

    /**
     * @param enableBlurInMainThread 是否允许在主线程上进行模糊处理，默认为false
     */
    public void setEnableBlurInMainThread(boolean enableBlurInMainThread) {
        this.mEnableBlurInMainThread = enableBlurInMainThread;
    }

    /**
     * 智能更新：当设置进来的值与之前没有发生改变时，将不会进行模糊更新处理以节约性能。
     * true：每次设置blurRadius或compressScale之后，若值和之前比发生了改变，则会进行模糊处理并更新View，否则不会。
     * false：设置blurRadius或compressScale之后，不会进行任何判断，一律进行模糊处理并更新View。
     *
     * @return 是否允许智能更新
     */
    public boolean isEnableSmartUpdate() {
        return enableSmartUpdate;
    }

    /**
     * 智能更新：当设置进来的值与之前没有发生改变时，将不会进行模糊更新处理以节约性能。
     * true：每次设置blurRadius或compressScale之后，若值和之前比发生了改变，则会进行模糊处理并更新View，否则不会。
     * false：设置blurRadius或compressScale之后，不会进行任何判断，一律进行模糊处理并更新View。
     *
     * @param enableSmartUpdate 是否允许智能更新
     */
    public void setEnableSmartUpdate(boolean enableSmartUpdate) {
        this.enableSmartUpdate = enableSmartUpdate;
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
        setBlurAndCompress(mBlurRadius, compressScale);
    }

    /**
     * 为了提高模糊化的性能，在进行模糊时要先对图片进行压缩，再显示出来，这就是压缩的倍率
     */
    public float getCompressScale() {
        return mCompressScale;
    }

    /**
     * 同时设值模糊半径和压缩比例两个参数，尽可能减少更新次数，节约性能。
     */
    public void setBlurAndCompress(@IntRange(from = MIN_BLUR_RADIUS, to = MAX_BLUR_RADIUS) int blurRadius, @FloatRange(from = 0f, to = 1f) float compressScale) {
        float preBlurRadius = mBlurRadius;
        float preCompressScale = mCompressScale;
        this.mBlurRadius = checkBlurRadius(blurRadius);
        this.mCompressScale = checkCompressScale(compressScale);
        if (!enableSmartUpdate || (preBlurRadius != mBlurRadius || preCompressScale != mCompressScale)) {
            updateBlur();
        }
    }

    /**
     * 模糊半径，取值 [0, 25]， 模糊半径越大，模糊程度越高
     */
    public void setBlurRadius(@IntRange(from = MIN_BLUR_RADIUS, to = MAX_BLUR_RADIUS) int blurRadius) {
        setBlurAndCompress(blurRadius, mCompressScale);
    }

    /**
     * 模糊半径，取值 [0, 25]， 模糊半径越大，模糊程度越高
     */
    public int getBlurRadius() {
        return this.mBlurRadius;
    }

    /**
     * 更新一次，显示当前画面的模糊画面
     */
    public void updateBlur() {
        doBlur(mSrcDrawable, mBlurRadius, mCompressScale);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsDetached = false;
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null && getDrawable() != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BlurImageView, 0, 0);
            mBlurRadius = typedArray.getInt(R.styleable.BlurImageView_blurRadius, mBlurRadius);
            mCompressScale = typedArray.getFloat(R.styleable.BlurImageView_compressScale, mCompressScale);
            mEnableBlurInMainThread = typedArray.getBoolean(R.styleable.BlurImageView_enableBlurInMainThread, mEnableBlurInMainThread);
            typedArray.recycle();
            updateSrcDrawable();
        }
    }

    private int checkBlurRadius(int blurRadius) {
        //检查是否合法并返回合法的compressScale
        if (blurRadius < MIN_BLUR_RADIUS) blurRadius = MIN_BLUR_RADIUS;
        else if (blurRadius > MAX_BLUR_RADIUS) blurRadius = MAX_BLUR_RADIUS;
        return blurRadius;
    }

    private float checkCompressScale(float compressScale) {
        //检查是否合法并返回合法的compressScale
        if (compressScale < 0) compressScale = 0;
        else if (compressScale > 1) compressScale = 1;
        return compressScale;
    }

    private void doBlur(Drawable srcDrawable, int radius, float compressScale) {
        if (srcDrawable == null)
            return;
        if (compressScale == 0 || radius == 0) {
            //直接显示，不进行模糊
            super.setImageDrawable(srcDrawable);
        } else {
            if (srcDrawable instanceof BitmapDrawable) {
                if (mEnableBlurInMainThread) {
                    setImageBitmap(BlurUtils.INSTANCE.blurByNative(((BitmapDrawable) srcDrawable).getBitmap(), radius, compressScale));
                } else {
                    //在子线程中运行不能强引用this，有内存泄漏的风险。
                    WeakReference<BlurImageView> thisReference = new WeakReference<>(this);
                    mExecutor.submit(() -> {
                        Bitmap blurred = BlurUtils.INSTANCE.blurByNative(((BitmapDrawable) srcDrawable).getBitmap(), radius, compressScale);
                        if (thisReference.get() != null && !thisReference.get().mIsDetached) {
                            thisReference.get().post(() -> thisReference.get().setImageBitmap(blurred));
                        }
                        return null;
                    });
                }
            } else super.setImageDrawable(srcDrawable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsDetached = true;
        mExecutor.shutdownNow();
    }

}