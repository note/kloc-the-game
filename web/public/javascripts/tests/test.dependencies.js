/**
 * Created by michal on 08/09/14.
 */

// Require.js + config
console.log("hello world1: " + EnvJasmine.libDir);
console.log("hello world2: " + EnvJasmine.testDir);

EnvJasmine.loadGlobal(EnvJasmine.libDir + "require-2.1.15.js");
EnvJasmine.loadGlobal(EnvJasmine.testDir + "require.conf.js");

console.log("hello world3: " + EnvJasmine.testDir);