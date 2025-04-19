package tasks

import contributors.User

/*
TODO: Write aggregation code.

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/
fun List<User>.aggregate(): List<User> =
    groupBy { it.login }
        .map { (login, group) -> User(login, group.sumOf { it.contributions }) }
        .sortedByDescending { it.contributions }


//简单看下 groupBy 和 map 的实现
fun <T, K> Iterable<T>.customGroupBy(keySelector: (T) -> K): Map<K, List<T>> {
    val result = mutableMapOf<K, MutableList<T>>()
    for (element in this) {
        val key = keySelector(element)
        val list = result.getOrPut(key) { mutableListOf() }
        list.add(element)
    }
    return result
}

//map 对应List和Map有不同的实现（参数类型不同，Map对应的是Map.Entry）
fun <T, R> Iterable<T>.customMap(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    for (element in this) {
        result.add(transform(element))
    }
    return result
}
