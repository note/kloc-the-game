define(['move'], function(Move) {
    var Piece = function(color) {
        this.color = color;
    };

    Piece.prototype.applyMove = function(chessboard, move, gameState) {
        return gameState;
    };

    Piece.prototype.anyMovePossible = function(chessboard, field, passedGameState) {
        var vectorsToCheck = this.vectors();
        var found = _.find(vectorsToCheck, function(vector){
            var move = Move.fromVector(field, vector);
            return move && chessboard.isLegalMove(move, passedGameState);
        });
        return found !== undefined;
    }

    return Piece;
});