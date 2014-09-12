'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){
        var white = Game.Color.white;
        var black = Game.Color.black;

        describe("Rook", function() {
            var chessboard;

            beforeEach(function(){
                chessboard= new Game.Chessboard();
            });

            it("can move vertically", function() {
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c1")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c7")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c8")))).toBe(true);
            });

            it("can move horizontally", function() {
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));

                var someLegalDestinations = ["a2", "b2", "d2", "h2"];
                someLegalDestinations.forEach(function(destination){
                    // TODO: use matchers
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field(destination)))).toBe(true);
                })
            });

            it("can take enemy piece", function() {
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c4")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c4")))).toBe(false);
            });

            it("cannot overleap", function() {
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("e2"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c5")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c8")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("f2")))).toBe(false);
            });

            it("cannot move other way than vertically and horizontally", function() {
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("b3")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("b4")))).toBe(false);
            });

            it("can be pinned", function() {
                chessboard.setPiece(new Game.Field("b8"), new Game.King(black));
                chessboard.setPiece(new Game.Field("c7"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("d6"), new Game.Bishop(white));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("b7")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("c1")))).toBe(false);
            });

            it("isAnyMovePossible can return true", function() {
                var rook = new Game.Rook(white);
                chessboard.setPiece(new Game.Field("a1"), rook);
                expect(rook.anyMovePossible(chessboard, new Game.Field("a1"))).toBe(true);
            });

            it("isAnyMovePossible can return false", function() {
                var rook = new Game.Rook(white);
                chessboard.setPiece(new Game.Field("a1"), rook);
                chessboard.setPiece(new Game.Field("a2"), new Game.Pawn(white));
                chessboard.setPiece(new Game.Field("b1"), new Game.Bishop(white));
                expect(rook.anyMovePossible(chessboard, new Game.Field("a1"))).toBe(false);
            });
        });

    }
);