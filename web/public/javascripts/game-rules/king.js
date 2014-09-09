define(['piece', 'gameState', 'move', 'underscore'], function(Piece, GameState, Move, _){
    var King = function(color) {
        Piece.call(this, color);
    }

    King.prototype.isLegalMove = function(chessboard, move, gameState) {
        var columnDiff = Math.abs(move.to.column - move.from.column);
        var rowDiff = Math.abs(move.to.row - move.from.row);
        return (rowDiff > 0 || columnDiff > 0) && (rowDiff < 2 && columnDiff < 2);
    }

    King.prototype.isChecked = function(chessboard, field, passedGameState){
        var gameState = passedGameState || new GameState();

        var enemyPiecesFields = chessboard.getFieldsWithPiecesOfColor(this.color.enemy());
        var found = _.find(enemyPiecesFields, function(enemyPieceField){
            return chessboard.canCheck(new Move(enemyPieceField, field), gameState);
        });
        return found !== undefined;
    }

//    King.prototype.applyMove = function(chessboard, move, gameState) {
//        return gameState;
//    }

    return King;
});
