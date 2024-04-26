package com.example.mystory.utils

import com.example.mystory.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryItems(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "id$i",
                "author $i",
                "desc $i",
                "www.photo.com/$i",
                "1-1-$i",
                i+1.toFloat(),
                i+2.toFloat()
            )
            items.add(story)
        }
        return items
    }
}