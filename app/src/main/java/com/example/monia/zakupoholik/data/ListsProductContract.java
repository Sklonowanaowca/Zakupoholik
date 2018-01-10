package com.example.monia.zakupoholik.data;

import android.provider.BaseColumns;

/**
 * Created by Monia on 2017-12-20.
 */

public class ListsProductContract {
    public static final class ListsEntry implements BaseColumns {
        public static final String NAZWA_TABELI = "listy";
        public static final String ID_LISTA = "id_lista";
        public static final String NAZWA_LISTY = "nazwa_listy";
        public static final String DATA_ZAKUPOW = "data_zakupow";
        public static final String KOSZT_ZAKUPOW = "koszt_zakupow";
        public static final String ID_UZYTKOWNIKA = "id_uzytkownika";

        public static final String PRODUKT_NAZWA_TABELI = "produkty";
        public static final String PRODUKT_ID_PRODUKT = "id_produkt";
        public static final String PRODUKT_ILOSC = "ilosc";
        public static final String PRODUKT_JEDNOSTKA = "jednostka";
        public static final String PRODUKT_CENA = "cena";
        public static final String PRODUKT_ID_LISTA = "id_lista";
        public static final String PRODUKT_NAZWA = "produkt_nazwa";
        public static final String PRODUKT_ID_SKLEP = "id_sklep";


        public static final String SKLEP_NAZWA_TABELI = "sklep";
        public static final String SKLEP_ID_SKLEP = "id_sklep";
        //public static final String SKLEP_NAZWA = "nazwa_sklepu";
        public static final String SYGNATURA = "sygnatura";
    }
}
