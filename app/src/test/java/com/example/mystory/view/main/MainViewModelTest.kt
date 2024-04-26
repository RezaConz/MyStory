package com.example.mystory.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.mystory.adapter.StoryAdapter
import com.example.mystory.data.LoginPreferences
import com.example.mystory.data.remote.repository.StoryRepository
import com.example.mystory.data.remote.response.ListStoryItem
import com.example.mystory.utils.DataDummy
import com.example.mystory.utils.MainDispatcherRule
import com.example.mystory.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import com.example.mystory.data.Result
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var preferences: LoginPreferences
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(storyRepository, preferences)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryItems()
        val data: PagingData<ListStoryItem> = StoryPagingSourceTest.snapshot(dummyStory)
        val expectedStory = MutableLiveData<Result<PagingData<ListStoryItem>>>()
        expectedStory.value = Result.Success(data)
        Mockito.`when`(storyRepository.getAllStoriesWithPaging(viewModel.viewModelScope)).thenReturn(expectedStory)

        val actualStory = viewModel.getStories.getOrAwaitValue()
        val successResult = actualStory as Result.Success
        val pagingData = successResult.data

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.diffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)

        Assert.assertNotNull(actualStory)
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories is Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<Result<PagingData<ListStoryItem>>>()
        expectedStory.value = Result.Success(data)
        Mockito.`when`(storyRepository.getAllStoriesWithPaging(viewModel.viewModelScope)).thenReturn(expectedStory)

        val actualStory = viewModel.getStories.getOrAwaitValue()
        val successResult = actualStory as Result.Success
        val pagingData = successResult.data

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.diffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSourceTest : PagingSource<Int, LiveData<List<ListStoryItem>>>() {

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}