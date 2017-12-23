package com.example.monia.zakupoholik.data;

import android.provider.BaseColumns;

/**
 * Created by Monia on 2017-12-20.
 */

public class ListsContract {
    public static final class ListsEntry implements BaseColumns {
        public static final String NAZWA_TABELI = "listy";
        public static final String ID_LISTA = "id_lista";
        public static final String NAZWA_LISTY = "nazwa_listy";
        public static final String DATA_ZAKUPOW = "data_zakupow";
        public static final String KOSZT_ZAKUPOW = "koszt_zakupow";
        public static final String ID_UZYTKOWNIKA = "id_uzytkownika";
    }
}
