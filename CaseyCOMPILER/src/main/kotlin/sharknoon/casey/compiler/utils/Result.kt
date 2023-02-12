package sharknoon.casey.compiler.utils

/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
sealed class Result<out T> {
    data class Error(val message: String = "", val e: Exception = Exception()) : Result<Nothing>()
    data class Success<T>(val value: T? = null) : Result<T>()
}

fun <T, R> Result<T>.ifSuccessful(func: (T?) -> R) {
    if (this is Result.Success) {
        func(this.value)
    }
}