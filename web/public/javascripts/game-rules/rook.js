define(['piece', 'color'], function(Piece, Color){
    var Rook = function(color) {
        Piece.call(this, color);
    };

    Rook.prototype = Object.create(Piece.prototype);

    Rook.prototype.isLegalMove = function(chessboard, move, gameState) {
        return (move.from.sameRow(move.to) || move.from.sameColumn(move.to)) && !chessboard.somethingBetween(move.from, move.to);
    }

    Rook.prototype.applyMove = function(chessboard, move, gameState) {
        var firstRow = this.color === Color.white ? 0 : 7;
        if(move.from.row === firstRow && move.from.column === 0){
            gameState.forColor(this.color).longCastlingEnabled = false;
        }

        if(move.from.row === firstRow && move.from.column === 7){
            gameState.forColor(this.color).shortCastlingEnabled = false;
        }

        return gameState;
    }

    return Rook;
});
