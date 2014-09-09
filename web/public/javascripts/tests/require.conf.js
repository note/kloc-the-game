/**
 * Created by michal on 08/09/14.
 */

require.config({
    baseUrl: EnvJasmine.rootDir,
    paths: {
        mocks:      EnvJasmine.mocksDir,
        specs:      EnvJasmine.specsDir,
        field:      'game-rules/field',
        piece:      'game-rules/piece',
        rook:       'game-rules/rook',
        bishop:     'game-rules/bishop',
        knight:     'game-rules/knight',
        queen:      'game-rules/queen',
        move:       'game-rules/move',
        color:      'game-rules/color',
        chessboard: 'game-rules/chessboard',
        game:       'game-rules/game',

        // Libraries
        // FIXME: we don't want to depend on jQuery necessarily, but for
        // now the sbt plugin requires it
        jquery:     'libs/jquery-1.11.1',
        underscore: 'libs/underscore-1.7.0'
    }
});
