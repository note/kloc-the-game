define(['underscore'], function(_){
    return {
        exists: function(list, fun, ctx) {
            var found = _.find(list, function(item){
                return fun.call(ctx, item);
            }, ctx);
            return found !== undefined;
        }
    };
});