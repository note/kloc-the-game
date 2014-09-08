'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

        describe("Bishop", function() {
            it("can move diagonally", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("h7")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f3")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("a8")))).toBe(true);
            });

            it("can take enemy piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(Game.Color.black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(Game.Color.black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(false);
            });

            it("cannot overleap", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(Game.Color.black));
                chessboard.setPiece(new Game.Field("d3"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("f3"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b1")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("g2")))).toBe(false);
            });

            it("cannot move other way diagonally", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Bishop(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e6")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b4")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c5")))).toBe(false);
            });

            it("can be pinned", function() {

            });

            it("isAnyMovePossible can return true", function() {

            });

            it("isAnyMovePossible can return false", function() {

            });
        });

    }
);