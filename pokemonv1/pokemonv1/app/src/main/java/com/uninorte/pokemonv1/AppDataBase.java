package com.uninorte.pokemonv1;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Andres Villa on 4/09/2016.
 */
@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {
    public static final String NAME = "DataBasePokemon4";

    public static final int VERSION = 1;
}
