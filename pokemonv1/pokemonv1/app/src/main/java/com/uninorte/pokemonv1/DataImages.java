package com.uninorte.pokemonv1;

import android.graphics.Bitmap;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Andres Villa on 13/09/2016.
 */
@Table(database = AppDataBase.class)
public class DataImages extends BaseModel implements Serializable {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    public long data1;

    @Column
    public String data2;

    public DataImages(){}

    public DataImages(long data1, String data2){
        this.data1 = data1;
        this.data2 = data2;
    }
}
