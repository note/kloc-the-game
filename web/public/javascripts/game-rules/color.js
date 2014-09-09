define([], function(){

    var white = {}
    var black = {};

    white.enemy = function(){
        return black;
    };

    black.enemy = function(){
        return white;
    };

    var Color = Object.freeze({
        white: white,
        black: black
    });

    return Color;
});