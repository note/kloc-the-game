/**
 * Created by michal on 08/09/14.
 */

require.config({
    baseUrl: EnvJasmine.rootDir,
    paths: {
        mocks:      EnvJasmine.mocksDir,
        specs:      EnvJasmine.specsDir,
        game:       'game-rules/chessboard',

        // Libraries
        // FIXME: we don't want to depend on jQuery necessarily, but for
        // now the sbt plugin requires it
        jquery:     'libs/jquery-1.11.1',
        underscore: 'libs/underscore-1.7.0'
    }
});
