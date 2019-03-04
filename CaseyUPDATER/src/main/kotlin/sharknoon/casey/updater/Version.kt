package sharknoon.casey.updater

import kotlin.math.min

class Version(private val list: List<Int>) {

    operator fun compareTo(v: Version): Int {
        val otherList = v.list
        for (i in 0..min(list.size, otherList.size)) {
            if (list[i] > otherList[i]) {
                return 1
            } else if (list[i] < otherList[i]) {
                return -1
            }
        }
        return 0
    }

    override fun toString() = list.joinToString(".")
}

fun String.toVersion() = Version(this.split(".").map { it.toIntOrNull() }.map { it ?: 0 })
