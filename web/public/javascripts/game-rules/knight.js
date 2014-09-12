define(['piece', 'chessboardUtils'], function(Piece, ChessboardUtils){
    var Knight = function(color) {
        Piece.call(this, color);
    }

    Knight.prototype = Object.create(Piece.prototype);

    Knight.prototype.isLegalMove = function(chessboard, move, gameState) {
        var columnsDiff = Math.abs(move.to.column - move.from.column);
        var rowsDiff = Math.abs(move.to.row - move.from.row);
        return (columnsDiff === 2 && rowsDiff === 1) || (columnsDiff === 1 && rowsDiff === 2);
    }

    Knight.prototype.vectors = function() {
        var Vector = ChessboardUtils.Vector;
        return [new Vector(1, 2), new Vector(2, 1), new Vector(2, -1), new Vector(1, -2), new Vector(-1, -2), new Vector(-2, -1), new Vector(-2, 1), new Vector(-1, 2)];
    }

    return Knight;
});
