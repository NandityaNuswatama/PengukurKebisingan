package com.soundmeter.application.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.domain.DeleteDataUseCase
import com.soundmeter.application.domain.GetListDataUseCase
import com.soundmeter.application.domain.UploadDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getListDataUseCase: GetListDataUseCase,
    private val deleteDataUseCase: DeleteDataUseCase,
    private val uploadDataUseCase: UploadDataUseCase
) : ViewModel() {
    
    private val _soundList = MutableLiveData<List<SoundEntity>>()
    val soundList: LiveData<List<SoundEntity>> get() = _soundList

    private val _onSuccessUpload = MutableLiveData<Any>()
    val onSuccessUpload: LiveData<Any> get() = _onSuccessUpload

    private val _onSuccessDelete = MutableLiveData<Any>()
    val onSuccessDelete: LiveData<Any> get() = _onSuccessDelete

    private val _onFailureUpload = MutableLiveData<String>()
    val onFailureUpload: LiveData<String> get() = _onFailureUpload

    fun getListData(){
        _soundList.value = getListDataUseCase.invoke().sortedByDescending { it.id }
    }
    
    fun delete(id: Int){
        deleteDataUseCase.invoke(id)
        _onSuccessDelete.value = true
        viewModelScope.launch {
            delay(500)
            getListData()
        }
    }

    fun uploadData(data: List<SoundEntity>) {
        uploadDataUseCase.uploadAll(
            data,
            onSuccess = {
                _onSuccessUpload.value = true
                viewModelScope.launch {
                    delay(500)
                    getListData()
                }
            },
            onFailure = {
                _onFailureUpload.value = it
            }
        )
    }
}