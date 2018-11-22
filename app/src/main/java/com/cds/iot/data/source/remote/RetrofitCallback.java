package com.cds.iot.data.source.remote;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/7 17:37
 */
public abstract class RetrofitCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if(response.isSuccessful()) {
            onSuccess(call, response);
        } else {
            onFailure(call, new Throwable(response.message()));
        }
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    public void onLoading(long total, long progress) {

    }
}
