define([], function() {
    var Piece = function(color) {
        this.color = color;
    };

    Piece.prototype.applyMove = function(chessboard, move, gameState) {
        return gameState;
    };

    Piece.prototype.canCheck = function(chessboard, move, gameState) {
        return this.isLegalMove(chessboard, move, gameState);
    }

    return Piece;
});