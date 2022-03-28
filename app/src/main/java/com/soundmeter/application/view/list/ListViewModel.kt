package com.soundmeter.application.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundmeter.application.data.SoundEntity
import com.soundmeter.application.domain.DeleteDataUseCase
import com.soundmeter.application.domain.GetListDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getListDataUseCase: GetListDataUseCase,
    private val deleteDataUseCase: DeleteDataUseCase,
) : ViewModel() {
    
    private val _soundList = MutableLiveData<List<SoundEntity>>()
    val soundList: LiveData<List<SoundEntity>> get() = _soundList
    
    
    fun getListData(){
        viewModelScope.launch {
            _soundList.value = getListDataUseCase.invoke()
        }
    }
    
    fun delete(id: Int){
        runBlocking {
            viewModelScope.launch {
                deleteDataUseCase.invoke(id)
            }
        }
    }
}