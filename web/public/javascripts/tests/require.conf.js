/**
 * Created by michal on 08/09/14.
 */

console.log("bazinga2: " + EnvJasmine.specsDir);

require.config({
    baseUrl: EnvJasmine.rootDir,
    paths: {
        mocks:      EnvJasmine.mocksDir,
        specs:      EnvJasmine.specsDir,

        // Libraries
        // FIXME: we don't want to depend on jQuery necessarily, but for
        // now the sbt plugin requires it
        jquery:     'libs/jquery-1.11.1'
    }
});

console.log("again bazinga2: " + EnvJasmine.specsDir);