define([], function() {
    var Piece = function(color) {
        this.color = color;
    };

    Piece.prototype.applyMove = function(chessboard, move, gameState) {
        return gameState;
    };

    return Piece;
});