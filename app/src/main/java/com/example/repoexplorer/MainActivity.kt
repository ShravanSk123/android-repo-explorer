package com.example.repoexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val viewModel: RepoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RepoExplorerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun RepoExplorerScreen(viewModel: RepoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("GitHub username") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.searchRepos(username) }) { Text("Search") }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is RepoUiState.Idle -> Text("Enter a username to see their public repos")
            is RepoUiState.Loading -> CircularProgressIndicator()
            is RepoUiState.Error -> Text("Error: ${state.message}")
            is RepoUiState.Success -> LazyColumn {
                items(state.repos) { repo ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(repo.name, style = MaterialTheme.typography.titleMedium)
                            repo.description?.let { Text(it) }
                            Text("★ ${repo.stargazersCount}")
                        }
                    }
                }
            }
        }
    }
}