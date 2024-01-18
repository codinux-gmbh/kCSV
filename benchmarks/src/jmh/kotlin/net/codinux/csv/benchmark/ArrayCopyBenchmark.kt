package net.codinux.csv.benchmark

import org.openjdk.jmh.annotations.*
import kotlin.random.Random

/**
 * Compares different methods to copy the content from one array to another.
 * Copying via iterating over indices turned out to be the fasted method. See results in
 * [results_ArrayCopyBenchmark.txt](../../../../../../docs/results/results_ArrayCopyBenchmark.txt)
 * [a](utils/Constants.kt)
 */
@BenchmarkMode(Mode.Throughput)
class ArrayCopyBenchmark {

    companion object {
        private val bufferSize = 8192

        private val countBuffers = 200

        private val destination = CharArray(bufferSize)
    }

    @State(Scope.Benchmark)
    class Buffers {

        private val random = Random(System.nanoTime())

        private var currentIndex = 0

        private val buffers: List<CharArray> = buildList(countBuffers) {
            (0 until countBuffers).forEach {
                add(CharArray(bufferSize) { random.nextInt(Char.MAX_VALUE.code).toChar() })
            }
        }

        fun next(): CharArray {
            if (currentIndex >= countBuffers) {
                currentIndex = 0
            }

            return buffers[currentIndex++]
        }

    }


    @Benchmark
    fun copyInto(buffers: Buffers) {
        val origin = buffers.next()

        origin.copyInto(destination, 0, 0, bufferSize)
    }

    @Benchmark
    fun copyViaWhileIterating(buffers: Buffers) {
        val origin = buffers.next()

        var index = 0
        while (index < bufferSize) {
            destination[index] = origin[index]
            index++
        }
    }

    @Benchmark
    fun copyViaIndexIterating(buffers: Buffers) {
        val origin = buffers.next()

        for (index in 0..<bufferSize) {
            destination[index] = origin[index]
        }
    }
}