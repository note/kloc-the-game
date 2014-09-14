define(['field'], function(Field){
    var Move = function(from, to, promoteTo) {
        if(_.isEqual(from, to)){
            throw new Error("Move constructor's arguments must differ" + from + ", " + to);
        }

        this.from = from;
        this.to = to;
        if(promoteTo){
            this.promoteTo = promoteTo;
        }
    };

    Move.prototype.toString = function() {
        return "Move(" + from + ", " + to + ")";
    };

    Move.fromVector = function(startField, vector) {
        try{
            return new Move(startField, new Field(startField.column + vector.x, startField.row + vector.y));
        }catch(err){
            return undefined;
        }
    };

    Move.surrender = {};
    Move.proposeDraw = {
        drawPropositionRelated: true
    };
    Move.propositionAccepted = {
        drawPropositionRelated: true
    };
    Move.propositionRejected = {
        drawPropositionRelated: true
    };

    return Move;
});