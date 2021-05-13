package com.example.surajbokankar.ssomanager.common;

import com.example.surajbokankar.ssomanager.model.ErrorResponse;

/**
 * Created by suraj.bokankar on 20/3/17.
 */

public interface AuthResponseInterface<T> {

    public void onSuccess(T response);

    public void onFailureError(ErrorResponse error);

}
