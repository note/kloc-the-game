define(['field', 'move', 'chessboard', 'color', 'rook', 'bishop', 'knight', 'queen'], function(Field, Move, ChessboardModule, Color, Rook, Bishop, Knight, Queen){
    return {
        Field: Field,
        Move: Move,
        Chessboard: ChessboardModule.Chessboard,
        Color: Color,
        Rook: Rook,
        Bishop: Bishop,
        Knight: Knight,
        Queen: Queen,
    };
});
