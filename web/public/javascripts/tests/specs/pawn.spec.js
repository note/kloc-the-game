'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

        describe("Pawn", function() {
                it("can move forward by 2 fields from start position", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e2"), new Game.Pawn(Game.Color.white));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")))).toBe(true);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e2"), new Game.Field("e5")))).toBe(false);

                    chessboard.setPiece(new Game.Field("d7"), new Game.Pawn(Game.Color.black));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d7"), new Game.Field("d5")))).toBe(true);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d7"), new Game.Field("d4")))).toBe(false);
                });

                it("can move forward by 1 field from start position", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e2"), new Game.Pawn(Game.Color.white));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e2"), new Game.Field("e3")))).toBe(true);

                    chessboard.setPiece(new Game.Field("d7"), new Game.Pawn(Game.Color.black));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d7"), new Game.Field("d6")))).toBe(true);
                });
                

                it("cannot move forward by 2 fields from non-start position", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e2"), new Game.Pawn(Game.Color.white));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e3"), new Game.Field("e5")))).toBe(false);

                    chessboard.setPiece(new Game.Field("d7"), new Game.Pawn(Game.Color.black));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d6"), new Game.Field("d5")))).toBe(false);
                });

                it("move forward by 1 field from non-start position", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e3"), new Game.Pawn(Game.Color.white));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e3"), new Game.Field("e4")))).toBe(true);

                    chessboard.setPiece(new Game.Field("d6"), new Game.Pawn(Game.Color.black));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d6"), new Game.Field("d5")))).toBe(true);
                });

                it("cannot move backwards", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e4"), new Game.Pawn(Game.Color.white));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e3")))).toBe(false);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e2")))).toBe(false);

                    chessboard.setPiece(new Game.Field("d7"), new Game.Pawn(Game.Color.black));
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d5"), new Game.Field("d6")))).toBe(false);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d5"), new Game.Field("d7")))).toBe(false);
                });

                it("take on diagonal 1 field forward", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e4"), new Game.Pawn(Game.Color.white));
                    chessboard.setPiece(new Game.Field("d5"), new Game.Pawn(Game.Color.black));
                    chessboard.setPiece(new Game.Field("f5"), new Game.Pawn(Game.Color.black));

                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("d5")))).toBe(true);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f5")))).toBe(true);
                });

                it("cannot take on diagonal further than 1 field forward", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e4"), new Game.Pawn(Game.Color.white));
                    chessboard.setPiece(new Game.Field("c6"), new Game.Pawn(Game.Color.black));
                    chessboard.setPiece(new Game.Field("b7"), new Game.Pawn(Game.Color.black));

                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c6")))).toBe(false);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b7")))).toBe(false);
                });

                it("cannot overleap when forward by one field", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e4"), new Game.Pawn(Game.Color.white));
                    chessboard.setPiece(new Game.Field("e5"), new Game.Pawn(Game.Color.black));

                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e5")))).toBe(false);
                });

                it("cannot overleap when forward by two fields", function() {
                    var chessboard = new Game.Chessboard();

                    chessboard.setPiece(new Game.Field("e2"), new Game.Pawn(Game.Color.white));
                    chessboard.setPiece(new Game.Field("e3"), new Game.Pawn(Game.Color.white));

                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")))).toBe(false);
                });
                
                it("enpassant right after enemy pawn move", function() {

                });


                it("cannot enpassant later", function() {
                });

                it("enpassant only after enemy two field-forward move", function() {});
                        

                it("be promoted to any piece", function() {});

                it("can be pinned", function() {});


                it("isAnyMovePossible can return true", function() {});


                it("isAnyMovePossible can return false", function() {});
        });

    }
);
