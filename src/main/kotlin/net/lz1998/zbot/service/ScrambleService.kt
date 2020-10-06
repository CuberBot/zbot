@file:Suppress("unused")

package net.lz1998.zbot.service

import org.springframework.stereotype.Service
import java.util.*

@Service
class ScrambleService {
    private val random = Random()
    fun getScrambleSlidysim(n: Int): String {
        var result = ""
        val vector = Vector<Int>()
        for (i in 0 until n * n) {
            vector.add(i)
        }
        vector.shuffle(random)
        val num = Array(vector.size) { 0 }
        vector.toArray(num)

        // 如果不可解交换两个非0
        if (!isSolveable(num, n)) {
            var r1 = random.nextInt(n * n)
            if (num[r1] == 0) {
                r1 = (r1 + 1) % (n * n)
            }
            var r2 = random.nextInt(n * n)
            while (r1 == r2 || num[r2] == 0) {
                r2 = (r2 + 1) % (n * n)
            }
            val t = num[r1]
            num[r1] = num[r2]
            num[r2] = t
        }

        // 输出
        for (i in 0 until n * n) {
            if (i == 0) {
                // 第一个不用换行和空格
            } else if (i % n == 0) {
                result += "\r\n"
            } else {
                result += " "
            }
            result += String.format("%d", num[i])
        }
        return result
    }

    companion object {
        private fun isSolveable(arr: Array<Int>, n: Int): Boolean {
            val invCount = getInvCount(arr)
            if (n and 1 != 0) {
                return invCount and 1 == 0
            }
            val pos = fixXPosition(arr, n)
            return invCount xor pos and 1 == 0
        }

        private fun fixXPosition(arr: Array<Int>, n: Int): Int {
            for (i in arr.indices.reversed()) {
                if (arr[i] == 0) {
                    return n - 1 - i / n
                }
            }
            throw RuntimeException("生成失败")
        }

        private fun getInvCount(arr: Array<Int>): Int {
            var result = 0
            for (i in arr.indices) {
                for (j in i + 1 until arr.size) {
                    if (arr[i] != 0 && arr[j] != 0 && arr[i] > arr[j]) {
                        result++
                    }
                }
            }
            return result
        }
    }
}