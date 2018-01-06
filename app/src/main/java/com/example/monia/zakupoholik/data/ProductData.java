package com.example.monia.zakupoholik.data;

/**
 * Created by Monia on 2018-01-03.
 */

public class ProductData {
    public int idProdukt;
    public String nazwa;
    public double ilosc;
    public String jednostka;
    public double cena;
    public long idLista;
    public int idSklep;

    public ProductData(){}

    public int getIdProdukt(){return idProdukt;}

    public String getNazwa(){return nazwa;}

    public double getIlosc(){return ilosc;}

    public String getJednostka(){return jednostka;}

    public double getCena(){return cena;}

    public long getIdLista(){return idLista;}

    public int getIdSklep(){return idSklep;}
}
