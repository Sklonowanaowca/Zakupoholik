package com.example.monia.zakupoholik.data;

/**
 * Created by Monia on 2017-12-20.
 */

public class ListData {
    public int idLista;
    public String nazwaListy;
    public String dataZakupow;
    public double kosztZakupow;
    public int idUzytkownika;

    public ListData(){

    }

    public  String getNazwaListy(){
        return nazwaListy;
    }

    public String getDataZakupow(){
        return dataZakupow;
    }

    public int getIdUzytkownika(){
        return idUzytkownika;
    }
}
