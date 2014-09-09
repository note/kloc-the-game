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
                
                it("enpassant right after enemy pawn move (white version)", function() {
                    var chessboard = Game.ChessboardFactory.getInitialPosition();

                    var gameState = chessboard.applyMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")), new Game.GameState());
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("a7"), new Game.Field("a6")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("e4"), new Game.Field("e5")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("f7"), new Game.Field("f5")), gameState);

                    expect(gameState.forColor(Game.Color.black).enpassantProneColumn).toEqual(5);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e5"), new Game.Field("f6")), gameState)).toBe(true);
                });

                // TODO: two versions of the same test are quite ugly
                it("enpassant right after enemy pawn move (black version)", function() {
                    var chessboard = Game.ChessboardFactory.getInitialPosition();

                    var gameState = chessboard.applyMove(new Game.Move(new Game.Field("a2"), new Game.Field("a3")), new Game.GameState());
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("e7"), new Game.Field("e5")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("a3"), new Game.Field("a4")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("e5"), new Game.Field("e4")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("f2"), new Game.Field("f4")), gameState);

                    expect(gameState.forColor(Game.Color.white).enpassantProneColumn).toEqual(5);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f3")), gameState)).toBe(true);
                });


                it("cannot enpassant later", function() {
                    var chessboard = Game.ChessboardFactory.getInitialPosition();

                    var gameState = chessboard.applyMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")), new Game.GameState());
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("a7"), new Game.Field("a6")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("e4"), new Game.Field("e5")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("f7"), new Game.Field("f5")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("b2"), new Game.Field("b3")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("b7"), new Game.Field("b6")), gameState);

                    expect(gameState.forColor(Game.Color.black).enpassantProneColumn).toEqual(null);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e5"), new Game.Field("f6")), gameState)).toBe(false);
                });

                it("cannot enpassant from incorrect position", function() {
                    var chessboard = Game.ChessboardFactory.getInitialPosition();

                    var gameState = chessboard.applyMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")), new Game.GameState());
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("a7"), new Game.Field("a6")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("d2"), new Game.Field("d4")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("a6"), new Game.Field("a7")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("d4"), new Game.Field("d5")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("f7"), new Game.Field("f5")), gameState);

                    expect(gameState.forColor(Game.Color.black).enpassantProneColumn).toEqual(5);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f6")), gameState)).toBe(false);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d5"), new Game.Field("f6")), gameState)).toBe(false);
                });

                it("enpassant only after enemy two field-forward move", function() {
                    var chessboard = Game.ChessboardFactory.getInitialPosition();

                    var gameState = chessboard.applyMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")), new Game.GameState());
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("f7"), new Game.Field("f6")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("e4"), new Game.Field("e5")), gameState);
                    gameState = chessboard.applyMove(new Game.Move(new Game.Field("f6"), new Game.Field("f5")), gameState);

                    expect(gameState.forColor(Game.Color.black).enpassantProneColumn).toEqual(null);
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e5"), new Game.Field("f6")), gameState)).toBe(false);
                });

                it("be promoted to any piece", function() {});

                it("can be pinned", function() {});


                it("isAnyMovePossible can return true", function() {});


                it("isAnyMovePossible can return false", function() {});
        });

    }
);
