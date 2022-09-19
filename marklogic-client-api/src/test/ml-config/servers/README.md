The previous TestServerBootstrapper class had a check to see if distribute-timestamps should be set to "cluster". 
This avoids read-after-write issues in a multi-host cluster. It's safe (though a little slower) to use this in any 
environment, so it's set to "cluster" by default. 
