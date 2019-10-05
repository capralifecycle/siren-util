@file:JvmName("ListUtil")
package no.capraconsulting.siren.internal.util

import java.util.function.Function

@Deprecated("Use Kotlin stdlib", ReplaceWith("list.map(map::apply)"))
internal fun <T, R> map(list: List<T>, map: Function<T, R>): List<R> =
    list.map(map::apply)
