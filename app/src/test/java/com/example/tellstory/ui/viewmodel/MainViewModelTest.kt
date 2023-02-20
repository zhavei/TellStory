package com.example.tellstory.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.data.DataDummy
import com.example.storyapp.helper.MainDispatcherRule
import com.example.storyapp.helper.getOrAwaitValue
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.repository.TellStoryRepository
import com.example.tellstory.ui.main.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var tellStoryRepository: TellStoryRepository

    @Test
    fun `when Paging Stories Should not null and return data`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<MainStory> = StoryPagingSource.snapshot(dummyStories)
        val expectedData = MutableLiveData<PagingData<MainStory>>()
        expectedData.value = data

        `when`(tellStoryRepository.fetchAllStories()).thenReturn(expectedData)

        val mainViewModel = MainViewModel(tellStoryRepository)

        val actualData = mainViewModel.listStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFFUTILS,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualData)

        //check paging data
        assertNotNull(differ.snapshot())
        //get size
        assertEquals(dummyStories.size, differ.snapshot().size)
        //get fist index
        assertEquals(dummyStories[0], differ.snapshot()[0])
        //get first indexes id
        assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)
        //get first id
        assertEquals(dummyStories.first().id, differ.snapshot().first()?.id)
        //get specific index
        assertEquals(dummyStories.get(7), differ.snapshot().get(7))
        //get last index
        assertEquals(dummyStories.lastIndex, differ.snapshot().lastIndex)
    }

    @Test
    fun `when Paging Stories empty, size should zero`() = runTest {
        val data: PagingData<MainStory> = StoryPagingSource.snapshot(listOf())
        val expectedData = MutableLiveData<PagingData<MainStory>>()
        expectedData.value = data

        `when`(tellStoryRepository.fetchAllStories()).thenReturn(expectedData)

        val mainViewModel = MainViewModel(tellStoryRepository)

        val actualData = mainViewModel.listStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFFUTILS,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualData)
        assertEquals(0, differ.snapshot().size)
    }


}

class StoryPagingSource : PagingSource<Int, LiveData<List<MainStory>>>() {
    companion object {
        fun snapshot(items: List<MainStory>): PagingData<MainStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<MainStory>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<MainStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}