package br.com.sovi.comunidades.ui.base;

import android.content.Context;

public class BasePresenter {

    private Context context;

    public BasePresenter(Context context) {
        this.context = context;
    }

    public void init() {
    }

    public Context getContext() {
        return context;
    }
}
