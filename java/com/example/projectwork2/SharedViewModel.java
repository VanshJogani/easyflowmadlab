package com.example.projectwork2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<List<String>> expenses = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> groups = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> myString = new MutableLiveData<>();
    private final MutableLiveData<Integer> user_id = new MutableLiveData<>();
    public void setMyString(String string) {
        myString.setValue(string);
    }
    public void setUser_id(int id){
        user_id.setValue(id);
    }
    public LiveData<Integer> getUser_id(){
        return user_id;
    }
    public LiveData<String> getMyString() {
        return myString;//Username
    }
    public void addExpense(String desc, String cost, String date) {
        List<String> currentList = expenses.getValue();
        currentList.add(desc + " - Rs. " + cost + " - " + date);
        expenses.setValue(currentList);
    }

    public LiveData<List<String>> getExpenses() {
        return expenses;
    }

    public void addGroup(String groupName) {
        List<String> currentGroups = groups.getValue();
        currentGroups.add(groupName);
        groups.setValue(currentGroups);
    }

    public LiveData<List<String>> getGroups() {
        return groups;
    }
}

