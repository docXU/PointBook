package com.debug.xxw.pointbook.viewmodel;

import android.util.Log;

import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.adapter.SearchHistoryAdapter;

import java.lang.reflect.Field;
import java.util.List;

public class RecommendSearchFragment extends SearchFragment {
    /**
     * 添加推荐项列表至候选列表中
     * @param items
     */
    public void insertRecommendList(List<String> items){
        //反射获取适配器对象
        try{
            //TODO:只有往SearchFragment里的history链表加数据然后notify
            SearchHistoryAdapter sha = (SearchHistoryAdapter) get(this, "searchHistoryAdapter");
        }catch (Exception e){
            Log.e("reflectErr", e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object get(Object instance, String variableName)
    {
        Class targetClass = instance.getClass().getSuperclass();
        SearchFragment superInst = (SearchFragment)targetClass.cast(instance);
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);
            //修改访问限制
            field.setAccessible(true);
            // superInst 为 null 可以获取静态成员
            // 非 null 访问实例成员
            return field.get(superInst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
