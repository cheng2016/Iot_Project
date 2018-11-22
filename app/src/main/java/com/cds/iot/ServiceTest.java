package com.cds.iot;

import com.cds.iot.data.entity.NewsList;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.google.gson.Gson;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by chengzj on 2017/6/18.
 */

public class ServiceTest {

    public static void main(String[] args) throws Exception {
        HttpApi iMainService = HttpFactory.createRetrofit2(HttpApi.class);
        Disposable disposable = iMainService.getNewsList("L295","10")
                .subscribe(new Consumer<NewsList>() {
                    @Override
                    public void accept(@NonNull NewsList newsList) throws Exception {
                        System.out.println(new Gson().toJson(newsList));
                    }
                });
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposable);
    }
}
