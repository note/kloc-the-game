'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

        describe("A field", function() {
            it("can be constructed from human-readable notation", function() {
                var field = new Game.Field("a3");
                expect(field.row).toEqual(2);
                expect(field.column).toEqual(0);
            });

            it("cannot be constructed from ill-formed huamn-readable notation", function() {
                function createConstructor(fieldLiteral) {
                    return function() {
                        new Game.Field(fieldLiteral);
                    }
                }
                expect(createConstructor("a311")).toThrow();
                expect(createConstructor("j3")).toThrow();
                expect(createConstructor("a9")).toThrow();
            });
        });

    }
);

