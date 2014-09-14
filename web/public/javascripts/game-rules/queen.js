define(['piece', 'chessboardUtils'], function(Piece, ChessboardUtils){
    var Queen = function(color) {
        Piece.call(this, color);
        this.symbol = 'q';
    }

    Queen.prototype = Object.create(Piece.prototype);

    Queen.prototype.isLegalMove = function(chessboard, move, gameState) {
        var isRookLikeMove = (move.from.sameRow(move.to) || move.from.sameColumn(move.to));
        var isBishopLikeMove = ChessboardUtils.sameDiagonal(move.from, move.to);
        return (isRookLikeMove || isBishopLikeMove) && !chessboard.somethingBetween(move.from, move.to);
    }

    Queen.prototype.vectors = function() {
        var Vector = ChessboardUtils.Vector;
        return [new Vector(0, 1), new Vector(0, -1), new Vector(1, 0), new Vector(-1, 0)];
    }

    return Queen;
});
