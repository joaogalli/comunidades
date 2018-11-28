package br.com.sovi.comunidades.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StatesAndCities {

    public List<State> data;

    public StatesAndCities(Context context) {
        Type REVIEW_TYPE = new TypeToken<List<State>>() {
        }.getType();
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            InputStream inputStream = context.getAssets().open("stateCities.json");
            reader = new JsonReader(new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = gson.fromJson(reader, REVIEW_TYPE); // contains the whole reviews list
    }

    public List<State> getStates() {
        return data;
    }

    public List<String> getStateNames() {
        List<String> stateNames = new ArrayList<>();

        if (data != null)
            for (State state : data) {
                stateNames.add(state.nome);
            }
        return stateNames;
    }

    public class State {

        private String sigla;

        private String nome;

        private String[] cidades;

        @Override
        public String toString() {
            return nome;
        }

        public String getSigla() {
            return sigla;
        }

        public void setSigla(String sigla) {
            this.sigla = sigla;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String[] getCidades() {
            return cidades;
        }

        public void setCidades(String[] cidades) {
            this.cidades = cidades;
        }
    }

}
