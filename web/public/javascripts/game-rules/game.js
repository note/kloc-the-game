define(['field', 'move', 'chessboard', 'color', 'rook', 'bishop', 'knight', 'queen', 'pawn'], function(Field, Move, ChessboardModule, Color, Rook, Bishop, Knight, Queen, Pawn){
    return {
        Field: Field,
        Move: Move,
        Chessboard: ChessboardModule.Chessboard,
        Color: Color,
        Rook: Rook,
        Bishop: Bishop,
        Knight: Knight,
        Queen: Queen,
        Pawn: Pawn
    };
});
