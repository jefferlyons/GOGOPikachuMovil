package com.uninorte.pokemonv1;

/**
 * Created by Andres Villa on 12/09/2016.
 */
public class Pokemon {
    String Id,Name;
    String Type;
    int Total, HP, Attack, Defense, SpAttack;
    int SpDefense, Speed;
    int Evol;
    String ImgFront, ImgBack,GifFront, GifBack, ImgUrl;
    public Pokemon(String Id, String Name, String Type, int Total, int HP, int Attact, int Defense, int SpAttack, int SpDefense, int Speed,
                   int Evol, String ImgFront, String ImgBack, String GifFront, String GifBack, String ImgUrl){
        this.Id = Id; this.Name = Name; this.Type = Type; this.Total = Total; this.HP = HP; this.Attack = Attact; this.Defense = Defense;
        this.SpAttack = SpAttack; this.SpDefense = SpDefense; this.Speed = Speed; this.Evol = Evol; this.ImgFront = ImgFront;
        this.ImgBack = ImgBack; this.GifFront = GifFront; this.GifBack = GifBack; this.ImgUrl = ImgUrl;
    }
}
