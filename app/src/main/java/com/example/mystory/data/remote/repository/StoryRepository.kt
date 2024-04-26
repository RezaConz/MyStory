package com.example.mystory.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystory.data.LoginPreferences
import com.example.mystory.data.remote.response.ErrorResponse
import com.example.mystory.data.remote.retrofit.ApiService
import com.example.mystory.data.Result
import com.example.mystory.data.local.StoryDatabase
import com.example.mystory.data.local.StoryRemoteMediator
import com.example.mystory.data.remote.response.ListStoryItem
import com.example.mystory.data.remote.response.LoginResult
import com.example.mystory.data.remote.retrofit.ApiConfig
import com.example.mystory.util.reduceFileImage
import com.example.mystory.util.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private var apiService: ApiService,
    private val pref: LoginPreferences,
    private val storyDatabase: StoryDatabase
){

       fun register(name: String, email: String, password: String) : LiveData<Result<ErrorResponse>> = liveData {
           emit(Result.Loading)
           try {
               val response = apiService.register(name, email, password)
               emit(Result.Success(response))
           } catch (e: HttpException) {
               val jsonInString = e.response()?.errorBody()?.string()
               val error = Gson().fromJson(jsonInString, ErrorResponse::class.java)
               emit(Result.Error(error.message.toString()))
           } catch (e: Exception) {
               emit(Result.Error("Error when caching API"))
           }
       }

    fun login(email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.login(email, password)
                val user = response.loginResult
                if (user != null) {
                    emit(Result.Success(user))
                } else {
                    emit(Result.Error("There was an error"))
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                emit(Result.Error(errorBody.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun uploadStory(getFile: File?, description: String, lat: Float? = null, lon: Float? = null): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        try {
            if (getFile != null) {
                val file = getFile.reduceFileImage()
                val desc = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                val response = apiService.uploadStory(imageMultipart, desc, lat, lon)
                emit(Result.Success(response))
            } else {
                emit(Result.Error("Error: No File Attached!"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = runBlocking {
                pref.getToken().first()
            }
            apiService = ApiConfig.getApiService(token.toString())
            val response = apiService.getStoriesWithLocation()
            val stories = response.listStoryItem
            emit(Result.Success(stories))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStoriesWithPaging(coroutineScope: CoroutineScope): LiveData<Result<PagingData<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = runBlocking {
                pref.getToken().first()
            }
            apiService = ApiConfig.getApiService(token.toString())
            val pager = Pager(
                config = PagingConfig(pageSize = 5 ),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
                pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
            )
            val pagingDataFlow = pager.flow.cachedIn(coroutineScope)
            pagingDataFlow.collect {
                emit(Result.Success(it))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

        companion object {
            @Volatile
            private var instance: StoryRepository? = null

            fun getInstance(apiService: ApiService, preferences: LoginPreferences, storyDatabase: StoryDatabase): StoryRepository =
                instance ?: synchronized(this) {
                    instance ?: StoryRepository(apiService,preferences,storyDatabase).also { instance = it }
                }
        }
}