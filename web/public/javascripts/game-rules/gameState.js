'use strict'

define(['color', 'underscore'], function(Color, _){
    function OneColorGameState() {
        this.shortCastlingEnabled = true;
        this.longCastlingEnabled = true;
        this.enpassantProneColumn = null;
    }

    var GameState = function() {
        this.stateForWhite = new OneColorGameState();
        this.stateForBlack = new OneColorGameState();
    }

    GameState.prototype.forColor = function(color) {
        switch(color){
            case Color.white:
                return this.stateForWhite;
            case Color.black:
                return this.stateForBlack;
            default:
                throw new Error("Illegal color: " + color);
        }
    };

    GameState.prototype.clone = function() {
        var cloned = new GameState();
        cloned.stateForWhite = _.clone(this.stateForWhite);
        cloned.stateForBlack = _.clone(this.stateForBlack);
        return cloned;
    }

    return GameState;
});
