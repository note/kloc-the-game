'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){
        var white = Game.Color.white;
        var black = Game.Color.black;

        describe("Bishop", function() {
            var chessboard;

            beforeEach(function(){
                chessboard= new Game.Chessboard();
            });

            it("can move diagonally", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("h7")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f3")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("a8")))).toBe(true);
            });

            it("can take enemy piece", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(false);
            });

            it("cannot overleap", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(black));
                chessboard.setPiece(new Game.Field("d3"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("f3"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b1")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("g2")))).toBe(false);
            });

            it("cannot move other way then diagonally", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e6")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b4")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c5")))).toBe(false);
            });

            it("can be pinned", function() {
                chessboard.setPiece(new Game.Field("b8"), new Game.King(black));
                chessboard.setPiece(new Game.Field("c7"), new Game.Bishop(black));
                chessboard.setPiece(new Game.Field("f4"), new Game.Bishop(white));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("b6")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("a5")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("d6")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("e5")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("f4")))).toBe(true);
            });

            it("isAnyMovePossible can return true", function() {
                var bishop = new Game.Bishop(white);
                chessboard.setPiece(new Game.Field("h1"), bishop);
                chessboard.setPiece(new Game.Field("h2"), new Game.Pawn(white));
                expect(bishop.anyMovePossible(chessboard, new Game.Field("h1"))).toBe(true);
            });

            it("isAnyMovePossible can return false", function() {
                var bishop = new Game.Bishop(white);
                chessboard.setPiece(new Game.Field("h1"), bishop);
                chessboard.setPiece(new Game.Field("g2"), new Game.Pawn(white));
                expect(bishop.anyMovePossible(chessboard, new Game.Field("h1"))).toBe(false);
            });
        });

    }
);