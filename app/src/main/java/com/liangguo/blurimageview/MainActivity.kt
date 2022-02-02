package com.liangguo.blurimageview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.liangguo.blurimageview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)!!

        mDataBinding.seekbarBlurRadius.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val start = System.currentTimeMillis()
                mDataBinding.blurImageView.blurRadius = p1.toFloat()
                updateTimeText(System.currentTimeMillis() - start)
                updateText()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        mDataBinding.seekbarCompressScale.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                val start = System.currentTimeMillis()
                val compressScale = p1.toFloat() / p0.max
                mDataBinding.blurImageView.compressScale = compressScale
                updateTimeText(System.currentTimeMillis() - start)
                updateText()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        mDataBinding.seekbarBlurRadius.progress = mDataBinding.blurImageView.blurRadius.toInt()
        mDataBinding.seekbarCompressScale.progress = (mDataBinding.blurImageView.compressScale * mDataBinding.seekbarCompressScale.max).toInt()

    }

    @SuppressLint("SetTextI18n")
    private fun updateText() {
        mDataBinding.textViewBlurRadius.text = "blurRadius:${mDataBinding.blurImageView.blurRadius}"
        mDataBinding.textViewCompressScale.text = "compressScale:${mDataBinding.blurImageView.compressScale}"
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeText(time: Long) {
        mDataBinding.textView.text = "$time ms"
    }

}