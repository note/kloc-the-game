'use strict'

define(['game', 'underscore'],
    function(Game, _){

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
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("a1"), new Game.King(Game.Color.white));
                chessboard.setPiece(new Game.Field("a4"), new Game.Queen(Game.Color.white));
                chessboard.setPiece(new Game.Field("a8"), new Game.Rook(Game.Color.black));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("a4"), new Game.Field("a2")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("a4"), new Game.Field("b4")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("a4"), new Game.Field("b3")))).toBe(false);
            });

            it("isAnyMovePossible can return true", function() {
                var chessboard = new Game.Chessboard();
                var queen = new Game.Queen(Game.Color.white);
                chessboard.setPiece(new Game.Field("a1"), queen);
                chessboard.setPiece(new Game.Field("a2"), new Game.Pawn(Game.Color.white));
                chessboard.setPiece(new Game.Field("b2"), new Game.Pawn(Game.Color.white));
                expect(queen.anyMovePossible(chessboard, new Game.Field("a1"))).toBe(true);
            });

            it("isAnyMovePossible can return false", function() {
                var chessboard = new Game.Chessboard();
                var queen = new Game.Queen(Game.Color.white);
                chessboard.setPiece(new Game.Field("a1"), queen);
                chessboard.setPiece(new Game.Field("a2"), new Game.Pawn(Game.Color.white));
                chessboard.setPiece(new Game.Field("b2"), new Game.Pawn(Game.Color.white));
                chessboard.setPiece(new Game.Field("b1"), new Game.Bishop(Game.Color.white));
                expect(queen.anyMovePossible(chessboard, new Game.Field("a1"))).toBe(false);
            });
        });

    }
);
