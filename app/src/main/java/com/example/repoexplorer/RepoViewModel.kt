package com.example.repoexplorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RepoUiState {
    object Idle : RepoUiState()
    object Loading : RepoUiState()
    data class Success(val repos: List<Repo>) : RepoUiState()
    data class Error(val message: String) : RepoUiState()
}

class RepoViewModel(
    private val repository: GitHubRepository = GitHubRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepoUiState>(RepoUiState.Idle)
    val uiState: StateFlow<RepoUiState> = _uiState.asStateFlow()

    fun searchRepos(username: String) {
        if (username.isBlank()) {
            _uiState.value = RepoUiState.Error("Enter a username first")
            return
        }
        _uiState.value = RepoUiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = RepoUiState.Success(repository.getPublicRepos(username.trim()))
            } catch (e: Exception) {
                _uiState.value = RepoUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}