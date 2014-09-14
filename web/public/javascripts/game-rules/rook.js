define(['piece', 'color', 'chessboardUtils', 'move'], function(Piece, Color, ChessboardUtils, Move){
    var Rook = function(color) {
        Piece.call(this, color);
        this.symbol = 'r';
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

    Rook.prototype.vectors = function() {
        var Vector = ChessboardUtils.Vector;
        return [new Vector(0, 1), new Vector(0, -1), new Vector(1, 0), new Vector(-1, 0)];
    }

    return Rook;
});
