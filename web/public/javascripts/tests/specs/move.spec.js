'use strict'

define(['game', 'underscore'],
    function(Game, _){

        describe("Move", function() {
            it("can be constructed", function(){
                var move = new Game.Move(new Game.Field("c2"), new Game.Field("c3"));
                expect(_.isEqual(move.from, new Game.Field("c2"))).toBe(true);
                expect(_.isEqual(move.to, new Game.Field("c3"))).toBe(true);
            });

            it("from and to fields must not be the same", function(){
                var constructIllegalMove = function() {
                    new Game.Move(new Game.Field("c2"), new Game.Field("c2"));
                };

                expect(constructIllegalMove).toThrow();
            });

            it("is incorrect when from field on chessboard is empty", function(){
                var chessboard = new Game.Chessboard();
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("a1"), new Game.Field("a2")))).toBe(false);
            });
        });

    }
);