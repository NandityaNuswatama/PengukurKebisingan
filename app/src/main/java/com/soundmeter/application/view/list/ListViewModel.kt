package com.soundmeter.application.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.domain.DeleteDataUseCase
import com.soundmeter.application.domain.GetListDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getListDataUseCase: GetListDataUseCase,
    private val deleteDataUseCase: DeleteDataUseCase,
) : ViewModel() {
    
    private val _soundList = MutableLiveData<List<SoundEntity>>()
    val soundList: LiveData<List<SoundEntity>> get() = _soundList

    fun getListData(){
        _soundList.value = getListDataUseCase.invoke().sortedByDescending { it.id }
    }
    
    fun delete(id: Int){
        deleteDataUseCase.invoke(id)
        getListData()
    }
}