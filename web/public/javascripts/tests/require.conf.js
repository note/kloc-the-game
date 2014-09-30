/**
 * Created by michal on 08/09/14.
 */

(function(){
    var baseUrl = window.EnvJasmine ? EnvJasmine.rootDir : "/assets/javascripts";

    var paths = {
        field:             'game-rules/field',
        piece:             'game-rules/piece',
        rook:              'game-rules/rook',
        bishop:            'game-rules/bishop',
        knight:            'game-rules/knight',
        queen:             'game-rules/queen',
        pawn:              'game-rules/pawn',
        king:              'game-rules/king',
        move:              'game-rules/move',
        color:             'game-rules/color',
        chessboardUtils:   'game-rules/chessboardUtils',
        chessboard:        'game-rules/chessboard',
        chessboardFactory: 'game-rules/chessboardFactory',
        gameState:         'game-rules/gameState',
        game:              'game-rules/game',
        funUtils:          'game-rules/funUtils',

        drawer:            'gui/drawer',

        // Libraries
        jquery:     'libs/jquery-1.11.1',
        underscore: 'libs/underscore-min',
        cookie:     'libs/jquery.cookie',
        sprintf:    'libs/sprintf.min',
        backbone:   'libs/backbone-min',
        vexDialog:  'libs/vex.dialog',
        vex:        'libs/vex.min'
    };

    if(window.EnvJasmine){
        paths.mocks = EnvJasmine.mocksDir;
        paths.specs = EnvJasmine.specsDir;
    }

    require.config({
        baseUrl: baseUrl,
        paths: paths
    });
})();
