package com.heathkev.quizado.utils

import com.heathkev.quizado.model.Result

class Utility {
    companion object{
        enum class Categories {
            All,
            Science,
            General,
            Math,
            History,
            Geography,
            Literature,
        }

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