define([], function(){

    var white = {}
    var black = {};

    white.enemy = function(){
        return black;
    };

    white.toString = function(){
        return "w";
    }

    black.enemy = function(){
        return white;
    };

    black.toString = function(){
        return "b";
    }

    var Color = Object.freeze({
        white: white,
        black: black,
        fromString: function(str){
            switch(str){
                case "w":
                    return white;
                case "b":
                    return black;
                default:
                    return undefined;
            }
        }
    });

    return Color;
});