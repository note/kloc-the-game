define(['piece', 'chessboard'], function(Piece, ChessboardModule){
    var ChessboardUtil = ChessboardModule.ChessboardUtil;

    var Bishop = function(color) {
        Piece.call(this, color);
    }

    Bishop.prototype.isLegalMove = function(chessboard, move) {
        return ChessboardUtil.sameDiagonal(move.from, move.to) && !chessboard.somethingBetween(move.from, move.to);
    }

    return Bishop;
});
