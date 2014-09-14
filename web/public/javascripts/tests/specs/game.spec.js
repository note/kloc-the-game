'use strict'

define(['game', 'underscore'],
    function(Game, _){

        function applyMove(game, fromString, toString){
            var move = new Game.Move(new Game.Field(fromString), new Game.Field(toString));
            expect(game.isLegalMove(move)).toBe(true);
            game.applyMove(move);
        }

        describe("Game", function() {
            it("starts with white's move", function(){
                var game = new Game.ChessGame();
                expect(game.isLegalMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")))).toBe(true);
                expect(game.isLegalMove(new Game.Move(new Game.Field("b1"), new Game.Field("c3")))).toBe(true);

                expect(game.isLegalMove(new Game.Move(new Game.Field("e7"), new Game.Field("e5")))).toBe(false);
                expect(game.isLegalMove(new Game.Move(new Game.Field("b8"), new Game.Field("c6")))).toBe(false);
            });

            it("two consequent moves of one player is not allowed", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                expect(game.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e5")))).toBe(false);
                applyMove(game, "a7", "a5");
                expect(game.isLegalMove(new Game.Move(new Game.Field("a5"), new Game.Field("a4")))).toBe(false);
                applyMove(game, "e4", "e5");
                applyMove(game, "a5", "a4");
            });

            it("state is stored internally", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                applyMove(game, "a7", "a5");
                applyMove(game, "e4", "e5");
                applyMove(game, "d7", "d5");
                expect(game.isLegalMove(new Game.Move(new Game.Field("e5"), new Game.Field("d6")))).toBe(true);
                applyMove(game, "a2", "a3");
                applyMove(game, "b7", "b6");
                expect(game.isLegalMove(new Game.Move(new Game.Field("e5"), new Game.Field("d6")))).toBe(false);
            });

            it("can end with checkmate", function(){
                var game = new Game.ChessGame();
                applyMove(game, "f2", "f4");
                applyMove(game, "e7", "e5");
                applyMove(game, "g2", "g4");
                applyMove(game, "d8", "h4");
                expect(game.result).toEqual(Game.ChessGame.Result.blackWinner);
            });

            it("can end with stalemate", function(){
                var input =
                    ["xxxxxxxK",
                     "xxxxxxxP",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "kxxxxxxr"
                    ];
                var justBeforeStalematePosition = Game.ChessboardFactory.loadFromArray(input);

                var game = new Game.ChessGame(justBeforeStalematePosition, Game.Color.black);
                applyMove(game, "h1", "g1");
                expect(game.result).toEqual(Game.ChessGame.Result.draw);
            });

            it("no move can be done when result is set", function(){
                var game = new Game.ChessGame();
                game.setResult(Game.ChessGame.Result.draw);
                expect(game.isLegalMove(new Game.Move(new Game.Field("e2"), new Game.Field("e4")))).toBe(false);
                expect(game.isLegalMove(new Game.Move(new Game.Field("e7"), new Game.Field("e5")))).toBe(false);
            });

            it("white can resign", function(){
                var game = new Game.ChessGame();
                game.applyMove(Game.Move.surrender);
                expect(game.result).toEqual(Game.ChessGame.Result.blackWinner);
            });

            it("black can resign", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                game.applyMove(Game.Move.surrender);
                expect(game.result).toEqual(Game.ChessGame.Result.whiteWinner);
            });

            it("draw proposition can be accepted", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                game.applyMove(Game.Move.proposeDraw);
                game.applyMove(Game.Move.propositionAccepted);
                expect(game.result).toEqual(Game.ChessGame.Result.draw);
            });

            it("draw proposition can be rejected", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                game.applyMove(Game.Move.proposeDraw);
                game.applyMove(Game.Move.propositionRejected);
                expect(game.result).toEqual(null);
                applyMove(game, "e7", "e5");
                game.applyMove(Game.Move.propositionAccepted);
                expect(game.result).toEqual(null);
            });

            it("cannot accept when there was no proposition", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                game.applyMove(Game.Move.propositionAccepted);
                expect(game.result).toEqual(null);
            });

            it("applying any move means proposition rejection", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                game.applyMove(Game.Move.proposeDraw);
                applyMove(game, "e7", "e5");
                expect(game.result).toEqual(null);
            });

            it("draw proposition internal state is kept up to date", function(){
                var game = new Game.ChessGame();
                applyMove(game, "e2", "e4");
                game.applyMove(Game.Move.proposeDraw);
                applyMove(game, "e7", "e5");
                expect(game.result).toEqual(null);
                game.applyMove(Game.Move.propositionAccepted);
                expect(game.result).toEqual(null);
            });
        });
    }
);