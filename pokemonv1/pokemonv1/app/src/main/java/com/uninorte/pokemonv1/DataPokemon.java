package com.uninorte.pokemonv1;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Andres Villa on 16/09/2016.
 */
@Table(database = AppDataBase.class)
public class DataPokemon extends BaseModel implements Serializable{

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    public int idPo;

    @Column
    public String name;

    @Column
    public String type;

    @Column
    public int total;

    @Column
    public int hp;

    @Column
    public int attack;

    @Column
    public int defense;

    @Column
    public int spattack;

    @Column
    public int spdefense;

    @Column
    public int speed;

    @Column
    public int evol;

    @Column
    public String imgfront;

    @Column
    public String imgback;

    @Column
    public String giffront;

    @Column
    public String gifback;

    @Column
    public String imgurl;

    public DataPokemon(){}

    public DataPokemon(int data1,String data2,String data3,int data4, int data5, int data6, int data7, int data8, int data9, int data10, int data11,
                       String data12,String data13, String data14, String data15, String data16){
        idPo=data1; name = data2; type=data3; total=data4; hp=data5; attack=data6; defense=data7;
        spattack=data8; spdefense = data9; speed=data10; evol=data11; imgfront=data12; imgback=data13; giffront= data14;
        gifback=data15; imgurl=data16;
    }


}
