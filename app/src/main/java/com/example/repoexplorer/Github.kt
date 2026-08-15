package com.example.repoexplorer

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class Repo(
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("html_url") val htmlUrl: String
)

interface GitHubApiService {
    @GET("users/{username}/repos")
    suspend fun getRepos(@Path("username") username: String): List<Repo>
}

interface GitHubRepository {
    suspend fun getPublicRepos(username: String): List<Repo>
}

class GitHubRepositoryImpl : GitHubRepository {
    private val api: GitHubApiService = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GitHubApiService::class.java)

    override suspend fun getPublicRepos(username: String): List<Repo> = api.getRepos(username)
}