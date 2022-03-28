package com.soundmeter.application.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundmeter.application.data.SoundEntity
import com.soundmeter.application.domain.DeleteDataUseCase
import com.soundmeter.application.domain.GetDetailDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailDataUseCase: GetDetailDataUseCase,
    private val deleteDataUseCase: DeleteDataUseCase,
) : ViewModel() {

    private val _detailData = MutableLiveData<SoundEntity>()
    val detailData: LiveData<SoundEntity> get() = _detailData
    
    fun getDetailData(id: Int){
        
        viewModelScope.launch {
    
            _detailData.value = getDetailDataUseCase.invoke(id)
        }
    }
    
    fun deleteData(id: Int){
        runBlocking {
            viewModelScope.launch {
                deleteDataUseCase.invoke(id)
            }
        }
    }
}