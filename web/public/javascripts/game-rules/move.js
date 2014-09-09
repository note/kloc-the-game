define(['underscore'], function(){
    var Move = function(from, to) {
        if(_.isEqual(from, to)){
            throw new Error("Move constructor's arguments must differ" + from + ", " + to);
        }

        this.from = from;
        this.to = to;
    };

    Move.prototype.toString = function() {
        return "Move(" + from + ", " + to + ")";
    };

    return Move;
});