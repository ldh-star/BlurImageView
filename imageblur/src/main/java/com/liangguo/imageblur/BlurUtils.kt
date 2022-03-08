package com.liangguo.imageblur

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.google.android.renderscript.Toolkit
import com.google.android.renderscript.createCompatibleBitmap
import com.google.android.renderscript.validateBitmap
import kotlin.math.roundToInt


/**
 * @author ldh
 * 时间: 2022/3/8 10:06
 * 邮箱: 2637614077@qq.com
 */
object BlurUtils {

    /**
     * 通过RenderScript来进行图像模糊，返回模糊后的图像。
     *
     * @param radius 模糊半径。
     * @param compressScale 图片压缩比例。
     */
    @Deprecated(
        message = "不推荐使用这种方式。从安卓12开始弃用，手机厂商可能不再支持对Render Script的硬件加速，将来的RenderScript可能会在CPU中运行。",
        replaceWith = ReplaceWith("使用Toolkit工具。", "[com.google.android.renderscript.Toolkit]")
    )
    fun Bitmap.blurByRenderScript(
        context: Context,
        radius: Int,
        compressScale: Float
    ): Bitmap {
        val width = (this.width * compressScale).roundToInt()
        val height = (this.height * compressScale).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val renderScript = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap)
        theIntrinsic.setRadius(radius.toFloat())
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }

    /**
     * 通过执行原生方法对图像进行模糊，返回模糊后的图像。
     *
     * @param radius 模糊半径。
     * @param compressScale 图片压缩比例。
     */
    fun Bitmap.blurByNative(
        radius: Int,
        compressScale: Float
    ): Bitmap {
        validateBitmap("blur", this)
        require(radius in 1..25) {
            "${javaClass.name} blur. The radius should be between 1 and 25. $radius provided."
        }
        val inputBitmap = Bitmap.createScaledBitmap(
            this,
            (this.width * compressScale).roundToInt(),
            (this.height * compressScale).roundToInt(),
            false
        )
        val outputBitmap = createCompatibleBitmap(inputBitmap)
        Toolkit.nativeBlurBitmap(
            Toolkit.nativeHandle,
            inputBitmap,
            outputBitmap,
            radius,
            null
        )
        return outputBitmap
    }

}
