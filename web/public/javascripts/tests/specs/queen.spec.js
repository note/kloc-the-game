'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

        describe("Queen", function() {
            it("can move vertically", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e1")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e8")))).toBe(true);
            });

            it("can move horizontally", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("a4")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("h4")))).toBe(true);
            });

            it("can move diagonally", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("h7")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f3")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("a8")))).toBe(true);
            });

            it("cannot move other way then diagonally", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("a3")))).toBe(false);
            });

            it("can take enemy piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(false);
            });

            it("cannot overleap", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Queen(Game.Color.black));
                chessboard.setPiece(new Game.Field("d3"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("e5"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c2")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e6")))).toBe(false);
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
