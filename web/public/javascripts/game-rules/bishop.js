define(['piece', 'chessboardUtils'], function(Piece, ChessboardUtils){
    var Bishop = function(color) {
        Piece.call(this, color);
    }

    Bishop.prototype = Object.create(Piece.prototype);

    Bishop.prototype.isLegalMove = function(chessboard, move, gameState) {
        return ChessboardUtils.sameDiagonal(move.from, move.to) && !chessboard.somethingBetween(move.from, move.to);
    }

    return Bishop;
});
