package com.heathkev.quizado.utils

import com.heathkev.quizado.data.Result

enum class Categories {
    Science,
    General,
    Math,
    History,
    Geography,
    Literature
}

class Utility {
    companion object{
        fun getCategoryResults() : List<Result>{
            return mutableListOf(
                Result("", "", "", "", Categories.Science.toString(), 0L, 0L, 0L),
                Result("", "", "", "", Categories.General.toString(), 0L, 0L, 0L),
                Result("", "", "", "", Categories.Math.toString(), 0L, 0L, 0L),
                Result("", "", "", "", Categories.History.toString(), 0L, 0L, 0L),
                Result("", "", "", "", Categories.Geography.toString(), 0L, 0L, 0L),
                Result("", "", "", "", Categories.Literature.toString(), 0L, 0L, 0L)
            )
        }
    }

}