define(['move', 'funUtils', 'color'], function(Move, FunUtils, Color) {
    var Piece = function(color) {
        this.color = color;
    };

    Piece.prototype.applyMove = function(chessboard, move, gameState) {
        return gameState;
    };

    Piece.prototype.anyMovePossible = function(chessboard, field, passedGameState) {
        var vectorsToCheck = this.vectors();
        return FunUtils.exists(vectorsToCheck, function(vector){
            var move = Move.fromVector(field, vector);
            return move && chessboard.isLegalMove(move, passedGameState);
        });
    }

    Piece.prototype.toString = function() {
        return this.color == Color.white ? this.symbol.toUpperCase() : this.symbol.toLowerCase();
    };

    return Piece;
});