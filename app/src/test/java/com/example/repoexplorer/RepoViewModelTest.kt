package com.example.repoexplorer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeGitHubRepository(private val repos: List<Repo>) : GitHubRepository {
    override suspend fun getPublicRepos(username: String) = repos
}

@OptIn(ExperimentalCoroutinesApi::class)
class RepoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before fun setup() = Dispatchers.setMain(testDispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `blank username produces an error state`() = runTest {
        val viewModel = RepoViewModel(FakeGitHubRepository(emptyList()))
        viewModel.searchRepos("")
        assertTrue(viewModel.uiState.value is RepoUiState.Error)
    }

    @Test
    fun `successful search produces success state with repos`() = runTest {
        val fakeRepos = listOf(Repo("hello-world", "desc", 10, "https://x"))
        val viewModel = RepoViewModel(FakeGitHubRepository(fakeRepos))
        viewModel.searchRepos("octocat")
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state is RepoUiState.Success)
        assertEquals(fakeRepos, (state as RepoUiState.Success).repos)
    }
}