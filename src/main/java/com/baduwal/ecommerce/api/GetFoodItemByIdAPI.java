package com.baduwal.ecommerce.api;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.searchers.FoodItemSearcher;

public class GetRoodItemByIdAPI {
    public FoodItem getFoodItemById(int id) {
        if(id < 0){
            return null;
        }

        return new FoodItemSearcher().searchById(id);
    }
}
