package com.liangguo.imageblur

import android.util.Log
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * @author ldh
 * 时间: 2022/2/22 13:10
 * 邮箱: 2637614077@qq.com
 *
 * 实时任务执行器。
 * 当队列中的任务堆积到一定程度时，会移除掉队头的任务，再队尾处插入新任务。
 *
 * @param maxTaskQueueSize 任务队列可容纳的最大任务数量。
 * @param corePoolSize 核心线程数。
 * @param maxPoolSize 线程池中最大线程数。
 */
class RealtimeExecutor(
    private val maxTaskQueueSize: Int = 3,
    private val corePoolSize: Int = 1,
    private val maxPoolSize: Int = 3,
) {

    /**
     * 内部执行任务的线程池
     */
    private var executor = newThreadPoolExecutor()

    /**
     * 检查执行器是否有效，失效后就换新
     */
    private fun checkExecutor() {
        if (executor.isTerminating || executor.isTerminated || executor.isShutdown) {
            executor = newThreadPoolExecutor()
        }
    }

    /**
     * 创建一个新的线程池
     */
    private fun newThreadPoolExecutor() =
        ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            60L,
            TimeUnit.SECONDS,
            ArrayBlockingQueue(maxTaskQueueSize)
        )

    /**
     * 提交一个新的任务
     */
    fun submit(task: () -> Unit) {
        checkExecutor()
        while (executor.queue.size >= maxTaskQueueSize) {
            executor.queue.poll()
        }
        executor.submit(task)
    }

    /**
     * 立即停止掉当前的所有任务以及活跃中的线程池。
     * 停止之后，但是当您再次提交任务时，会产生新的线程池来运行。
     */
    fun shutdownNow() {
        try {
            executor.shutdownNow()
        } catch (e: Exception) {
            Log.e(javaClass.name, "shutdownNow()", e)
        }
    }

}